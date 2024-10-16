package Test;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import javax.imageio.ImageIO;

public class AdobeRGBToSRGBConverter {

    public static void main(String[] args) {
        try {
            // Lade das Bild im Adobe RGB Farbraum
            File inputFile = new File("/Users/x810we/Pictures/output_adobe_rgb_image.jpg");
            BufferedImage adobeRGBImage = ImageIO.read(inputFile);

            // Erhalte den sRGB Farbraum
            ColorSpace sRGBColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);

            // Erstelle einen ColorConvertOp um von Adobe RGB (1998) zu sRGB zu konvertieren
            ColorConvertOp colorConvertOp = new ColorConvertOp(adobeRGBImage.getColorModel().getColorSpace(), sRGBColorSpace, null);

            // Konvertiere das Bild
            BufferedImage sRGBImage = new BufferedImage(adobeRGBImage.getWidth(), adobeRGBImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            colorConvertOp.filter(adobeRGBImage, sRGBImage);

            // Speichere das konvertierte Bild
            File outputFile = new File("/Users/x810we/Pictures/path_to_output_srgb_image.jpg");
            ImageIO.write(sRGBImage, "jpg", outputFile);

            System.out.println("Bild erfolgreich von Adobe RGB (1998) zu sRGB konvertiert.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
