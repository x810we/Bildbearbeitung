package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class LuminosityBlendMode {
    public static void main(String[] args) {
        try {
            // Zwei Bilder laden
            BufferedImage imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));
            BufferedImage imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));

            int width = Math.min(imageA.getWidth(), imageB.getWidth());
            int height = Math.min(imageA.getHeight(), imageB.getHeight());

            // Neues Bild für das Ergebnis erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Luminanz-Mischmodus auf jedes Pixel anwenden
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Pixel von Bild A und Bild B abrufen
                    Color colorA = new Color(imageA.getRGB(x, y));
                    Color colorB = new Color(imageB.getRGB(x, y));

                    // Luminanzwert aus Bild A berechnen
                    float lumA = 0.2126f * colorA.getRed() + 0.7152f * colorA.getGreen() + 0.0722f * colorA.getBlue();

                    // Farbton und Sättigung aus Bild B verwenden und Luminanz von Bild A hinzufügen
                    int r = adjustLuminosity(colorB.getRed(), colorB.getGreen(), colorB.getBlue(), lumA);
                    int g = adjustLuminosity(colorB.getGreen(), colorB.getRed(), colorB.getBlue(), lumA);
                    int b = adjustLuminosity(colorB.getBlue(), colorB.getRed(), colorB.getGreen(), lumA);

                    // Alpha-Kanal berücksichtigen
                    int a = colorA.getAlpha(); // Alternativ könnte auch der Alpha-Wert gemischt werden

                    // Ergebnisfarbe erstellen und ins Ausgabe-Bild setzen
                    Color outputColor = new Color(r, g, b, a);
                    outputImage.setRGB(x, y, outputColor.getRGB());
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_luminosity_blend.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode zur Anpassung der Luminanz eines Farbkanals
    private static int adjustLuminosity(int color, int otherColor1, int otherColor2, float lumA) {
        float lumB = 0.2126f * color + 0.7152f * otherColor1 + 0.0722f * otherColor2;
        float newColor = color + (lumA - lumB);

        // Begrenzen des Farbwerts auf den Bereich [0, 255]
        return Math.min(Math.max((int) newColor, 0), 255);
    }
}
