package Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCalculationWithMask {
    public static void main(String[] args) throws Exception {
        BufferedImage image1 = ImageIO.read(new File("image1.png"));
        BufferedImage image2 = ImageIO.read(new File("image2.png"));
        BufferedImage maskImage = ImageIO.read(new File("mask.png"));  // Mask file

        BufferedImage result = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int offset = 50;  // Example offset value
        float scale = 1.0f;  // Example scale value

        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                int rgb1 = image1.getRGB(x, y);
                int rgb2 = image2.getRGB(x, y);
                int mask = maskImage.getRGB(x, y) & 0xFF;  // Grayscale mask (only using one channel)

                // Apply mask to scale the effect
                float maskFactor = mask / 255.0f;  // Normalize the mask to a 0-1 range

                // Extract channels from both images
                int r1 = (rgb1 >> 16) & 0xFF;
                int g1 = (rgb1 >> 8) & 0xFF;
                int b1 = rgb1 & 0xFF;

                int r2 = (rgb2 >> 16) & 0xFF;
                int g2 = (rgb2 >> 8) & 0xFF;
                int b2 = rgb2 & 0xFF;

                // Apply addition/subtraction only where mask allows it
                int resultR = Math.min(255, Math.max(0, (int) (((r1 + r2) * maskFactor * scale) + offset)));
                int resultG = Math.min(255, Math.max(0, (int) (((g1 + g2) * maskFactor * scale) + offset)));
                int resultB = Math.min(255, Math.max(0, (int) (((b1 + b2) * maskFactor * scale) + offset)));

                // Combine and save the new pixel
                int newRGB = (255 << 24) | (resultR << 16) | (resultG << 8) | resultB;
                result.setRGB(x, y, newRGB);
            }
        }

        // Save the result
        ImageIO.write(result, "png", new File("output_image_masked.png"));
    }
}
