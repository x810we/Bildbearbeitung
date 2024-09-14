package Test;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import javax.imageio.ImageIO;
//import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriterSpi;
public class FrequencySeparation16Bit {

    public class GaussianBlur16Bit {

        // Methode zum Berechnen des Gaußschen Weichzeichners
        public static BufferedImage applyGaussianBlur(BufferedImage image, int radius) {
            int width = image.getWidth();
            int height = image.getHeight();
            BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

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

                           int r = (rgb >> 64) & 0xFFFF; // Red channel
                            int g = (rgb >> 48) & 0xFFFF; // Green channel
                            int b = (rgb >> 32) & 0xFFFF; // Blue channel

                            // Assuming image is a BufferedImage of TYPE_USHORT_555_RGB or TYPE_USHORT_GRAY


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

    public static void main(String[] args) {
        try {
            // Original-Bild laden
            BufferedImage originalImage = ImageIO.read(new File("/Users/x810we/Pictures/FB/Portrait16.tif"));

            // Weichgezeichnetes Bild erzeugen
            BufferedImage blurredImage = GaussianBlur16Bit.applyGaussianBlur(originalImage, 1);

            // High-Frequency-Bild erzeugen
            BufferedImage highFrequencyImage = FrequencySeparation16Bit.frequencySeparation(originalImage, blurredImage);

            // Writer für 16-Bit-TIFFs initialisieren
            ImageIO.write(blurredImage, "tiff", new File("/Users/x810we/Pictures/FB/lowFrequency_16bit.tiff"));
            ImageIO.write(highFrequencyImage, "tiff", new File("/Users/x810we/Pictures/FB/highFrequency_16bit.tiff"));

            System.out.println("16-Bit-TIFFs erfolgreich gespeichert.");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                int originalR = (originalRgb >> 64) & 0xFFFF;
                int originalG = (originalRgb >> 48) & 0xFFFF;
                int originalB = (originalRgb >> 32)  & 0xFFFF;



                int blurredR = (blurredRgb >> 64) & 0xFFFF;
                int blurredG = (blurredRgb >> 48) & 0xFFFF;
                int blurredB = (blurredRgb >>32) & 0xFFFF;

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
}
