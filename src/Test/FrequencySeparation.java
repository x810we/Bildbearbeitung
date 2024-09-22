package Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class FrequencySeparation {

    // Methode zum Anwenden eines Gaußschen Weichzeichnungsfilters
    public static BufferedImage applyGaussianBlur(BufferedImage img, int radius) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage blurred = new BufferedImage(width, height, img.getType());

        // Einfache Gauß-Filter-Simulation durch gewichtetes Mittel (hier vereinfachter Box-Blur)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int sumR = 0, sumG = 0, sumB = 0;
                int count = 0;

                // Box-Blur innerhalb des Radius
                for (int i = -radius; i <= radius; i++) {
                    for (int j = -radius; j <= radius; j++) {
                        int newX = x + i;
                        int newY = y + j;
                        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                            Color pixelColor = new Color(img.getRGB(newX, newY));
                            sumR += pixelColor.getRed();
                            sumG += pixelColor.getGreen();
                            sumB += pixelColor.getBlue();
                            count++;
                        }
                    }
                }

                // Durchschnittswert innerhalb des Radius anwenden
                Color newColor = new Color(sumR / count, sumG / count, sumB / count);
                blurred.setRGB(x, y, newColor.getRGB());
            }
        }

        return blurred;
    }

    // Methode zur Frequenztrennung (Struktur- und Farbebenen erzeugen)
    public static void frequencySeparation(BufferedImage image, int blurRadius, String lowFreqPath, String highFreqPath) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Erzeuge das weichgezeichnete Bild (Low Frequency)
        BufferedImage lowFreqImage = applyGaussianBlur(image, blurRadius);

        // Erzeuge das Bild der hohen Frequenzen (Struktur)
        BufferedImage highFreqImage = new BufferedImage(width, height, image.getType());

        // Berechne das High-Frequency-Bild (Struktur)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color originalColor = new Color(image.getRGB(x, y));
                Color blurredColor = new Color(lowFreqImage.getRGB(x, y));

                // Hohe Frequenzen = Original - Weichgezeichnet
                int r = Math.min(255, Math.max(0, originalColor.getRed() - blurredColor.getRed() + 128));
                int g = Math.min(255, Math.max(0, originalColor.getGreen() - blurredColor.getGreen() + 128));
                int b = Math.min(255, Math.max(0, originalColor.getBlue() - blurredColor.getBlue() + 128));

                Color highFreqColor = new Color(r, g, b);
                highFreqImage.setRGB(x, y, highFreqColor.getRGB());
            }
        }

        try {
            // Speichere das Low-Frequency-Bild (Farbe)
            ImageIO.write(lowFreqImage, "png", new File("/Users/x810we/Pictures/FB/lowPortrait.jpg"));

            // Speichere das High-Frequency-Bild (Struktur)
            ImageIO.write(highFreqImage, "png", new File("/Users/x810we/Pictures/FB/highPortrait.jpg"));

            System.out.println("Frequenztrennung abgeschlossen. Dateien gespeichert.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // Lade das Eingangsbild (Porträt)
            BufferedImage inputImage = ImageIO.read(new File("/Users/x810we/Pictures/FB/Portrait.jpg"));

            // Setze den Radius für den Weichzeichner
            int blurRadius = 5;  // Passe diesen Wert für deine Bedürfnisse an

            // Pfade für die Ausgabebilder
            String lowFreqPath = "/Users/x810we/Pictures/FB/low_frequency_image.jpg";
            String highFreqPath = "/Users/x810we/Pictures/FB/high_frequency_image.jpg";

            // Führe die Frequenztrennung aus
            frequencySeparation(inputImage, blurRadius, lowFreqPath, highFreqPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
