package Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCalculation {

    public static void main(String[] args) {
        try {
            // Load two 8-bit images (they must be the same dimensions)
            BufferedImage image1 = ImageIO.read(new File("path_to_first_image.jpg"));
            BufferedImage image2 = ImageIO.read(new File("path_to_second_image.jpg"));

            // Perform addition blend mode with scale and offset
            BufferedImage additionResult = blendImages(image1, image2, "addition", 1.0f, 0);
            ImageIO.write(additionResult, "jpg", new File("addition_result.jpg"));

            // Perform subtraction blend mode with scale and offset
            BufferedImage subtractionResult = blendImages(image1, image2, "subtraction", 1.0f, 0);
            ImageIO.write(subtractionResult, "jpg", new File("subtraction_result.jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage blendImages(BufferedImage img1, BufferedImage img2, String mode, float scale, int offset) {
        int width = img1.getWidth();
        int height = img1.getHeight();

        // Ensure both images are the same size
        if (width != img2.getWidth() || height != img2.getHeight()) {
            throw new IllegalArgumentException("Images must have the same dimensions");
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Loop through each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get RGB values of both images
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                // Extract RGB channels
                int r1 = (rgb1 >> 16) & 0xFF;
                int g1 = (rgb1 >> 8) & 0xFF;
                int b1 = rgb1 & 0xFF;

                int r2 = (rgb2 >> 16) & 0xFF;
                int g2 = (rgb2 >> 8) & 0xFF;
                int b2 = rgb2 & 0xFF;

                // Apply blend mode
                int[] blendedRGB = new int[3];
                if ("addition".equalsIgnoreCase(mode)) {
                    blendedRGB[0] = (int) Math.min(255, Math.max(0, (r1 + r2) * scale + offset));
                    blendedRGB[1] = (int) Math.min(255, Math.max(0, (g1 + g2) * scale + offset));
                    blendedRGB[2] = (int) Math.min(255, Math.max(0, (b1 + b2) * scale + offset));
                } else if ("subtraction".equalsIgnoreCase(mode)) {
                    blendedRGB[0] = (int) Math.min(255, Math.max(0, (r1 - r2) * scale + offset));
                    blendedRGB[1] = (int) Math.min(255, Math.max(0, (g1 - g2) * scale + offset));
                    blendedRGB[2] = (int) Math.min(255, Math.max(0, (b1 - b2) * scale + offset));
                }

                // Combine channels back into a single int and set pixel
                int newRGB = (blendedRGB[0] << 16) | (blendedRGB[1] << 8) | blendedRGB[2];
                result.setRGB(x, y, newRGB);
            }
        }
        return result;
    }
}
