package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class NegativeImageExample {
    public static void main(String[] args) throws IOException {
        BufferedImage inputImage = ImageIO.read(new File("/Users/x810we/Library/Mobile Documents/X6B29J8D22~com~savysoda~documents/Documents/Daten/04 Technik/91 Fotographie/13 Fotobücher/Bildbearbeitung/Fotos/Muster-Programmierung.jpg"));
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < inputImage.getHeight(); y++) {
            for (int x = 0; x < inputImage.getWidth(); x++) {
                int rgb = inputImage.getRGB(x, y);
                Color color = new Color(rgb);

                int red = 255 - color.getRed();
                int green = 255 - color.getGreen();
                int blue = 255 - color.getBlue();

                int invertedRgb = (red << 16) | (green << 8) | blue;
                outputImage.setRGB(x, y, invertedRgb);
            }
        }

        ImageIO.write(outputImage, "jpg", new File("/Users/x810we/Library/Mobile Documents/X6B29J8D22~com~savysoda~documents/Documents/Daten/04 Technik/91 Fotographie/13 Fotobücher/Bildbearbeitung/Fotos/Muster-Programmierung-Negative_output.jpg"));
    }
}