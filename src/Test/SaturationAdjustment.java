package Test;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaturationAdjustment {
    public static void main(String[] args) {
        String inputImagePath = "/Users/x810we/Library/Mobile Documents/X6B29J8D22~com~savysoda~documents/Documents/Daten/04 Technik/91 Fotographie/13 Fotobücher/Bildbearbeitung/Fotos/MacBookPro.jpg"; // Pfad zum Eingangsbild
        String outputImagePath = "/Users/x810we/Library/Mobile Documents/X6B29J8D22~com~savysoda~documents/Documents/Daten/04 Technik/91 Fotographie/13 Fotobücher/Bildbearbeitung/Fotos/MacBookPro-Saturation.jpg"; // Pfad zum Ausgabebild

        try {
            // Lade das Bild
            BufferedImage image = ImageIO.read(new File(inputImagePath));
 //           ImageIO.write(image, "png", new File(outputImagePath));

            // Durchlaufe alle Pixel im Bild
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);

                    // Extrahiere Rot, Grün und Blau Komponenten
                    int a = (rgb >> 24) & 0xFF;
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    // Erhöhe die Sättigung (Beispiel: um 50)
                    r = Math.min(255, r + 100);
                    g = Math.min(255, g + 100);
                    b = Math.min(255, b + 100);

                    // Setze die bearbeiteten Werte zurück ins RGB
                    rgb = (a << 24) | (r << 16) | (g << 8) | b;

                    image.setRGB(x, y, rgb);
                }
            }

            //
            // Speichere das bearbeitete Bild
            ImageIO.write(image, "jpg", new File(outputImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}