package Test;

/**
 * File: Grayscale.java
 *
 * Description:
 * Convert color image into grayscale image.
 *
 * @author Yusuf Shakeel
 * Date: 26-01-2014 sun
 *
 * www.github.com/yusufshakeel/Java-Image-Processing-Project
 */

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class AufhellenZweiEbenen{
    public static void main(String args[])throws IOException{
        BufferedImage img1 = null, img2 = null,  img3 = null;
        File f = null;

        //read image
        try{
            f = new File("/Users/x810we/Pictures/FB/FarbchartEbenenmodi.png");
            img1 = ImageIO.read(f);
        }catch(IOException e){
            System.out.println(e);
        }
        try{
            f = new File("/Users/x810we/Pictures/FB/Grauchart.png");
            img2 = ImageIO.read(f);
        }catch(IOException e){
            System.out.println(e);
        }
        img3 = img1;
        //get image width and height
        int width = img1.getWidth();
        int height = img1.getHeight();

        //convert to grayscale
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p1 = img1.getRGB(x,y);
                int p2 = img2.getRGB(x,y);

                int a1 = (p1>>24)&0xff;
                int r1 = (p1>>16)&0xff;
                int g1 = (p1>>8)&0xff;
                int b1 = p1&0xff;

                int a2 = (p2>>24)&0xff;
                int r2 = (p2>>16)&0xff;
                int g2 = (p2>>8)&0xff;
                int b2 = p2&0xff;

                if (r1 < r2) {r1 = r2;};
                if (b1 < b2) {b1 = b2;};
                if (g1 < g2) {g1 = g2;};

                p1 = (a1<<24) | (r1<<16) | (g1<<8) | b1;

                //replace RGB value with avg
                //p = (a<<24) | (avg<<16) | (avg<<8) | avg;

                img3.setRGB(x, y, p1);
            }
        }

        //write image
        try{
            f = new File("/Users/x810we/Pictures/FB/Output-FarbchartEbenenmodi.png");
            ImageIO.write(img3, "png", f);
        }catch(IOException e){
            System.out.println(e);
        }
    }//main() ends here
}//class ends here