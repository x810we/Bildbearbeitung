package Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaturationBlendMode {

    // Methode zur Umwandlung von RGB nach HSL
    public static float[] rgbToHsl(Color color) {
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float hue = 0;
        float saturation = 0;
        float luminance = (max + min) / 2;

        if (delta != 0) {
            saturation = (luminance > 0.5) ? delta / (2 - max - min) : delta / (max + min);

            if (max == r) {
                hue = (g - b) / delta + (g < b ? 6 : 0);
            } else if (max == g) {
                hue = (b - r) / delta + 2;
            } else {
                hue = (r - g) / delta + 4;
            }

            hue /= 6;
        }

        return new float[]{hue, saturation, luminance};
    }

    // Methode zur Umwandlung von HSL nach RGB
    public static Color hslToRgb(float[] hsl) {
        float hue = hsl[0];
        float saturation = hsl[1];
        float luminance = hsl[2];

        float r, g, b;

        if (saturation == 0) {
            r = g = b = luminance; // Grauwerte
        } else {
            float q = (luminance < 0.5) ? luminance * (1 + saturation) : luminance + saturation - luminance * saturation;
            float p = 2 * luminance - q;
            r = hueToRgb(p, q, hue + 1f / 3f);
            g = hueToRgb(p, q, hue);
            b = hueToRgb(p, q, hue - 1f / 3f);
        }

        return new Color(clamp(r * 255), clamp(g * 255), clamp(b * 255));
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1f / 6f) return p + (q - p) * 6 * t;
        if (t < 1f / 2f) return q;
        if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6;
        return p;
    }

    private static int clamp(float value) {
        return Math.max(0, Math.min(255, Math.round(value)));
    }

    // Methode zum Anwenden des Sättigungsblendmodus
    public static BufferedImage applySaturationBlend(BufferedImage imageA, BufferedImage imageB) {
        int width = Math.min(imageA.getWidth(), imageB.getWidth());
        int height = Math.min(imageA.getHeight(), imageB.getHeight());

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color colorA = new Color(imageA.getRGB(x, y), true);
                Color colorB = new Color(imageB.getRGB(x, y), true);

                // RGB in HSL für beide Bilder umwandeln
                float[] hslA = rgbToHsl(colorA);
                float[] hslB = rgbToHsl(colorB);

                // Farbton und Helligkeit von B, aber Sättigung von A
                float hueB = hslB[0];
                float luminanceB = hslB[2];
                float saturationA = hslA[1];

                float[] hslResult = new float[]{hueB, saturationA, luminanceB};

                // HSL in RGB zurück umwandeln
                Color outputColor = hslToRgb(hslResult);

                outputImage.setRGB(x, y, outputColor.getRGB());
            }
        }

        return outputImage;
    }

    // Test-Methode
    public static void main(String[] args) throws IOException {
        // Hier kannst du deine Bilder laden und den Modus anwenden
        BufferedImage imageA = null;
        try {
            imageA = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedImage imageB = null;
        try {
            imageB = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage resultImage = applySaturationBlend(imageA, imageB);
        ImageIO.write(resultImage, "png", new File("/Users/x810we/Pictures/FB/output_saturation_blend2.png"));

        // Speichere oder zeige das resultierende Bild an
    }
}
