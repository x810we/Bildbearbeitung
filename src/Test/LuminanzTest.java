package Test;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.lang.Math;

public class LuminanzTest {
    public static void main(String[] args) {
        try {
            // Originalbild laden
            //BufferedImage originalImage = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));
            //BufferedImage originalImage2 = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));

            BufferedImage originalImage = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));
            BufferedImage originalImage2 = ImageIO.read(new File("/Users/x810we/Pictures/FB/Farbchart.png"));

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // Ausgabe-Bild erstellen
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Durchlaufe jedes Pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Farbe des Originalbildes holen
                    int orgbOriginal = originalImage.getRGB(x, y);
                    Color colorOriginal = new Color(orgbOriginal);

                    int rgbOriginal2 = originalImage2.getRGB(x, y);
                    Color colorOriginal2 = new Color(rgbOriginal2);

                    // Graufläche-Farbe erstellen (RGB = 128, 128, 128)
                    Color grayColor = new Color(colorOriginal2.getRed(), colorOriginal2.getGreen(), colorOriginal2.getBlue());

                    // Farbton und Sättigung des Originalbildes
                    float[] hsbOriginal = Color.RGBtoHSB(colorOriginal.getRed(), colorOriginal.getGreen(), colorOriginal.getBlue(), null);

                    // Luminanz von der Graufläche
                    float[] hsbGray = Color.RGBtoHSB(grayColor.getRed(), grayColor.getGreen(), grayColor.getBlue(), null);

                    int p = originalImage.getRGB(x,y);
                    int a = (p>>24)&0xff;
                    int r = (p>>16)&0xff;
                    int g = (p>>8)&0xff;
                    int b = p&0xff;

                    int p2 = originalImage2.getRGB(x,y);
                    int a2 = (p2>>24)&0xff;
                    int r2 = (p2>>16)&0xff;
                    int g2 = (p2>>8)&0xff;
                    int b2 = p2&0xff;

                    float Luminanz = (float) (0.2126 * r2 + 0.7152 * g2 + 0.0722 * b2);
                    float LuminanzNormiert = 1.0f/255.0f *Luminanz;
                    if (LuminanzNormiert >= 1f)  {hsbOriginal[0] = 0; hsbOriginal[1] = 0;}

                    // Kombinierte HSB-Werte: Farbton und Sättigung vom Original, Luminanz von der Graufläche
                    float absWert = (1.0f - (hsbGray[2] - 0.0f));
                    if (absWert < 0f)  {absWert = 0f;}

                    //int rgbCombined = Color.HSBtoRGB(hsbGray[0], hsbOriginal[1],hsbOriginal[2]);
                    //int rgbCombined = Color.HSBtoRGB(hsbGray[0], hsbOriginal[1], hsbOriginal[2]);
                    int rgbCombined = Color.HSBtoRGB(hsbOriginal[0], hsbOriginal[1] * (1f - LuminanzNormiert), LuminanzNormiert);
                    //int rgbCombined = Color.HSBtoRGB(hsbGray[0], hsbGray[1] * (1.0f - (hsbOriginal[2] - 0.0f)), hsbOriginal[2]);
                    //int rgbCombined = Color.HSBtoRGB(Math.abs(hsbGray[0]-hsbOriginal[0]), 0, hsbOriginal[2]);
                    //int rgbCombined = Color.HSBtoRGB(0,0, hsbOriginal[2]);)
                    // In das Ausgabebild schreiben
                    int a3 = (rgbCombined>>24)&0xff;
                    int r3 = (rgbCombined>>16)&0xff;
                    int g3 = (rgbCombined>>8)&0xff;
                    int b3 = rgbCombined&0xff;
                    outputImage.setRGB(x, y, rgbCombined);

                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/output_imageLuminance.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}