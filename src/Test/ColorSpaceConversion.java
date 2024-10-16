package Test;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ColorSpaceConversion {

    public static void main(String[] args) {
        try {
            // Load the image in sRGB color space
            BufferedImage sRGBImage = ImageIO.read(new File("/Users/x810we/Pictures/IMG_4352.jpg"));

            // Load the Adobe RGB (1998) ICC profile from an InputStream /Users/x810we/IdeaProjects/Bildbearbeitung/Pictures/AdobeRGB1998.icc
            InputStream iccInputStream = new FileInputStream("/Users/x810we/IdeaProjects/Bildbearbeitung/Pictures/AdobeRGB1998.icc");
            ICC_Profile adobeRGBProfile = ICC_Profile.getInstance(iccInputStream);

            // Create Adobe RGB ColorSpace object
            ICC_ColorSpace adobeRGBColorSpace = new ICC_ColorSpace(adobeRGBProfile);

            // Create ColorConvertOp object for conversion
            ColorConvertOp colorConvertOp = new ColorConvertOp(sRGBImage.getColorModel().getColorSpace(), adobeRGBColorSpace, null);

            // Convert the sRGB image to Adobe RGB (1998)
            BufferedImage adobeRGBImage = colorConvertOp.filter(sRGBImage, null);

            // Save the converted image
            ImageIO.write(adobeRGBImage, "jpg", new File("/Users/x810we/Pictures/output_adobe_rgb_image.jpg"));

            System.out.println("Image successfully converted from sRGB to Adobe RGB (1998)!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
