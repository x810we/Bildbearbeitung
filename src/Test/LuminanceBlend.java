package Test;



import java.awt.Color;
        import java.awt.image.BufferedImage;
        import java.io.File;
        import javax.imageio.ImageIO;

public class LuminanceBlend {
    public static void main(String[] args) {
        try {
            // Originalbild laden
            BufferedImage originalImage = ImageIO.read(new File("/Users/x810we/Pictures/Sophie2024.png"));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // Ausgabe-Bild erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Durchlaufe jedes Pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Farbe des Originalbildes holen
                    int rgbOriginal = originalImage.getRGB(x, y);
                    Color colorOriginal = new Color(rgbOriginal);

                    // Graufläche-Farbe erstellen (RGB = 128, 128, 128)
                    Color grayColor = new Color(128, 128, 128);

                    // Farbton und Sättigung des Originalbildes
                    float[] hsbOriginal = Color.RGBtoHSB(colorOriginal.getRed(), colorOriginal.getGreen(), colorOriginal.getBlue(), null);

                    // Luminanz von der Graufläche
                    float[] hsbGray = Color.RGBtoHSB(grayColor.getRed(), grayColor.getGreen(), grayColor.getBlue(), null);

                    // Kombinierte HSB-Werte: Farbton und Sättigung vom Original, Luminanz von der Graufläche
                    int rgbCombined = Color.HSBtoRGB(hsbOriginal[0], hsbOriginal[1], hsbGray[2]);

                    // In das Ausgabebild schreiben
                    outputImage.setRGB(x, y, rgbCombined);
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/output_imageLuminance.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}