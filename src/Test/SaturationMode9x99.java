package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SaturationMode9x99 {

    // Methode zur Berechnung der Sättigung für zwei Bilder
    public static BufferedImage applySaturationMode(BufferedImage baseImage, BufferedImage blendImage) {
        int width = baseImage.getWidth();
        int height = baseImage.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color baseColor = new Color(baseImage.getRGB(x, y));
                Color blendColor = new Color(blendImage.getRGB(x, y));

                float[] baseHSB = Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), null);
                float[] blendHSB = Color.RGBtoHSB(blendColor.getRed(), blendColor.getGreen(), blendColor.getBlue(), null);

                // Sättigung des Blend-Bildes auf das Basis-Bild übertragen
                float resultSaturation = blendHSB[1]; // Sättigung von blendColor
                int rgbResult = Color.HSBtoRGB(baseHSB[0], resultSaturation, baseHSB[2]);

                resultImage.setRGB(x, y, rgbResult);
            }
        }
        return resultImage;
    }

    public static void main(String[] args) {
        try {
            // Einlesen der Bilder
            BufferedImage baseImage = ImageIO.read(new File("path/to/base_image.png"));
            BufferedImage blendImage = ImageIO.read(new File("path/to/blend_image.png"));

            // Anwenden des Sättigungsmodus
            BufferedImage resultImage = applySaturationMode(baseImage, blendImage);

            // Speichern des Ergebnisses
            ImageIO.write(resultImage, "png", new File("path/to/result_image.png"));
            System.out.println("Das Bild mit dem Sättigungsmodus wurde erfolgreich erstellt.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
