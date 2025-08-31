package Test;

import com.aspose.psd.Image;
import com.aspose.psd.fileformats.psd.PsdImage;
import com.aspose.psd.fileformats.psd.layers.Layer;
import com.aspose.psd.imageoptions.PsdOptions;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class CreatePsdWithMask {
    public static void main(String[] args) throws Exception {
        // 1. Lade das gestreckte Bild und die Maske (Graustufen PNG)
        BufferedImage stretched = ImageIO.read(new File("/Users/x810we/Pictures/2025_08_Collage/IMG_0125.png"));
        //BufferedImage stretched = ImageIO.read(new File("/Users/x810we/Pictures/2025_08_Collage/IMG_0125-stretched.png"));
        BufferedImage mask = ImageIO.read(new File("/Users/x810we/Pictures/2025_08_Collage/IMG_0125-maske.png"));

        // 2. Erstelle ein neues PSD-Image mit den entsprechenden Abmessungen
        try (PsdImage psd = new PsdImage(stretched.getWidth(), stretched.getHeight())) {
            // 3. Füge die Bildebene hinzu
            Layer imageLayer = new Layer();
            imageLayer.setName("Stretched Image");
            //imageLayer.setBounds(0, 0, stretched.getWidth(), stretched.getHeight());
            psd.addLayer(imageLayer);
            //imageLayer.savePixels(stretched);

            // 4. Füge die Maske als zusätzliche Raster-Layer oder Maskenebene hinzu
            Layer maskLayer = new Layer();
            maskLayer.setName("Alpha Mask");
            //maskLayer.setBounds(0, 0, mask.getWidth(), mask.getHeight());
            psd.addLayer(maskLayer);
            //maskLayer.savePixels(mask);

            // 5. Speichere das PSD mit den Standardoptionen
            PsdOptions options = new PsdOptions();
            psd.save("/Users/x810we/Pictures/2025_08_Collage/IMG_0125-streched.psd", options);
        }
    }
}

