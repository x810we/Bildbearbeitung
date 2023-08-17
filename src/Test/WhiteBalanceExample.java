package Test;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class WhiteBalanceExample {
    public static void main(String[] args) throws IOException {
        BufferedImage inputImage = ImageIO.read(new File("/Users/x810we/Library/Mobile Documents/X6B29J8D22~com~savysoda~documents/Documents/Daten/04 Technik/91 Fotographie/13 Fotobücher/Bildbearbeitung/Fotos/MacBookPro.jpg"));
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        // Gray card's average gray value (adjust this based on the gray card used)
        int grayValue = 128;

        for (int y = 0; y < inputImage.getHeight(); y++) {
            for (int x = 0; x < inputImage.getWidth(); x++) {
                int rgb = inputImage.getRGB(x, y);
                Color color = new Color(rgb);

                int a = (rgb >> 24) & 0xFF; //Aplpha Transperency
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // Calculate the scaling factor based on the gray card's average gray value
                double scaleFactor = (double) grayValue / ((red + green + blue) / 3.0);

                red = (int) (red * scaleFactor);
                green = (int) (green * scaleFactor);
                blue = (int) (blue * scaleFactor);

                // Make sure the values are within the valid range
                red = Math.min(255, Math.max(0, red));
                green = Math.min(255, Math.max(0, green));
                blue = Math.min(255, Math.max(0, blue));

                int adjustedRgb = (a << 24) | (red << 16) | (green << 8) | blue;
                outputImage.setRGB(x, y, adjustedRgb);
            }
        }

        ImageIO.write(outputImage, "jpg", new File("/Users/x810we/Library/Mobile Documents/X6B29J8D22~com~savysoda~documents/Documents/Daten/04 Technik/91 Fotographie/13 Fotobücher/Bildbearbeitung/Fotos/MacBookProWhite_balanced_output.jpg"));
    }
}