package Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class HochPassFilter {
    public static void main(String[] args) {
        try {
            // Bild laden
            BufferedImage originalImage = ImageIO.read(new File("/Users/x810we/Pictures/FB/FarbchartEbenenmodi.png"));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // Graustufenbild erstellen (notwendig für Hochpassfilter)
            BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = grayImage.createGraphics();
            g.drawImage(originalImage, 0, 0, null);
            g.dispose();

            // Bild für Hochpassfilter erstellen i
            BufferedImage highPassImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Hochpassfilter anwenden
            int radius = 300; // Radius von 10 Pixeln

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int sum = 0;
                    int count = 0;

                    // Schleife für benachbarte Pixel
                    for (int ky = -radius; ky <= radius; ky++) {
                        for (int kx = -radius; kx <= radius; kx++) {
                            int posX = x + kx;
                            int posY = y + ky;

                            // Stellen sicher, dass wir innerhalb der Bildgrenzen bleiben
                            if (posX >= 0 && posX < width && posY >= 0 && posY < height) {
                                int grayValue = new Color(grayImage.getRGB(posX, posY)).getRed();
                                sum += grayValue;
                                count++;
                            }
                        }
                    }

                    int average = sum / count;
                    int originalGrayValue = new Color(grayImage.getRGB(x, y)).getRed();
                    int highPassValue = Math.max(0, Math.min(255, originalGrayValue - average + 128));

                    // RGB-Wert für Hochpassbild setzen (Graustufenbild)
                    Color highPassColor = new Color(highPassValue, highPassValue, highPassValue);
                    highPassImage.setRGB(x, y, highPassColor.getRGB());
                }
            }

            // Hochpassbild speichern
            ImageIO.write(highPassImage, "png", new File("/Users/x810we/Pictures/FB/output_HochpassFilter.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
