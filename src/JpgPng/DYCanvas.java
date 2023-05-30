package JpgPng;

import dyimagefx.MyImage;

/**
 * File: DYCanvas.java
 *
 * Description:
 * This file contains some random canvas creating method.
 *
 * @author Yusuf Shakeel
 * @version 1.0
 * Date: 26-01-2014 sun
 *
 * www.github.com/yusufshakeel/Java-Image-Processing-Project
 *
 * The MIT License (MIT)
 * Copyright (c) 2014 Yusuf Shakeel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class DYCanvas {

    ///////////////////////////////// COLOR MIX methods ////////////////////////

    /**
     * This method will generate an image of different shade out of the entered image.
     *
     * @param img The image color to mix.
     */


    public static void main(String[] args){
        int minIntensity[] = new int[3];

        MyImage iobj = new MyImage();
        iobj.readImage("/Users/x810we/Pictures/IMG_2027.jpg");

        colorMix3(iobj);

        iobj.writeImage("/Users/x810we/Pictures/Output-0.png");
    }



    public static void colorMix(MyImage img){

        int maskSize = 3;   //The width of the mask.

        /**
         * Buffered array of pixels holds the intermediate value of pixels that
         * is multiplied with the mask to get the final value for the center
         * pixel under the mask.
         */
        int buff[];


        /**
         * This array will store the output of the median filter operation which will
         * be later written back to the original image pixels.
         */
        int outputPixels[] = new int[img.getImageTotalPixels()];

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        /** Sharpen operation */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                /** Fill buff array */
                int i = 0;
                buff = new int[9];
                for(int r = y - (maskSize / 2); r <= y + (maskSize / 2); r++){
                    for(int c = x - (maskSize / 2); c <= x + (maskSize / 2); c++){
                        if(r < 0 || r >= height || c < 0 || c >= width){
                            /** Some portion of the mask is outside the image. */
                            int tr = r, tc = c;
                            if(r < 0){
                                tr = r+1;
                            }else if(r == height){
                                tr = r-1;
                            }
                            if(c < 0){
                                tc = c+1;
                            }else if(c == width){
                                tc = c-1;
                            }
                            buff[i] = img.getPixel(tc, tr);
                        }else{
                            buff[i] = img.getPixel(c, r);
                        }
                        i++;
                    }
                }

                /** Sum of the image buff */
                int sum = 0;
                for(i = 0; i < 9; i++){
                    sum += buff[i];
                }

                /** Save result in outputPixels array. */
                outputPixels[x+y*width] = sum;
            }
        }
        /** Write the output pixels to the image pixels */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                img.setPixelToValue(x, y, outputPixels[x+y*width]);
            }
        }
    }

    /**
     * This method will mix colors.
     *
     * @param img The image color to mix.
     */
    public static void colorMix2(MyImage img){

        /**
         * Mask is a 2D square of odd size like 3x3
         * For simplicity converting it into 1D array.
         */
        int mask[] = new int[]{    9, -1, -3,
                1,  2,  1,
                -3, -1,  9};

        int maskSize = 3;   //The width of the mask.

        /**
         * Buffered array of pixels holds the intermediate value of pixels that
         * is multiplied with the mask to get the final value for the center
         * pixel under the mask.
         */
        int buff[];

        /**
         * This array will store the output of the median filter operation which will
         * be later written back to the original image pixels.
         */
        int outputPixels[] = new int[img.getImageTotalPixels()];

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        /** Sharpen operation */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                /** Fill buff array */
                int i = 0;
                buff = new int[9];
                for(int r = y - (maskSize / 2); r <= y + (maskSize / 2); r++){
                    for(int c = x - (maskSize / 2); c <= x + (maskSize / 2); c++){
                        if(r < 0 || r >= height || c < 0 || c >= width){
                            /** Some portion of the mask is outside the image. */
                            int tr = r, tc = c;
                            if(r < 0){
                                tr = r+1;
                            }else if(r == height){
                                tr = r-1;
                            }
                            if(c < 0){
                                tc = c+1;
                            }else if(c == width){
                                tc = c-1;
                            }
                            buff[i] = img.getPixel(tc, tr);
                        }else{
                            buff[i] = img.getPixel(c, r);
                        }
                        i++;
                    }
                }

                /** Multiply mask with buff array to get the final value. */
                int sa=0, sr=0, sg=0, sb=0;
                for(i = 0; i < 9; i++){
                    sa += mask[i]*ImageFX.getAlphaValueFromPixelValue(buff[i]);
                    sr += mask[i]*ImageFX.getRedValueFromPixelValue(buff[i]);
                    sg += mask[i]*ImageFX.getGreenValueFromPixelValue(buff[i]);
                    sb += mask[i]*ImageFX.getBlueValueFromPixelValue(buff[i]);
                }

                /** Save result in outputPixels array. */
                int p = ImageFX.getPixelValueFromARGBValue(sa, sr, sg, sb);
                outputPixels[x+y*width] = p;
            }
        }
        /** Write the output pixels to the image pixels */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                img.setPixelToValue(x, y, outputPixels[x+y*width]);
            }
        }
    }

    /**
     * This method will mix colors.
     *
     * @param img The image color to mix.
     */
    public static void colorMix3(MyImage img){

        /**
         * Mask is a 2D square of odd size like 3x3
         * For simplicity converting it into 1D array.
         */
        int mask[] = new int[]{    3, -1, -3,
                1,  2,  1,
                -3, -1,  3};

        int maskSize = 3;   //The width of the mask.

        /**
         * Buffered array of pixels holds the intermediate value of pixels that
         * is multiplied with the mask to get the final value for the center
         * pixel under the mask.
         */
        int buff[];

        /**
         * This array will store the output of the median filter operation which will
         * be later written back to the original image pixels.
         */
        int outputPixels[] = new int[img.getImageTotalPixels()];

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        /** Sharpen operation */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                /** Fill buff array */
                int i = 0;
                buff = new int[9];
                for(int r = y - (maskSize / 2); r <= y + (maskSize / 2); r++){
                    for(int c = x - (maskSize / 2); c <= x + (maskSize / 2); c++){
                        if(r < 0 || r >= height || c < 0 || c >= width){
                            /** Some portion of the mask is outside the image. */
                            int tr = r, tc = c;
                            if(r < 0){
                                tr = r+1;
                            }else if(r == height){
                                tr = r-1;
                            }
                            if(c < 0){
                                tc = c+1;
                            }else if(c == width){
                                tc = c-1;
                            }
                            buff[i] = img.getPixel(tc, tr);
                        }else{
                            buff[i] = img.getPixel(c, r);
                        }
                        i++;
                    }
                }

                /** Multiply mask with buff array to get the final value. */
                int sa=0, sr=0, sg=0, sb=0;
                for(i = 0; i < 9; i++){
                    sa += mask[i]*ImageFX.getAlphaValueFromPixelValue(buff[i]);
                    sr += mask[i]*ImageFX.getRedValueFromPixelValue(buff[i]);
                    sg += mask[i]*ImageFX.getGreenValueFromPixelValue(buff[i]);
                    sb += mask[i]*ImageFX.getBlueValueFromPixelValue(buff[i]);
                }

                /** Save result in outputPixels array. */
                int p = ImageFX.getPixelValueFromARGBValue(sa, sr, sg, sb);
                outputPixels[x+y*width] = p;
            }
        }
        /** Write the output pixels to the image pixels */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                img.setPixelToValue(x, y, outputPixels[x+y*width]);
            }
        }
    }

    ///////////////////////////////// LAYERS ///////////////////////////////////
    /**
     * This method will replace the text area with the image that is placed beneath it.
     * This function takes two images. One is the photo image and the other is the text
     * image. The text image has a white background and a text message written in black color.
     * The text image is placed on top of the photo image. This function will replace the
     * text pixels of the text image with the photo pixels of the photo image that is
     * beneath it.
     *
     * @param textImg The image object that holds the text message image.
     * @param photoImg The image object that holds the photo image.
     */
    public static void Layer_pictureText(MyImage textImg, MyImage photoImg){
        //test image dimension
        int tWidth = textImg.getImageWidth();
        int tHeight = textImg.getImageHeight();

        //photo image dimension
        int pWidth = photoImg.getImageWidth();
        int pHeight = photoImg.getImageHeight();
        int pTotalPixel = photoImg.getImageTotalPixels();

        for(int y = 0; y < tHeight; y++){
            for(int x = 0; x < tWidth; x++){
                //get text image RGB value
                int r = textImg.getRed(x, y);
                int g = textImg.getGreen(x, y);
                int b = textImg.getBlue(x, y);

                //check whether pixel is not WHITE
                if(r != 255 || g != 255 || b != 255){
                    if((x+y*pWidth)<= pTotalPixel){
                        //photo image exists below text image. take photo image RGB value
                        r = photoImg.getRed(x, y);
                        g = photoImg.getGreen(x, y);
                        b = photoImg.getBlue(x, y);

                        //set photo image pixel value to text image pixel value
                        textImg.setPixel(x, y, 255, r, g, b);
                    }
                }
            }
        }
    }
}//DYCanvas class ends here
