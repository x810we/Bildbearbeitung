package Test;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DynamikAdjustment {
    public static void main(String[] args) throws IOException {
        BufferedImage inputImage = ImageIO.read(new File("/Users/x810we/Library/Mobile Documents/X6B29J8D22~com~savysoda~documents/Documents/Daten/04 Technik/91 Fotographie/13 Fotobücher/Bildbearbeitung/Fotos/MacBookPro.jpg"));
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        // Linear contrast adjustment parameters
        double contrastFactor = 1.5;
        double brightnessOffset = 20;

        for (int y = 0; y < inputImage.getHeight(); y++) {
            for (int x = 0; x < inputImage.getWidth(); x++) {
                int rgb = inputImage.getRGB(x, y);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Apply contrast and brightness adjustments
                red = (int) (contrastFactor * red + brightnessOffset);
                green = (int) (contrastFactor * green + brightnessOffset);
                blue = (int) (contrastFactor * blue + brightnessOffset);

                red = Math.min(255, Math.max(0, red));
                green = Math.min(255, Math.max(0, green));
                blue = Math.min(255, Math.max(0, blue));

                int adjustedRgb = (red << 16) | (green << 8) | blue;
                outputImage.setRGB(x, y, adjustedRgb);
            }
        }

        ImageIO.write(outputImage, "jpg", new File("/Users/x810we/Library/Mobile Documents/X6B29J8D22~com~savysoda~documents/Documents/Daten/04 Technik/91 Fotographie/13 Fotobücher/Bildbearbeitung/Fotos/MacBookPro-Dynamik.jpg"));
    }
}
