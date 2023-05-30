package JpgPng;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import dyimagefx.*;


public class Intensity {

    ///////////////////////////// GRAYSCALE IMAGE //////////////////////////////

    /**
     * This will return the maximum intensity of the grayscale image img.
     *
     * @param img The grayscale image.
     * @return maximum intensity.
     */

    public static void main(String[] args){
        int minIntensity[] = new int[3];

        MyImage iobj = new MyImage();
        iobj.readImage("/Users/x810we/Pictures/IMG_2027.jpg");

        int maxGrayScale1 = grayscale_getMax(iobj);
        int maxRedScale1  = red_getMax(iobj);
        minIntensity      = color_getMin(iobj);

        iobj.writeImage("/Users/x810we/Pictures/Output-0.png");
    }




    public static int grayscale_getMax(MyImage img){
        int maxIntensity = 0;

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        //for grayscale image RGB value is same. So considering RED value.
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int r = img.getRed(x, y);
                if(r > maxIntensity){
                    maxIntensity = r;
                }
            }
        }
        return maxIntensity;
    }

    /**
     * This will return the minimum intensity of the grayscale image img.
     *
     * @param img The grayscale image.
     * @return minimum intensity.
     */
    public static int grayscale_getMin(MyImage img){
        int minIntensity = 255;

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        //for grayscale image RGB value is same. So considering RED value.
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int r = img.getRed(x, y);
                if(r < minIntensity){
                    minIntensity = r;
                }
            }
        }
        return minIntensity;
    }

    ///////////////////////////// RED IMAGE ////////////////////////////////////

    /**
     * This will return the maximum intensity of the RED image img.
     *
     * @param img The RED image.
     * @return maximum intensity.
     */
    public static int red_getMax(MyImage img){
        int maxIntensity = 0;

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int r = img.getRed(x, y);
                if(r > maxIntensity){
                    maxIntensity = r;
                }
            }
        }
        return maxIntensity;
    }

    /**
     * This will return the minimum intensity of the RED image img.
     *
     * @param img The RED image.
     * @return minimum intensity.
     */
    public static int red_getMin(MyImage img){
        int minIntensity = 255;

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int r = img.getRed(x, y);
                if(r < minIntensity){
                    minIntensity = r;
                }
            }
        }
        return minIntensity;
    }

    ///////////////////////////// GREEN IMAGE //////////////////////////////////

    /**
     * This will return the maximum intensity of the GREEN image img.
     *
     * @param img The GREEN image.
     * @return maximum intensity.
     */
    public static int green_getMax(MyImage img){
        int maxIntensity = 0;

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int g = img.getGreen(x, y);
                if(g > maxIntensity){
                    maxIntensity = g;
                }
            }
        }
        return maxIntensity;
    }

    /**
     * This will return the minimum intensity of the GREEN image img.
     *
     * @param img The GREEN image.
     * @return minimum intensity.
     */
    public static int green_getMin(MyImage img){
        int minIntensity = 255;

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int g = img.getGreen(x, y);
                if(g < minIntensity){
                    minIntensity = g;
                }
            }
        }
        return minIntensity;
    }

    ///////////////////////////// BLUE IMAGE ///////////////////////////////////

    /**
     * This will return the maximum intensity of the BLUE image img.
     *
     * @param img The BLUE image.
     * @return maximum intensity.
     */
    public static int blue_getMax(MyImage img){
        int maxIntensity = 0;

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int b = img.getBlue(x, y);
                if(b > maxIntensity){
                    maxIntensity = b;
                }
            }
        }
        return maxIntensity;
    }

    /**
     * This will return the minimum intensity of the BLUE image img.
     *
     * @param img The BLUE image.
     * @return minimum intensity.
     */
    public static int blue_getMin(MyImage img){
        int minIntensity = 255;

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int b = img.getBlue(x, y);
                if(b < minIntensity){
                    minIntensity = b;
                }
            }
        }
        return minIntensity;
    }

    ///////////////////////////// COLOR IMAGE //////////////////////////////////

    /**
     * This will return the maximum intensity of the color image img.
     *
     * @param img The color image.
     * @return maximum intensity array having 3 elements for RGB.
     */
    public static int[] color_getMax(MyImage img){
        int maxIntensity[] = new int[3];
        maxIntensity[0] = red_getMax(img);
        maxIntensity[1] = green_getMax(img);
        maxIntensity[2] = blue_getMax(img);
        return maxIntensity;
    }

    /**
     * This will return the minimum intensity of the color image img.
     *
     * @param img The color image.
     * @return minimum intensity.
     */
    public static int[] color_getMin(MyImage img){
        int minIntensity[] = new int[3];
        minIntensity[0] = red_getMin(img);
        minIntensity[1] = green_getMin(img);
        minIntensity[2] = blue_getMin(img);
        return minIntensity;
    }
}//class ends here
