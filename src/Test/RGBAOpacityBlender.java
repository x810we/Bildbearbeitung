package Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RGBAOpacityBlender {

    public static void main(String[] args) {
        try {
            // 1. Zwei gleich große Bilder mit Alpha-Kanal laden
            BufferedImage image1 = ImageIO.read(new File("/Users/x810we/Pictures/IMG_2027.jpg"));
            BufferedImage image2 = ImageIO.read(new File("/Users/x810we/Pictures/IMG_2027.jpg"));

            int width = image1.getWidth();
            int height = image1.getHeight();

            if (image2.getWidth() != width || image2.getHeight() != height) {
                System.err.println("Die Bilder müssen gleich groß sein.");
                return;
            }

            // 2. Einstellbare Opacity-Werte
            double opacity1 = 0.8;
            double opacity2 = 0.5;

            // Optional: normalisieren, falls Summe > 1
            double totalOpacity = opacity1 + opacity2;
            if (totalOpacity > 1.0) {
                opacity1 /= totalOpacity;
                opacity2 /= totalOpacity;
            }

            // 3. Neues Bild mit Alpha-Kanal
            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // 4. Pixelweise RGBA-Werte mischen
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgba1 = image1.getRGB(x, y);
                    int rgba2 = image2.getRGB(x, y);

                    int a1 = (rgba1 >> 24) & 0xFF;
                    int r1 = (rgba1 >> 16) & 0xFF;
                    int g1 = (rgba1 >> 8) & 0xFF;
                    int b1 = rgba1 & 0xFF;

                    int a2 = (rgba2 >> 24) & 0xFF;
                    int r2 = (rgba2 >> 16) & 0xFF;
                    int g2 = (rgba2 >> 8) & 0xFF;
                    int b2 = rgba2 & 0xFF;

                    // Alpha separat gewichten und mischen
                    int a = (int) (a1 * opacity1 + a2 * opacity2);
                    int r = (int) (r1 * opacity1 + r2 * opacity2);
                    int g = (int) (g1 * opacity1 + g2 * opacity2);
                    int b = (int) (b1 * opacity1 + b2 * opacity2);

                    int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                    result.setRGB(x, y, rgba);
                }
            }

            // 5. Ergebnis speichern
            ImageIO.write(result, "png", new File("/Users/x810we/Pictures/IMG_2027gemischt_opacity.png"));
            System.out.println("Bild gespeichert als 'gemischt_alpha.png'");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
