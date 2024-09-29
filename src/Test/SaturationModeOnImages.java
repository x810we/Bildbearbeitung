package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SaturationModeOnImages {

    // Methode zur Konvertierung von RGB in HSB
    public static float[] rgbToHSB(int r, int g, int b) {
        return Color.RGBtoHSB(r, g, b, null);
    }

    // Methode zur Konvertierung von HSB in RGB
    public static int hsbToRGB(float hue, float saturation, float brightness) {
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    // Sättigungsmodus auf zwei Pixel anwenden
    public static Color applySaturationMode(Color baseColor, Color blendColor) {
        // Basisfarben (untere Ebene)
        int baseR = baseColor.getRed();
        int baseG = baseColor.getGreen();
        int baseB = baseColor.getBlue();

        // Blendfarben (obere Ebene)
        int blendR = blendColor.getRed();
        int blendG = blendColor.getGreen();
        int blendB = blendColor.getBlue();

        // Farben in HSB umwandeln
        float[] baseHSB = rgbToHSB(baseR, baseG, baseB);
        float[] blendHSB = rgbToHSB(blendR, blendG, blendB);

        // Behalte den Farbton und die Helligkeit des Basispixels und verwende die Sättigung des Blendpixels
        float newHue = baseHSB[0];
        float newSaturation = blendHSB[1];  // Sättigung der Blend-Ebene verwenden
        float newBrightness = baseHSB[2];

        // Konvertiere das Ergebnis in RGB
        int newRGB = hsbToRGB(newHue, newSaturation, newBrightness);
        return new Color(newRGB);
    }

    // Methode zur Anwendung des Sättigungsmodus auf zwei Bilddateien
    public static BufferedImage applySaturationModeToImages(BufferedImage baseImage, BufferedImage blendImage) {
        int width = Math.min(baseImage.getWidth(), blendImage.getWidth());
        int height = Math.min(baseImage.getHeight(), blendImage.getHeight());
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Sättigungsmodus auf jedes Pixel anwenden
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color baseColor = new Color(baseImage.getRGB(x, y));
                Color blendColor = new Color(blendImage.getRGB(x, y));
                Color resultColor = applySaturationMode(baseColor, blendColor);
                resultImage.setRGB(x, y, resultColor.getRGB());
            }
        }

        return resultImage;
    }

    public static void main(String[] args) {
        try {
            // Lade das Farbchart und Grauchart als Bilddateien
            File baseImageFile = new File("path_to_your_farbchart_image.png");
            File blendImageFile = new File("path_to_your_grauchart_image.png");

            BufferedImage baseImage = ImageIO.read(baseImageFile);
            BufferedImage blendImage = ImageIO.read(blendImageFile);

            // Sättigungsmodus anwenden
            BufferedImage resultImage = applySaturationModeToImages(baseImage, blendImage);

            // Ergebnis als neues Bild speichern
            File outputfile = new File("path_to_save_output_image.png");
            ImageIO.write(resultImage, "png", outputfile);

            System.out.println("Sättigungsmodus wurde erfolgreich angewendet und das Bild wurde gespeichert!");

        } catch (Exception e) {
            System.err.println("Fehler beim Verarbeiten der Bilder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
