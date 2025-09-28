package Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CurvesLUT {
    public static void main(String[] args) throws IOException {
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        // Beispiel-S-Kurve definieren
        int[] lut = buildSCurveLUT();

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int a = (rgb >> 24) & 0xFF;
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8)  & 0xFF;
                int b = (rgb)       & 0xFF;

                // Kanal für Kanal über LUT remappen
                r = lut[r];
                g = lut[g];
                b = lut[b];

                int newRGB = (a << 24) | (r << 16) | (g << 8) | b;
                dst.setRGB(x, y, newRGB);
            }
        }

        System.out.println("Kurve angewandt – Datei: output_curved.png");
    }

    static int[] buildSCurveLUT() {
        int[] lut = new int[256];
        for (int i = 0; i < 256; i++) {
            double x = i / 255.0;
            // Beispiel: einfache S-Kurve via logistischer Funktion
            double y = 1.0 / (1.0 + Math.exp(-12 * (x - 0.5)));
           // double y = x;
            lut[i] = (int)Math.round(y * 255.0);
        }
        return lut;
    }
}
