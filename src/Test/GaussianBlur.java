package Test;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.Color;


public class GaussianBlur {
    public static GaussianBlur blur;
    public static void main(String[] args) {

        double[][] weights = GaussianBlur.getInstance().generateweightMatrix(6,Math.sqrt(6));
        blur.printWeightedMatrixToFile(weights);

        BufferedImage answer = null;
        try {
            //BufferedImage source_img = ImageIO.read(new File("/Users/x810we/Pictures/IMG_1477.jpeg"));
           // answer = GaussianBlur.getInstance().createGaussianedImage(source_img, weights, 1500);
           answer = blur.createGaussianImage(ImageIO.read(new File("/Users/x810we/Pictures/MA-Herren40-2.jpg")), weights, 6 );


            //GaussianBlur.getInstance().createGaussianedImage(ImageIO.read(Class.class.getResourceAsStream("/Users/x810we/Pictures/IMG_1477.jpeg")), weights, 150);
     }   catch (IOException e) {
         e.printStackTrace();
     }
        try {
            ImageIO.write(answer, "PNG", new File("/Users/x810we/Pictures/answer.png"));
             } catch (IOException e) {
            e.printStackTrace();
        }
         System.out.println("Done");
     }

    public GaussianBlur() {

    }

//     public static GaussianBlur blur;

   public static GaussianBlur getInstance() {
        if (blur == null)
            blur = new GaussianBlur();
            return blur;

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
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i].length; j++) {
                    weights[i][j] /= summation;
                    System.out.print(weights[i][j] + " ");
                }
                System.out.println();
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




    public BufferedImage createGaussianImage(BufferedImage source_image,double weights[][], int radius) {
       System.out.println("Working...");
       BufferedImage answer = new BufferedImage(source_image.getWidth(),source_image.getHeight(),BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < (source_image.getWidth() - radius); x++) {
            for (int y = 0; y < (source_image.getHeight() - radius); y++) {
                double[][] distributedColorRed = new double[radius][radius];
                double[][] distributedColorGreen = new double[radius][radius];
                double[][] distributedColorBlue = new double[radius][radius];

                for (int weightX = 0; weightX < weights.length; weightX++) {
                    for (int weightY = 0; weightY < weights[weightX].length; weightY++) {


                            int sampleX = x + weightX - (weights.length / 2);
                            int sampleY = y + weightY - (weights.length / 2);

                            if( sampleX >= 0 && sampleY >= 0) {
                                double currentWeight = weights[weightX][weightY];
                                Color sampledColor = new Color(source_image.getRGB(sampleX, sampleY));

                                distributedColorRed[weightX][weightY] = currentWeight * (double) sampledColor.getRed();
                                distributedColorGreen[weightX][weightY] = currentWeight * (double) sampledColor.getGreen();
                                distributedColorBlue[weightX][weightY] = currentWeight * (double) sampledColor.getBlue();
                            }
                        }
                }
                if ( x<=source_image.getWidth() && y<= source_image.getHeight()) {
                    answer.setRGB(x, y, new Color(getwightedColorValue(distributedColorRed), getwightedColorValue(distributedColorGreen), getwightedColorValue(distributedColorBlue)).getRGB());
                    if (x == 290 && y == 208) {
                        System.out.println("Done:" + getwightedColorValue(distributedColorRed)+"  " + getwightedColorValue(distributedColorGreen) + "  " + getwightedColorValue(distributedColorBlue));
                    }
                }
            }
           //answer.setRGB(x,y,rgb);
            }

            return answer;

        }
        //return source_image;

    public int getwightedColorValue(double[][] weightedColor) {
        double summation = 0;
        for (int i = 0; i < weightedColor.length; i++) {
            for (int j = 0; j < weightedColor[i].length; j++) {
                summation += weightedColor[i][j];
            }
        }
        return (int) summation;
    }



        public double gaussianModel ( double x, double y, double variance){
            return (1 / (2 * Math.PI * Math.pow(variance, 2))
                    * Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(variance, 2))));

        }
    }
