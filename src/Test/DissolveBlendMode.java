package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Random;

public class DissolveBlendMode {
    public static void main(String[] args) {
        try {
            // Zwei Bilder laden
            BufferedImage imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));
            BufferedImage imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));

            int width = Math.min(imageA.getWidth(), imageB.getWidth());
            int height = Math.min(imageA.getHeight(), imageB.getHeight());

            // Neues Bild für das Ergebnis erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Random random = new Random();

            // Sprenkel-Mischmodus auf jedes Pixel anwenden
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Pixel von Bild A und Bild B abrufen
                    Color colorA = new Color(imageA.getRGB(x, y), true);
                    Color colorB = new Color(imageB.getRGB(x, y), true);

                    // Alpha-Wert von Bild A auslesen
                    int alphaA = colorA.getAlpha();

                    // Zufälligen Wert zwischen 0 und 255 erzeugen
                    int randomValue = random.nextInt(256);

                    Color outputColor;
                    if (randomValue < alphaA) {
                        // Wenn der Zufallswert kleiner als der Alpha-Wert ist, nimm das obere Bild
                        outputColor = new Color(colorA.getRed(), colorA.getGreen(), colorA.getBlue(), colorA.getAlpha());
                    } else {
                        // Ansonsten nimm das untere Bild
                        outputColor = new Color(colorB.getRed(), colorB.getGreen(), colorB.getBlue(), colorB.getAlpha());
                    }

                    if (x == 3 && y == 5) {  // Beispiel: Rotes Pixel an Position (3,5)
                        //colorA = new Color(colorA.getRed(), colorA.getGreen(), colorA.getBlue(), 128);  // Setzt Alpha auf 128
                        outputColor = new Color(255, 0, 0, 128);
                   }
                    // Ergebnisfarbe ins Ausgabe-Bild setzen
                    outputImage.setRGB(x, y, outputColor.getRGB());
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_dissolve_blend.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
