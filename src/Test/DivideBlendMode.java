package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class DivideBlendMode {

    public static void main(String[] args) {
        try {
            // Zwei Bilder laden
            BufferedImage imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));
            BufferedImage imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));

            int width = Math.min(imageA.getWidth(), imageB.getWidth());
            int height = Math.min(imageA.getHeight(), imageB.getHeight());

            // Neues Bild für das Ergebnis erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Dividieren-Mischmodus auf jedes Pixel anwenden
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Pixel von Bild A und Bild B abrufen
                    Color colorA = new Color(imageA.getRGB(x, y), true);
                    Color colorB = new Color(imageB.getRGB(x, y), true);

                    // RGB-Kanäle berechnen
                    int red = divideChannel(colorA.getRed(), colorB.getRed());
                    int green = divideChannel(colorA.getGreen(), colorB.getGreen());
                    int blue = divideChannel(colorA.getBlue(), colorB.getBlue());

                    // Alpha-Wert beibehalten
                    int alpha = colorA.getAlpha();

                    // Ergebnisfarbe ins Ausgabe-Bild setzen
                    Color outputColor = new Color(red, green, blue, alpha);
                    outputImage.setRGB(x, y, outputColor.getRGB());
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_divide_blend.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funktion, um den Dividieren-Effekt auf einen Farbkanal anzuwenden
    private static int divideChannel(int valueA, int valueB) {
        if (valueB == 0) {
            return 255; // Division durch 0 verhindern
        }
        int result = (255 * valueA) / (valueB + 1); // +1, um Division durch 0 zu vermeiden
        return Math.min(255, result); // Sicherstellen, dass der Wert im Bereich 0 bis 255 bleibt
    }
}
