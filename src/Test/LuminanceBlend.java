package Test;



import java.awt.Color;
        import java.awt.image.BufferedImage;
        import java.io.File;
        import javax.imageio.ImageIO;


public class LuminanceBlend {
    public static void main(String[] args) {
        try {
            // Originalbild laden
            BufferedImage originalImage = ImageIO.read(new File("/Users/x810we/Pictures/FB/FarbchartEbenenmodi.png"));
            BufferedImage originalImage2 = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));

            //BufferedImage originalImage = ImageIO.read(new File("/Users/x810we/Pictures/FB/Grauchart.png"));
            //BufferedImage originalImage2 = ImageIO.read(new File("/Users/x810we/Pictures/FB/FarbchartEbenenmodi.png"));

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

                    int rgbOriginal2 = originalImage.getRGB(x, y);
                    Color colorOriginal2 = new Color(rgbOriginal2);

                    // Graufläche-Farbe erstellen (RGB = 128, 128, 128)
                    Color grayColor = new Color(colorOriginal2.getRed(), colorOriginal2.getGreen(), colorOriginal2.getBlue());



                    //Berechnung der Liminanz
                    float Luminanz = (float) (colorOriginal2.getRed() * 0.2126 + colorOriginal2.getGreen() * 0.7152 + colorOriginal2.getBlue() * 0.0072);

                    // Farbton und Sättigung des Originalbildes
                    float[] hsbOriginal = Color.RGBtoHSB(colorOriginal.getRed(), colorOriginal.getGreen(), colorOriginal.getBlue(), null);

                    // Luminanz von der Graufläche
                    float[] hsbGray = Color.RGBtoHSB(grayColor.getRed(), grayColor.getGreen(), grayColor.getBlue(), null);

                    //Maximalwert ermittel
                    float MaxiWert = Math.max(hsbGray[0],hsbGray[1]);
                          MaxiWert = Math.max(MaxiWert,hsbGray[2])  - 0.0f;

                    float MaxiWert1 = Math.max(hsbOriginal[0],hsbOriginal[1]);
                    MaxiWert1 = Math.max(MaxiWert1,hsbOriginal[2]);


                    //Kombinierte HSB-Werte: Farbton und Sättigung vom Original, Luminanz von der Graufläche
                   int rgbCombined = Color.HSBtoRGB(hsbOriginal[0], hsbOriginal[1], hsbGray[2]);
                   // int rgbCombined = Color.HSBtoRGB(hsbGray[0],  hsbOriginal[1], hsbGray[2]);
                  //  int rgbCombined = Color.HSBtoRGB(hsbOriginal[0] -0.0f, hsbOriginal[1] - 0.0f, MaxiWert );
                  //  int rgbCombined = Color.HSBtoRGB(hsbOriginal[0],hsbOriginal[1], hsbGray[2]);

                    // In das Ausgabebild schreiben
                    outputImage.setRGB(x, y, rgbCombined);
                }
            }

            // Ergebnis speichern
            ImageIO.write(outputImage, "png", new File("/Users/x810we/Pictures/FB/Output-FarbchartEbenenmodi-Luminaz.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}