package JpgPng;

import dyimagefx.MyImage;
import dyimagefx.DYColor;

/**
 * File: DYMosaic.java
 *
 * Description:
 * This file contains methods to create random mosaic.
 *
 * @author Yusuf Shakeel
 * @version 1.0
 * Date: 21-02-2014 fri
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
public class DYMosaic {


    //////////////////////////////// MOSAIC Method /////////////////////////////

    /**
     * This will create the mosaic.
     *
     * @param img The image object.
     * @param DYColor The hex color code.
     * @param mSize the mosaic piece size.
     */

    public static void main(String[] args){
        int minIntensity[] = new int[3];

        MyImage iobj = new MyImage();
        iobj.readImage("/Users/x810we/Pictures/IMG_2027.jpg");

        _createMosaic(iobj, DYColor.Green_Color_Wheel_X11_green,220);

        iobj.writeImage("/Users/x810we/Pictures/Output-0.png");
    }



    private static void _createMosaic(MyImage img, int DYColor, int mSize){
        int r = (DYColor>>16)&0xFF;
        int g = (DYColor>>8)&0xFF;
        int b = DYColor&0xFF;

        int buff = 20;
        int lim = Math.max(Math.max(r, g), b);
        lim = (((255-lim)/2)>buff)?buff:((255-lim)/2);

        //image dimension
        int width = img.getImageWidth();
        int height = img.getImageHeight();

        for(int y = 0; y < height; y += mSize){
            for(int x = 0; x < width; x += mSize){
                int c;
                if(lim>0){
                    c = (int)(Math.random()*lim);
                    c = ((int)(Math.random()*2))==0?c:-1*c;
                }else{
                    lim = buff;
                    c = -1*(int)(Math.random()*lim);
                }

                int[] rgb = new int[3];
                rgb[0] = (c+r)>0?(c+r):r;
                rgb[1] = (c+g)>0?(c+g):g;
                rgb[2] = (c+b)>0?(c+b):b;

                for(int yi = 0; yi < mSize && y+yi < height; yi++){
                    for(int xi = 0; xi < mSize && x+xi < width; xi++){
                        img.setPixel(x+xi, y+yi, 255, rgb[0], rgb[1], rgb[2]);
                    }
                }
            }
        }
    }

    /**
     * This method will generate a random mosaic pattern.
     *
     * @param img The image object.
     * @param DYColor The Hex color code.
     * @param pieceSize The piece size. [Mosaic pieces will be in square shape]
     */
    public static void _random(MyImage img, int DYColor, int pieceSize){
        int r = (int)(Math.random()*256);
        int g = (int)(Math.random()*256);
        int b = (int)(Math.random()*256);
        int color = (255<<24) | (r<<16) | (g<<8) | b;
        _createMosaic(img, color, pieceSize);
    }

    /**
     * This method will generate a color mosaic pattern.
     *
     * @param img The image object.
     * @param DYColor The Hex color code.
     * @param pieceSize The piece size. [Mosaic pieces will be in square shape]
     */
    public static void myColorMosaic(MyImage img, int DYColor, int pieceSize){
        _createMosaic(img, DYColor, pieceSize);
    }

    /**
     * This method will generate a color mosaic pattern based on the RGB value entered.
     *
     * @param img The image object
     * @param r The red value [0-255].
     * @param g The green value [0-255].
     * @param b The blue value [0-255].
     * @param pieceSize The piece size. [Mosaic pieces will be in square shape]
     */
    public static void myRGBMosaic(MyImage img, int r, int g, int b, int pieceSize){
        int DYColor = (255<<24) | (r<<16) | (g<<8) | b;
        _createMosaic(img, DYColor, pieceSize);
    }
}//class Mosaic ends here