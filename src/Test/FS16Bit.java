package Test;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

//import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriterSpi;

public class FS16Bit{

    public static void main(String[] args) {
        try {
            // Lade das Originalbild
            ImageIO.scanForPlugins();

            BufferedImage originalImage = ImageIO.read(new File("/Users/x810we/Pictures/FB/Portrait16.tif"));



                    // Erzeuge eine weichgezeichnete Version des Bildes (hier simuliert, du kannst deine Blur-Methode implementieren)
            BufferedImage blurredImage = GaussianBlur16Bit.applyGaussianBlur(originalImage, 5);

            // Erzeuge das Struktur-Bild (High-Frequency)
            BufferedImage highFrequencyImage = frequencySeparation(originalImage, blurredImage);

            // Speichere die Ergebnisse
            ImageIO.write(blurredImage, "tif", new File("/Users/x810we/Pictures/FB/lowFrequency_16bit.tif"));
            ImageIO.write(highFrequencyImage, "tif", new File("/Users/x810we/Pictures/FB/highFrequency_16bit.tif"));

            System.out.println("Frequenztrennung erfolgreich abgeschlossen.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode zur Weichzeichnung (Gaußscher Weichzeichner für 16-Bit)
    private static BufferedImage applyGaussianBlur(BufferedImage image) {
        // Beispielhafter Platzhalter für eine Weichzeichnungslogik.
        // Du solltest hier einen echten Gaußschen Weichzeichner für 16-Bit-Bilder verwenden.
        BufferedImage blurredImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_USHORT_555_RGB);
        // TODO: Implementiere den Gauß-Blur für 16-Bit
        return blurredImage;
    }

    // Methode zur Frequenztrennung (16-Bit-Bilder)
    private static BufferedImage frequencySeparation(BufferedImage original, BufferedImage blurred) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage highFrequencyImage = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_555_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int originalRgb = original.getRGB(x, y);
                int blurredRgb = blurred.getRGB(x, y);

                // Extrahiere die 16-Bit-Farbkanäle
                int originalR = (originalRgb >> 16) & 0xFFFF;
                int originalG = (originalRgb >> 8) & 0xFFFF;
                int originalB = originalRgb & 0xFFFF;

                int blurredR = (blurredRgb >> 16) & 0xFFFF;
                int blurredG = (blurredRgb >> 8) & 0xFFFF;
                int blurredB = blurredRgb & 0xFFFF;

                // Berechne die High-Frequency-Komponente
                int r = Math.min(65535, Math.max(0, originalR - blurredR + 32768)); // 32768 = 50% Grau bei 16-Bit
                int g = Math.min(65535, Math.max(0, originalG - blurredG + 32768));
                int b = Math.min(65535, Math.max(0, originalB - blurredB + 32768));

                // Setze den neuen RGB-Wert
                int newRgb = (r << 16) | (g << 8) | b;
                highFrequencyImage.setRGB(x, y, newRgb);
            }
        }

        return highFrequencyImage;
    }

    public class GaussianBlur16Bit {

        // Methode zum Berechnen des Gaußschen Weichzeichners
        public static BufferedImage applyGaussianBlur(BufferedImage image, int radius) {
            int width = image.getWidth();
            int height = image.getHeight();
            BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_555_RGB);

            // Erzeuge den Gauß-Kernel
            double[][] kernel = createGaussianKernel(radius);
            int kernelSize = 2 * radius + 1;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    // Accumulatoren für die Summe der Farbwerte
                    double redSum = 0, greenSum = 0, blueSum = 0;
                    double weightSum = 0;

                    // Wende den Gauß-Kernel auf das aktuelle Pixel an
                    for (int i = -radius; i <= radius; i++) {
                        for (int j = -radius; j <= radius; j++) {
                            int currentX = Math.min(Math.max(x + i, 0), width - 1);
                            int currentY = Math.min(Math.max(y + j, 0), height - 1);

                            int rgb = image.getRGB(currentX, currentY);
                            int r = (rgb >> 16) & 0xFFFF;
                            int g = (rgb >> 8) & 0xFFFF;
                            int b = rgb & 0xFFFF;

                            // Gewicht aus dem Gauß-Kernel
                            double weight = kernel[i + radius][j + radius];

                            // Summiere die gewichteten Farbwerte
                            redSum += r * weight;
                            greenSum += g * weight;
                            blueSum += b * weight;
                            weightSum += weight;
                        }
                    }

                    // Normalisiere die Summen und setze den neuen Pixelwert
                    int r = (int) Math.min(65535, Math.max(0, redSum / weightSum));
                    int g = (int) Math.min(65535, Math.max(0, greenSum / weightSum));
                    int b = (int) Math.min(65535, Math.max(0, blueSum / weightSum));

                    int newRgb = (r << 16) | (g << 8) | b;
                    blurredImage.setRGB(x, y, newRgb);
                }
            }

            return blurredImage;
        }

        // Methode zum Erzeugen des Gauß-Kernels
        private static double[][] createGaussianKernel(int radius) {
            int size = 2 * radius + 1;
            double[][] kernel = new double[size][size];
            double sigma = radius / 3.0;
            double twoSigmaSquare = 2 * sigma * sigma;
            double piSigma = 2 * Math.PI * sigma * sigma;
            double total = 0;

            for (int i = -radius; i <= radius; i++) {
                for (int j = -radius; j <= radius; j++) {
                    double distance = i * i + j * j;
                    kernel[i + radius][j + radius] = Math.exp(-distance / twoSigmaSquare) / piSigma;
                    total += kernel[i + radius][j + radius];
                }
            }

            // Normiere den Kernel
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    kernel[i][j] /= total;
                }
            }

            return kernel;
        }
    }
}
