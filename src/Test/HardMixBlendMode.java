package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class HardMixBlendMode {

    public static void main(String[] args) {
        try {
            // Zwei Bilder laden
            BufferedImage imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));
            BufferedImage imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));

            int width = Math.min(imageA.getWidth(), imageB.getWidth());
            int height = Math.min(imageA.getHeight(), imageB.getHeight());

            // Neues Bild für das Ergebnis erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Hart mischen-Mischmodus auf jedes Pixel anwenden
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Pixel von Bild A und Bild B abrufen
                    Color colorA = new Color(imageA.getRGB(x, y), true);
                    Color colorB = new Color(imageB.getRGB(x, y), true);

                    // RGB-Kanäle mischen
                    int red = hardMixChannel(colorA.getRed(), colorB.getRed());
                    int green = hardMixChannel(colorA.getGreen(), colorB.getGreen());
                    int blue = hardMixChannel(colorA.getBlue(), colorB.getBlue());

                    // Alpha-Wert beibehalten
                    int alpha = colorA.getAlpha();

                    // Ergebnisfarbe ins Ausgabe-Bild setzen
                    Color outputColor = new Color(red, green, blue, alpha);
                    outputImage.setRGB(x, y, outputColor.getRGB());
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_hard_mix_blend.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funktion, um den Hart mischen-Effekt auf einen Farbkanal anzuwenden
    private static int hardMixChannel(int valueA, int valueB) {
        int result = valueA + valueB;
        return result >= 255 ? 255 : 0;  // Schwellwert bei 255
    }
}
