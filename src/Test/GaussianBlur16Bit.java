package Test;

import java.awt.image.BufferedImage;

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
                        int r = (rgb >> 16) & 0xFFFF;
                        int g = (rgb >>  8) & 0xFFFF;
                        int b = rgb  & 0xFFFF;


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