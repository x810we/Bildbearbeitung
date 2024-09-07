package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class HardLightBlendMode {
    public static void main(String[] args) {
        try {
            // Zwei Bilder laden
            BufferedImage imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));
            BufferedImage imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));

            int width = Math.min(imageA.getWidth(), imageB.getWidth());
            int height = Math.min(imageA.getHeight(), imageB.getHeight());

            // Neues Bild für das Ergebnis erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // "Hartes Licht"-Mischmodus auf jedes Pixel anwenden
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Pixel von Bild A und Bild B abrufen
                    Color colorA = new Color(imageA.getRGB(x, y), true);
                    Color colorB = new Color(imageB.getRGB(x, y), true);

                    // RGB-Kanäle von beiden Bildern normalisieren (zwischen 0 und 1)
                    double[] hardLightResult = new double[3];  // Für R, G und B
                    for (int i = 0; i < 3; i++) {
                        double valueA = normalize(colorA.getRGB() >> (16 - 8 * i) & 0xFF);  // Rot, Grün oder Blau extrahieren
                        double valueB = normalize(colorB.getRGB() >> (16 - 8 * i) & 0xFF);

                        // Hartes Licht-Formel anwenden
                        if (valueA < 0.5) {
                            hardLightResult[i] = 2 * valueA * valueB;
                        } else {
                            hardLightResult[i] = 1 - 2 * (1 - valueA) * (1 - valueB);
                        }
                    }

                    // Ergebniswerte wieder in 0-255 Bereich umwandeln
                    int red = denormalize(hardLightResult[0]);
                    int green = denormalize(hardLightResult[1]);
                    int blue = denormalize(hardLightResult[2]);

                    // Alpha-Wert beibehalten
                    int alpha = colorA.getAlpha();

                    // Ergebnisfarbe ins Ausgabe-Bild setzen
                    Color outputColor = new Color(red, green, blue, alpha);
                    outputImage.setRGB(x, y, outputColor.getRGB());
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_hard_light_blend.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hilfsfunktionen zur Normalisierung und Denormalisierung der RGB-Werte
    private static double normalize(int value) {
        return value / 255.0;
    }

    private static int denormalize(double value) {
        return (int) Math.round(value * 255);
    }
}
