package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SaturationModeWithHSL {

    // Methode zur Konvertierung von RGB nach HSL
    public static float[] rgbToHSL(int r, int g, int b) {
        float rNorm = r / 255f;
        float gNorm = g / 255f;
        float bNorm = b / 255f;

        float max = Math.max(rNorm, Math.max(gNorm, bNorm));
        float min = Math.min(rNorm, Math.min(gNorm, bNorm));
        float delta = max - min;

        // Berechne den Farbton (Hue)
        float hue = 0;
        if (delta != 0) {
            if (max == rNorm) {
                hue = (gNorm - bNorm) / delta + (gNorm < bNorm ? 6 : 0);
            } else if (max == gNorm) {
                hue = (bNorm - rNorm) / delta + 2;
            } else {
                hue = (rNorm - gNorm) / delta + 4;
            }
            hue /= 6;
        }

        // Berechne die Helligkeit (Lightness)
        float lightness = (max + min) / 2;

        // Berechne die Sättigung (Saturation)
        float saturation = 0;
        if (delta != 0) {
            saturation = delta / (1 - Math.abs(2 * lightness - 1));
        }

        return new float[]{hue * 360, saturation * 100, lightness * 100};
    }

    // Methode zur Konvertierung von HSL nach RGB
    public static int hslToRGB(float h, float s, float l) {
        s /= 100;
        l /= 100;

        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;

        float r = 0, g = 0, b = 0;
        if (0 <= h && h < 60) {
            r = c;
            g = x;
        } else if (60 <= h && h < 120) {
            r = x;
            g = c;
        } else if (120 <= h && h < 180) {
            g = c;
            b = x;
        } else if (180 <= h && h < 240) {
            g = x;
            b = c;
        } else if (240 <= h && h < 300) {
            r = x;
            b = c;
        } else if (300 <= h && h < 360) {
            r = c;
            b = x;
        }

        int rInt = Math.round((r + m) * 255);
        int gInt = Math.round((g + m) * 255);
        int bInt = Math.round((b + m) * 255);

        return (rInt << 16) | (gInt << 8) | bInt;
    }

    // Sättigungsmodus anwenden (HSL-Farbraum)
    public static Color applySaturationMode(Color baseColor, Color blendColor) {
        // RGB-Werte des Basis- und Blend-Pixels
        int baseR = baseColor.getRed();
        int baseG = baseColor.getGreen();
        int baseB = baseColor.getBlue();

        int blendR = blendColor.getRed();
        int blendG = blendColor.getGreen();
        int blendB = blendColor.getBlue();

        // Konvertiere beide Farben nach HSL
        float[] baseHSL = rgbToHSL(baseR, baseG, baseB);
        float[] blendHSL = rgbToHSL(blendR, blendG, blendB);

        // Ersetze die Sättigung der Basisfarbe durch die Sättigung der Blendfarbe
        float newHue = baseHSL[0];           // Behalte den Farbton der Basisfarbe
        float newSaturation = blendHSL[1];   // Übernehme die Sättigung der Blendfarbe
        float newLightness = baseHSL[2];     // Behalte die Helligkeit der Basisfarbe

        // Konvertiere das Ergebnis zurück in RGB
        int newRGB = hslToRGB(newHue, newSaturation, newLightness);
        return new Color(newRGB);
    }

    // Methode zur Anwendung des Sättigungsmodus auf zwei Bilddateien
    public static BufferedImage applySaturationModeToImages(BufferedImage baseImage, BufferedImage blendImage) {
        int width = Math.min(baseImage.getWidth(), blendImage.getWidth());
        int height = Math.min(baseImage.getHeight(), blendImage.getHeight());
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Wende den Sättigungsmodus für jedes Pixel an
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
            // Lade das Farbchart und das Grauchart als Bilddateien
            File baseImageFile = new File("path_to_your_farbchart_image.png");
            File blendImageFile = new File("path_to_your_grauchart_image.png");

            BufferedImage baseImage = ImageIO.read(baseImageFile);
            BufferedImage blendImage = ImageIO.read(blendImageFile);

            // Wende den Sättigungsmodus an
            BufferedImage resultImage = applySaturationModeToImages(baseImage, blendImage);

            // Speichere das Ergebnis als neue Bilddatei
            File outputfile = new File("path_to_save_output_image.png");
            ImageIO.write(resultImage, "png", outputfile);

            System.out.println("Sättigungsmodus erfolgreich angewendet und Bild gespeichert!");

        } catch (Exception e) {
            System.err.println("Fehler beim Verarbeiten der Bilder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
