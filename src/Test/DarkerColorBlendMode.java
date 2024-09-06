package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class DarkerColorBlendMode {
    public static void main(String[] args) {
        try {
            // Zwei Bilder laden
            BufferedImage imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));
            BufferedImage imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));

            int width = Math.min(imageA.getWidth(), imageB.getWidth());
            int height = Math.min(imageA.getHeight(), imageB.getHeight());

            // Neues Bild für das Ergebnis erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Dunklere Farben-Mischmodus auf jedes Pixel anwenden
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Pixel von Bild A und Bild B abrufen
                    Color colorA = new Color(imageA.getRGB(x, y), true);
                    Color colorB = new Color(imageB.getRGB(x, y), true);

                    // Luminanz berechnen (ein einfacher Ansatz für Luminanz)
                    double luminanceA = 0.299 * colorA.getRed() + 0.587 * colorA.getGreen() + 0.114 * colorA.getBlue();
                    double luminanceB = 0.299 * colorB.getRed() + 0.587 * colorB.getGreen() + 0.114 * colorB.getBlue();

                    // Dunklere Farbe wählen
                    Color outputColor;
                    if (luminanceA < luminanceB) {
                        outputColor = new Color(colorA.getRed(), colorA.getGreen(), colorA.getBlue(), colorA.getAlpha());
                    } else {
                        outputColor = new Color(colorB.getRed(), colorB.getGreen(), colorB.getBlue(), colorB.getAlpha());
                    }

                    // Ergebnisfarbe ins Ausgabe-Bild setzen
                    outputImage.setRGB(x, y, outputColor.getRGB());
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_darker_color_blend.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
