package Test;

import java.util.Objects;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.Color;


public class GaussianBlur {

    public static void main(String[] args) {

        double[][] weights = GaussianBlur.getInstance().generateweightMatrix(1500,Math.sqrt(1500));
        GaussianBlur.getInstance().printWeightedMatrixToFile(weights);
        System.out.println();
    }

    private GaussianBlur() {
    }

    private static GaussianBlur blur;

   public static GaussianBlur getInstance() {
        if (blur == null)
            blur = new GaussianBlur();
            return blur;

   }

   public double[][] gaussianKernel(int size, double variance) {
       double[][] kernel = new double[size][size];
       double sumTotal = 0;
       int kernelRadius = size / 2;
       double distance = 0;

       double calculatedEuler = 1.0 / (2.0 * Math.PI * Math.pow(variance, 2));

       for (int filterY = -kernelRadius; filterY <= kernelRadius; filterY++) {
           for (int filterX = -kernelRadius; filterX <= kernelRadius; filterX++) {
               distance = ((filterX * filterX) + (filterY * filterY)) / (2 * (variance * variance));

               kernel[filterY + kernelRadius][filterX + kernelRadius] = calculatedEuler * Math.exp(-distance);

               sumTotal += kernel[filterY + kernelRadius][filterX + kernelRadius];
           }
       }

       for (int y = 0; y < size; y++) {
           for (int x = 0; x < size; x++) {
               kernel[y][x] = kernel[y][x] * (1.0 / sumTotal);
           }
       }

       return kernel;
   }

    public double[][] generateweightMatrix(int radius, double variance) {
        double[][] weights = new double[radius][radius];
        double summation = 0;
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = gaussianModel(i - radius / 2, j - radius / 2, variance);
                summation += weights[i][j];
                System.out.print(weights[i][j] + " ");
            }
            System.out.println();
        }
            System.out.println("---------");
            for (int i = 0; i < weights. length; i++) {
                for (int j = 0; j < weights[i].length; j++) {
                    weights[i][j] /= summation;
                }
            }

        System.out.println(summation);
            return weights;

        }



    public void printWeightedMatrixToFile(double[][] weightMatrix) {
        BufferedImage img = new BufferedImage(weightMatrix.length, weightMatrix.length, BufferedImage.TYPE_INT_RGB);
        double max = 0;
        for (int i = 0; i < weightMatrix.length; i++) {
            for (int j = 0; j < weightMatrix.length; j++) {
                //int value = (int) (weightMatrix[i] * 255);
                //int pixel = (value << 16) | (value << 8) | value;
                //img.setRGB(i, j, pixel);
                max = Math.max (max, weightMatrix[i][j]);
            }
        }

        for (int i = 0; i < weightMatrix.length; i++) {
            for (int j = 0; j < weightMatrix.length; j++){
                int grayScaleValue = (int) ((weightMatrix[i][j] / max) * 255d);
                img.setRGB(i, j, new Color(grayScaleValue, grayScaleValue, grayScaleValue).getRGB());

            }
        }

        try {
            ImageIO.write(img, "PNG", new File("/Users/x810we/Pictures/gaussian-weights-graphed.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



   public double gaussianModel(double x,double y,double variance) {
    return (1 / (2 * Math.PI * Math.pow(variance, 2))
            * Math. exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(variance, 2))));

}
}
