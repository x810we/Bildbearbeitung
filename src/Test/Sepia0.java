package Test;

// Java program to demonstrate
// colored to sepia conversion

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Sepia0 {
    public static void main(String args[])
            throws IOException
    {
        BufferedImage img = null;
        File f = null;

        // read image
        try {
            f = new File(
                    "/Users/x810we/Pictures/IMG_2027.jpg");
            img = ImageIO.read(f);
        }
        catch (IOException e) {
            System.out.println(e);
        }

        // get width and height of the image
        int width = img.getWidth();
        int height = img.getHeight();

        // convert to sepia
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = img.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int R = (p >> 16) & 0xff;
                int G = (p >> 8) & 0xff;
                int B = p & 0xff;

                // calculate newRed, newGreen, newBlue
                int newRed = (int)(0.393 * R + 0.769 * G
                        + 0.189 * B);
                int newGreen = (int)(0.349 * R + 0.686 * G
                        + 0.168 * B);
                int newBlue = (int)(0.272 * R + 0.534 * G
                        + 0.131 * B);

                // check condition
                if (newRed > 255)
                    R = 255;
                else
                    R = newRed;

                if (newGreen > 255)
                    G = 255;
                else
                    G = newGreen;

                if (newBlue > 255)
                    B = 255;
                else
                    B = newBlue;
/*
                G = G +190;
                if (G >=255) G = 255;
                R = G; B = G;
*/

                // set new RGB value
                p = (a << 24) | (R << 16) | (G << 8) | B;

                img.setRGB(x, y, p);
            }
        }

        // write image
        try {
            f = new File(
                    "/Users/x810we/Pictures/IMG_2027Output-0.jpg");
            ImageIO.write(img, "png", f);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
}
