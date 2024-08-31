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

public class Aufhellen{
    public static void main(String args[])throws IOException{
        BufferedImage img = null;
        File f = null;

        //read image
        try{
            f = new File("/Users/x810we/Pictures/Sophie2024.jpg");
            img = ImageIO.read(f);
        }catch(IOException e){
            System.out.println(e);
        }

        //get image width and height
        int width = img.getWidth();
        int height = img.getHeight();

        //convert to grayscale
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p = img.getRGB(x,y);

                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                //calculate average
                int avg = (r+g+b)/3;

                int FG = (r+g+b)/3;

/*                double LumiB  = 0.299* 128 + 0.587*128 + 0.114*128;
                double LumiA  = 0.299* r + 0.587*g + 0.114*b;
*/
                if (r <= 128) {r = 128;};
                if (b <= 128) {b = 128;};
                if (g <= 128) {g = 128;};


/*              if (FG >= 128) {r = r - 128;};
                if (FG >= 128) {r = g - 128;};
                if (FG >= 128) {r = b - 128;};
*/
//              r = r-128; 	if (r <= 0) {r = 0;}
//              g = g-128; 	if (g <= 0) {g = 0;}
//             	b = b-128;	if (b <= 0) {b = 0;}

                a = 255;
//              r = Math.abs(r-128);

//              g = Math.abs(g-128);
//              b = Math.abs(b-128);
//
//              g = 136;
//              b = 17;
                p = (a<<24) | (r<<16) | (g<<8) | b;

                //replace RGB value with avg
                //p = (a<<24) | (avg<<16) | (avg<<8) | avg;

                img.setRGB(x, y, p);
            }
        }

        //write image
        try{
            f = new File("/Users/x810we/Pictures/TestBild111-e.png");
            ImageIO.write(img, "png", f);
        }catch(IOException e){
            System.out.println(e);
        }
    }//main() ends here
}//class ends here