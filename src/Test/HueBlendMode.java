package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class HueBlendMode {

    public static void main(String[] args) {
        try {
            // Zwei Bilder laden
            BufferedImage imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));
            BufferedImage imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));

            int width = Math.min(imageA.getWidth(), imageB.getWidth());
            int height = Math.min(imageA.getHeight(), imageB.getHeight());

            // Neues Bild für das Ergebnis erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Farbton-Mischmodus auf jedes Pixel anwenden
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Pixel von Bild A und Bild B abrufen
                    Color colorA = new Color(imageA.getRGB(x, y), true);
                    Color colorB = new Color(imageB.getRGB(x, y), true);

                    // RGB nach HSL umwandeln
                    float[] hslA = rgbToHsl(colorA);
                    float[] hslB = rgbToHsl(colorB);

                    // Behalte die Sättigung und Helligkeit von Bild B bei
                    hslA[1] = hslB[1]; // Sättigung von Bild B
                    hslA[2] = hslB[2]; // Helligkeit von Bild B

                    // HSL zurück nach RGB umwandeln
                    Color outputColor = hslToRgb(hslA);

                    // Ergebnisfarbe ins Ausgabe-Bild setzen
                    outputImage.setRGB(x, y, outputColor.getRGB());
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_hue_blend.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funktion, um RGB nach HSL umzurechnen
    private static float[] rgbToHsl(Color color) {
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h = 0f;
        if (delta != 0) {
            if (max == r) {
                h = (g - b) / delta;
            } else if (max == g) {
                h = 2f + (b - r) / delta;
            } else {
                h = 4f + (r - g) / delta;
            }
            h *= 60;
            if (h < 0) h += 360;
        }

        float l = (max + min) / 2;
        float s = delta == 0 ? 0 : delta / (1 - Math.abs(2 * l - 1));

        return new float[]{h, s, l};
    }

    // Funktion, um HSL zurück nach RGB zu wandeln
    private static Color hslToRgb(float[] hsl) {
        float h = hsl[0];
        float s = hsl[1];
        float l = hsl[2];

        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;

        float r = 0, g = 0, b = 0;
        if (h >= 0 && h < 60) {
            r = c;
            g = x;
        } else if (h >= 60 && h < 120) {
            r = x;
            g = c;
        } else if (h >= 120 && h < 180) {
            g = c;
            b = x;
        } else if (h >= 180 && h < 240) {
            g = x;
            b = c;
        } else if (h >= 240 && h < 300) {
            r = x;
            b = c;
        } else if (h >= 300 && h < 360) {
            r = c;
            b = x;
        }

        int red = (int) ((r + m) * 255);
        int green = (int) ((g + m) * 255);
        int blue = (int) ((b + m) * 255);

        return new Color(red, green, blue);
    }
}
