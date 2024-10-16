package Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HEICToJPGConverter {
    public static void main(String[] args) {
        // Pfad der HEIC-Datei
        File heicFile = new File("/Users/x810we/Pictures/IMG_4352.HEIC");
        // Pfad zur Ausgabe JPG-Datei
        File jpgFile = new File("/Users/x810we/Pictures/output_image.jpg");

        try {
            // Lese die HEIC-Datei ein
            BufferedImage image = ImageIO.read(heicFile);

            // Konvertiere und speichere die Datei als JPG
            if (image != null) {
                ImageIO.write(image, "jpg", jpgFile);
                System.out.println("HEIC wurde erfolgreich in JPG umgewandelt.");
            } else {
                System.out.println("Bild konnte nicht gelesen werden.");
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen oder Speichern der Datei: " + e.getMessage());
        }
    }
}
