package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class DifferenceBlendMode {

    public static void main(String[] args) {
        try {
            // Zwei Bilder laden
            BufferedImage imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));
            BufferedImage imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));


            int width = Math.min(imageA.getWidth(), imageB.getWidth());
            int height = Math.min(imageA.getHeight(), imageB.getHeight());

            // Neues Bild für das Ergebnis erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Differenz-Mischmodus auf jedes Pixel anwenden
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Pixel von Bild A und Bild B abrufen
                    Color colorA = new Color(imageA.getRGB(x, y), true);
                    Color colorB = new Color(imageB.getRGB(x, y), true);

                    // RGB-Kanäle mischen
                    int red = differenceChannel(colorA.getRed(), colorB.getRed());
                    int green = differenceChannel(colorA.getGreen(), colorB.getGreen());
                    int blue = differenceChannel(colorA.getBlue(), colorB.getBlue());

                    // Alpha-Wert beibehalten
                    int alpha = colorA.getAlpha();

                    // Ergebnisfarbe ins Ausgabe-Bild setzen
                    Color outputColor = new Color(red, green, blue, alpha);
                    outputImage.setRGB(x, y, outputColor.getRGB());
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_difference_blend.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funktion, um den Differenz-Effekt auf einen Farbkanal anzuwenden
    private static int differenceChannel(int valueA, int valueB) {
        return Math.abs(valueA - valueB);  // Differenz der Farbwerte
    }
}
