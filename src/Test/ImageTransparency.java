package Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageTransparency {
    public static void main(String[] args) {
        try {
            // Bild laden
            BufferedImage image = ImageIO.read(new File("/Users/x810we/Pictures/Sophie2024.png"));

            // Neues Bild mit Alpha-Kanal erstellen
            BufferedImage transparentImage = new BufferedImage(
                    image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // Grafik-Objekt zum Zeichnen erstellen
            Graphics2D g2d = transparentImage.createGraphics();

            // Bild durchlaufen und Alpha-Wert setzen
            int alphaValue = 80; // 50% Transparenz
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgba = image.getRGB(x, y);
                    Color col = new Color(rgba, true);
                    int r = col.getRed();
                    int g = col.getGreen();
                    int b = col.getBlue();
                    int t = col.getTransparency();
                    int a = col.getAlpha();
                    int Test = col.getTransparency();

                    Color newCol = new Color(col.getRed(), col.getGreen(), col.getBlue(), alphaValue);
                    transparentImage.setRGB(x, y, newCol.getRGB());
                }
            }

            // Grafik-Objekt freigeben
            g2d.dispose();

            // Bild speichern
            ImageIO.write(transparentImage, "png", new File("/Users/x810we/Pictures/output_imageTransparency.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}