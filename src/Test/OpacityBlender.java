package Test;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class OpacityBlender {

    public static void main(String[] args) {
        try {
            // 1. Zwei gleich große Bilder laden
            BufferedImage image1 = ImageIO.read(new File("/Users/x810we/Pictures/IMG_2027.jpg"));
            BufferedImage image2 = ImageIO.read(new File("/Users/x810we/Pictures/IMG_2027.jpg"));

            int width = image1.getWidth();
            int height = image1.getHeight();

            if (image2.getWidth() != width || image2.getHeight() != height) {
                System.err.println("Die Bilder müssen gleich groß sein.");
                return;
            }

            // 2. Opacity-Werte (zwischen 0.0 und 1.0)
            double opacity1 = 0.1;  // z. B. 40 % für Bild 1
            double opacity2 = 0.1;  // z. B. 60 % für Bild 2

            double total = opacity1 + opacity2;

            // Normalisieren (optional)
            if (total > 1.0) {
                opacity1 /= total;
                opacity2 /= total;
            }

            // 3. Neues Bild erzeugen
            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // 4. Pixelweise mischen mit Gewichtung
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb1 = image1.getRGB(x, y);
                    int rgb2 = image2.getRGB(x, y);

                    int r1 = (rgb1 >> 16) & 0xFF;
                    int g1 = (rgb1 >> 8) & 0xFF;
                    int b1 = rgb1 & 0xFF;

                    int r2 = (rgb2 >> 16) & 0xFF;
                    int g2 = (rgb2 >> 8) & 0xFF;
                    int b2 = rgb2 & 0xFF;

                    int r = (int) (r1 * opacity1 + r2 * opacity2);
                    int g = (int) (g1 * opacity1 + g2 * opacity2);
                    int b = (int) (b1 * opacity1 + b2 * opacity2);

                    int rgb = (r << 16) | (g << 8) | b;
                    result.setRGB(x, y, rgb);
                }
            }

            // 5. Ergebnis speichern
            ImageIO.write(result, "png", new File("/Users/x810we/Pictures/IMG_2027gemischt_opacity.png"));
            System.out.println("Bild gespeichert als 'gemischt_opacity.png'");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
