package Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SchwarzWeiss {
    public static void main(String[] args) throws IOException {
        BufferedImage inputImage = ImageIO.read(new File("/Users/810we/Library/Mobile Documents/X6B29J8D22~com~savysoda~documents/Documents/Daten/04 Technik/91 Fotographie/13 Fotobücher/Bildbearbeitung/Fotos/Muster-Programmierung.jpg"));
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < inputImage.getHeight(); y++) {
            for (int x = 0; x < inputImage.getWidth(); x++) {
                int rgb = inputImage.getRGB(x, y);
                Color color = new Color(rgb);

                int grey = ((color.getRed() + color.getGreen() + color.getBlue()) / 3 ) - 8 ;
                int red = 0, green = 0, blue = 0;

                if (grey >= 0 )  {red = grey;green = grey; blue = grey;} else {red = 0;green = 0; blue = 0;}


                int invertedRgb = (red << 16) | (green << 8) | blue;
                outputImage.setRGB(x, y, invertedRgb);
            }
        }

        ImageIO.write(outputImage, "jpg", new File("/Users/810we/Library/Mobile Documents/X6B29J8D22~com~savysoda~documents/Documents/Daten/04 Technik/91 Fotographie/13 Fotobücher/Bildbearbeitung/Fotos/Muster-Programmierung-Negative-OutPut.jpg"));
    }
}
