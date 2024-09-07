package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ColorDodgeBlendMode {
    public static void main(String[] args) {
        try {
            // Zwei Bilder laden
            BufferedImage imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));
            BufferedImage imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));

            int width = Math.min(imageA.getWidth(), imageB.getWidth());
            int height = Math.min(imageA.getHeight(), imageB.getHeight());

            // Neues Bild für das Ergebnis erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Negativ Multiplizieren-Mischmodus auf jedes Pixel anwenden
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Pixel von Bild A und Bild B abrufen
                    Color colorA = new Color(imageA.getRGB(x, y), true);
                    Color colorB = new Color(imageB.getRGB(x, y), true);

                    // RGB-Kanäle von 0-255 in den Bereich 0-1 normalisieren
                    double rA = colorA.getRed() / 255.0;
                    double gA = colorA.getGreen() / 255.0;
                    double bA = colorA.getBlue() / 255.0;

                    double rB = colorB.getRed() / 255.0;
                    double gB = colorB.getGreen() / 255.0;
                    double bB = colorB.getBlue() / 255.0;

                    // Negativ Multiplizieren-Blending (Color Dodge)
                    int red = (int) Math.min(255, rB / (1.0 - rA) * 255);
                    int green = (int) Math.min(255, gB / (1.0 - gA) * 255);
                    int blue = (int) Math.min(255, bB / (1.0 - bA) * 255);

                    // Alpha-Wert beibehalten
                    int alpha = colorA.getAlpha();

                    // Ergebnisfarbe ins Ausgabe-Bild setzen
                    Color outputColor = new Color(red, green, blue, alpha);
                    outputImage.setRGB(x, y, outputColor.getRGB());
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_color_dodge_blend.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
