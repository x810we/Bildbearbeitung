package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class MultiplyBlendMode {
    public static void main(String[] args) {
        try {
            // Zwei Bilder laden
            BufferedImage imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));
            BufferedImage imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));

            int width = Math.min(imageA.getWidth(), imageB.getWidth());
            int height = Math.min(imageA.getHeight(), imageB.getHeight());

            // Neues Bild für das Ergebnis erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Multiplizieren-Mischmodus auf jedes Pixel anwenden
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Pixel von Bild A und Bild B abrufen
                    Color colorA = new Color(imageA.getRGB(x, y));
                    Color colorB = new Color(imageB.getRGB(x, y));

                    // Rot, Grün, Blau Kanäle multiplizieren und auf den Bereich [0, 255] begrenzen
                    int r = (colorA.getRed() * colorB.getRed()) / 255;
                    int g = (colorA.getGreen() * colorB.getGreen()) / 255;
                    int b = (colorA.getBlue() * colorB.getBlue()) / 255;

                    // Alpha-Kanal berücksichtigen
                    int a = colorA.getAlpha(); // Alternativ könnte auch der Alpha-Wert gemischt werden

                    // Ergebnisfarbe erstellen und ins Ausgabe-Bild setzen
                    Color outputColor = new Color(r, g, b, a);
                    outputImage.setRGB(x, y, outputColor.getRGB());
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_multiply_blend.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
