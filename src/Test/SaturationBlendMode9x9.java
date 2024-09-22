package Test;
import java.awt.Color;

public class SaturationBlendMode9x9 {

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

    // Beispiel: Sättigungsmodus auf ein 9x9 Bild (Farbschema gegen Graustufen) anwenden
    public static void applySaturationModeTo9x9(Color[][] baseImage, Color[][] blendImage) {
        Color[][] resultImage = new Color[9][9];

        // Sättigungsmodus auf jedes Pixel anwenden
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                resultImage[i][j] = applySaturationMode(baseImage[i][j], blendImage[i][j]);
            }
        }

        // Ausgabe der neuen Farben (Beispielweise Hex-Werte)
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.printf("%06X ", resultImage[i][j].getRGB() & 0xFFFFFF);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        // Beispiel-Farben für das 9x9 Farbchart (HSL: Weiss, Grau, Gelb, Grün, Lila, Rot, Blau, Schwarz)
        Color[][] baseImage = {
                {new Color(255, 255, 255), new Color(128, 128, 128), new Color(255, 255, 0), new Color(0, 255, 255), new Color(0, 255, 0), new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 0, 255), new Color(0, 0, 0)},
                {new Color(255, 255, 255), new Color(128, 128, 128), new Color(255, 255, 0), new Color(0, 255, 255), new Color(0, 255, 0), new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 0, 255), new Color(0, 0, 0)},
                {new Color(255, 255, 255), new Color(128, 128, 128), new Color(255, 255, 0), new Color(0, 255, 255), new Color(0, 255, 0), new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 0, 255), new Color(0, 0, 0)},
                {new Color(255, 255, 255), new Color(128, 128, 128), new Color(255, 255, 0), new Color(0, 255, 255), new Color(0, 255, 0), new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 0, 255), new Color(0, 0, 0)},
                {new Color(255, 255, 255), new Color(128, 128, 128), new Color(255, 255, 0), new Color(0, 255, 255), new Color(0, 255, 0), new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 0, 255), new Color(0, 0, 0)},
                {new Color(255, 255, 255), new Color(128, 128, 128), new Color(255, 255, 0), new Color(0, 255, 255), new Color(0, 255, 0), new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 0, 255), new Color(0, 0, 0)},
                {new Color(255, 255, 255), new Color(128, 128, 128), new Color(255, 255, 0), new Color(0, 255, 255), new Color(0, 255, 0), new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 0, 255), new Color(0, 0, 0)},
                {new Color(255, 255, 255), new Color(128, 128, 128), new Color(255, 255, 0), new Color(0, 255, 255), new Color(0, 255, 0), new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 0, 255), new Color(0, 0, 0)},
                {new Color(255, 255, 255), new Color(128, 128, 128), new Color(255, 255, 0), new Color(0, 255, 255), new Color(0, 255, 0), new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 0, 255), new Color(0, 0, 0)}
        };

        // Beispiel für das 9x9 Graustufen-Chart (HSL: Schwarz zu Weiß)
        Color[][] blendImage = {
                {new Color(0, 0, 0), new Color(38, 38, 38), new Color(77, 77, 77), new Color(102, 102, 102), new Color(128, 128, 128), new Color(166, 166, 166), new Color(204, 204, 204), new Color(230, 230, 230), new Color(255, 255, 255)},
                {new Color(0, 0, 0), new Color(38, 38, 38), new Color(77, 77, 77), new Color(102, 102, 102), new Color(128, 128, 128), new Color(166, 166, 166), new Color(204, 204, 204), new Color(230, 230, 230), new Color(255, 255, 255)},
                {new Color(0, 0, 0), new Color(38, 38, 38), new Color(77, 77, 77), new Color(102, 102, 102), new Color(128, 128, 128), new Color(166, 166, 166), new Color(204, 204, 204), new Color(230, 230, 230), new Color(255, 255, 255)},
                {new Color(0, 0, 0), new Color(38, 38, 38), new Color(77, 77, 77), new Color(102, 102, 102), new Color(128, 128, 128), new Color(166, 166, 166), new Color(204, 204, 204), new Color(230, 230, 230), new Color(255, 255, 255)},
                {new Color(0, 0, 0), new Color(38, 38, 38), new Color(77, 77, 77), new Color(102, 102, 102), new Color(128, 128, 128), new Color(166, 166, 166), new Color(204, 204, 204), new Color(230, 230, 230), new Color(255, 255, 255)},
                {new Color(0, 0, 0), new Color(38, 38, 38), new Color(77, 77, 77), new Color(102, 102, 102), new Color(128, 128, 128), new Color(166, 166, 166), new Color(204, 204, 204), new Color(230, 230, 230), new Color(255, 255, 255)},
                {new Color(0, 0, 0), new Color(38, 38, 38), new Color(77, 77, 77), new Color(102, 102, 102), new Color(128, 128, 128), new Color(166, 166, 166), new Color(204, 204, 204), new Color(230, 230, 230), new Color(255, 255, 255)},
                {new Color(0, 0, 0), new Color(38, 38, 38), new Color(77, 77, 77), new Color(102, 102, 102), new Color(128, 128, 128), new Color(166, 166, 166), new Color(204, 204, 204), new Color(230, 230, 230), new Color(255, 255, 255)},
                {new Color(0, 0, 0), new Color(38, 38, 38), new Color(77, 77, 77), new Color(102, 102, 102), new Color(128, 128, 128), new Color(166, 166, 166), new Color(204, 204, 204), new Color(230, 230, 230), new Color(255, 255, 255)},
        };
    }
}