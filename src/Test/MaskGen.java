package Test;

/**
 * Java Luminanzmasken‑Generator
 *
 * Erzeugt aus einem Eingangsbild (beliebiges sRGB‑Bild) eine Graustufen‑PNG, die
 * als Luminanzmaske in Photoshop genutzt werden kann. Unterstützt mehrere Modi:
 *  - luminance  : echte Luminanz (sRGB → linear → Y → sRGB‑Graustufe)
 *  - constant   : konstante Graustufe (z.B. 50% = 128)
 *  - threshold  : binär: weiß wenn Luminanz ≥ T, sonst schwarz
 *  - range      : weiche „Mitteltöne“: 0 außerhalb [low, high], linearer Verlauf nach innen
 *  - lights     : helligkeitsbetonte Maske (Y^gammaBoost)
 *  - darks      : tiefenbetonte Maske (1 − (1 − Y)^gammaBoost)
 * Optional: invertiert Ausgabe.
 *
 * Build:   javac MaskGen.java
 * Run:     java MaskGen <input> <output.png> --mode <m> [--const 0..255] [--thr 0..1]
 *                                   [--low 0..1] [--high 0..1] [--gamma 0.0..6.0] [--invert]
 *
 * Beispiele:
 *  1) Echte Luminanzmaske:
 *     java MaskGen foto.jpg maske.png --mode luminance
 *
 *  2) „50% Graustufe“ als konstante Maske (gleichmäßige, halbdurchsichtige Wirkung):
 *     java MaskGen foto.jpg maske.png --mode constant --const 128
 *
 *  3) Helle Bereiche (≥ 50%) als Lights‑Maske, binär:
 *     java MaskGen foto.jpg maske.png --mode threshold --thr 0.5
 *
 *  4) Weiche Mitteltöne in [0.3, 0.7]:
 *     java MaskGen foto.jpg maske.png --mode range --low 0.3 --high 0.7
 *
 *  5) Lights/Darks mit anhebbarem Kontrast:
 *     java MaskGen foto.jpg lights.png --mode lights --gamma 1.5
 *     java MaskGen foto.jpg darks.png  --mode darks  --gamma 1.5
 *
 * Photoshop‑Import (kurz): Ebenenmaske auf Zielleben anlegen → Maskenthumbnail Alt/Option‑Klicken
 * → maske.png öffnen, Strg/Cmd‑A, Strg/Cmd‑C → in die Maske einfügen (Strg/Cmd‑V) → Fertig.
 */

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MaskGen {
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            usageAndExit();
        }
        String input = args[0];
        String output = args[1];
        String mode = null;
        int constGray = 128;     // 0..255
        double thr = 0.5;        // 0..1 (auf Luminanz)
        double low = 0.4, high = 0.6; // 0..1
        double gammaBoost = 1.0; // für lights/darks
        boolean invert = false;

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--mode": mode = args[++i]; break;
                case "--const": constGray = clamp255(Integer.parseInt(args[++i])); break;
                case "--thr": thr = clamp01(Double.parseDouble(args[++i])); break;
                case "--low": low = clamp01(Double.parseDouble(args[++i])); break;
                case "--high": high = clamp01(Double.parseDouble(args[++i])); break;
                case "--gamma": gammaBoost = Math.max(0.0, Double.parseDouble(args[++i])); break;
                case "--invert": invert = true; break;
                //default: usageAndExit();
            }
        }
        if (mode == null) usageAndExit();
        if (low > high) { double t = low; low = high; high = t; }

        BufferedImage src = ImageIO.read(new File(input));
        int w = src.getWidth();
        int h = src.getHeight();

        // Ausgabe: 8‑Bit Graustufen‑PNG (TYPE_BYTE_GRAY)
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = out.getRaster();
        byte[] row = new byte[w];

        // sRGB → Luminanz Y (linear‑light) pro Pixel berechnen
        // Formel: Y = 0.2126*R_lin + 0.7152*G_lin + 0.0722*B_lin  (Rec.709/sRGB)
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = src.getRGB(x, y);
                int r8 = (argb >> 16) & 0xFF;
                int g8 = (argb >> 8) & 0xFF;
                int b8 = (argb) & 0xFF;

                double r = srgbToLinear(r8 / 255.0);
                double g = srgbToLinear(g8 / 255.0);
                double b = srgbToLinear(b8 / 255.0);
                double Y = 0.2126 * r + 0.7152 * g + 0.0722 * b; // 0..1, linear‑light

                double m; // 0..1 Maskenwert je nach Modus
                switch (mode.toLowerCase()) {
                    case "luminance":
                        // echte Luminanz als Grauwert (wieder in sRGB kodieren für visuelle Linearität)
                        m = linearToSrgb(Y);
                        break;
                    case "constant":
                        m = constGray / 255.0;
                        break;
                    case "threshold":
                        m = (Y >= thr) ? 1.0 : 0.0;
                        break;
                    case "range":
                        // Dreiecksprofil: 0 außerhalb [low, high], innen linear hoch/runter
                        if (Y <= low || Y >= high) {
                            m = 0.0;
                        } else {
                            double mid = (low + high) * 0.5;
                            if (Y <= mid) m = (Y - low) / (mid - low);
                            else m = (high - Y) / (high - mid);
                            m = Math.max(0.0, Math.min(1.0, m));
                        }
                        break;
                    case "lights":
                        // betont helle Tonwerte (sanft): m = (Y)^gammaBoost in sRGB‑Wahrnehmung
                        m = Math.pow(linearToSrgb(Y), Math.max(1e-6, gammaBoost));
                        break;
                    case "darks":
                        // betont dunkle Tonwerte: m = 1 − (1 − Y_srgb)^gammaBoost
                        double Ys = linearToSrgb(Y);
                        m = 1.0 - Math.pow(1.0 - Ys, Math.max(1e-6, gammaBoost));
                        break;
                    default:
                        throw new IllegalArgumentException("Unbekannter --mode: " + mode);
                }
                if (invert) m = 1.0 - m;
                row[x] = (byte) (int) Math.round(m * 255.0);
            }
            wr.setDataElements(0, y, w, 1, row);
        }

        // Schreiben als PNG
        ImageIO.write(out, "png", new File(output));
        System.out.println("Maske geschrieben: " + output + " (" + w + "x" + h + ")");
    }

    private static double clamp01(double v) { return Math.max(0.0, Math.min(1.0, v)); }
    private static int clamp255(int v) { return Math.max(0, Math.min(255, v)); }

    /**
     * sRGB → linear‑light (EOTF), gemäß sRGB‑Standard.
     */
    public static double srgbToLinear(double c) {
        if (c <= 0.04045) return c / 12.92;
        return Math.pow((c + 0.055) / 1.055, 2.4);
    }

    /**
     * linear‑light → sRGB (OETF), inverse zu srgbToLinear.
     */
    public static double linearToSrgb(double c) {
        if (c <= 0.0031308) return 12.92 * c;
        return 1.055 * Math.pow(c, 1.0 / 2.4) - 0.055;
    }

    private static void usageAndExit() {
        System.err.println("Usage: java MaskGen <input> <output.png> --mode <luminance|constant|threshold|range|lights|darks> " +
                "[--const 0..255] [--thr 0..1] [--low 0..1] [--high 0..1] [--gamma 0..6] [--invert]");
        System.exit(1);
    }
}
