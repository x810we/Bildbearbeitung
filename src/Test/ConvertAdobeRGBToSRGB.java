package Test;


import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ConvertAdobeRGBToSRGB {

    public static void main(String[] args) {
        try {
            // Lade das Bild im Adobe RGB (1998)-Farbraum
            File inputFile = new File("/Users/x810we/Pictures/output_adobe_rgb_image.jpg");
            BufferedImage adobeRgbImage = ImageIO.read(inputFile);

            // Konvertiere von Adobe RGB nach sRGB
            ColorSpace sRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            ColorConvertOp convertToSRGB = new ColorConvertOp(adobeRgbImage.getColorModel().getColorSpace(), sRGB, null);

            // Erzeuge das konvertierte Bild
            BufferedImage sRGBImage = convertToSRGB.filter(adobeRgbImage, null);

            // Speichere das konvertierte Bild im sRGB-Farbraum
            File outputFile = new File("/Users/x810we/Pictures/output_srgb_image.jpg");
            ImageIO.write(sRGBImage, "jpg", outputFile);

            System.out.println("Konvertierung erfolgreich abgeschlossen.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
