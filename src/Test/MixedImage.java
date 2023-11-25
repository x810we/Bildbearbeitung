package Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MixedImage {
 public static float R1, G1, B1;
    public static void main(String args[])throws IOException {
        BufferedImage img = null;
        File f = null;

        //read image
        try{
            f = new File("/Users/x810we/Pictures/TestBild-T-100.png");
            img = ImageIO.read(f);
        }catch(IOException e){
            System.out.println(e);
        }


        //get width and height
        int width = img.getWidth();
        int height = img.getHeight();

        for (int y = 0; y< height; y++){
            int p = img.getRGB(0, y);
            int a = (p>>24)&0xff;
            int b = p&0xff;

        }

        //convert to blue image
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p = img.getRGB(x,y);

                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                R1 = r;
                G1 = g;
                B1 = b;

                R1 = (R1/255 * R1/255) * 255;
                G1 = (G1/255 * G1/255) * 255;
                B1 = (B1/255 * B1/255) * 255;

                r = (int) (R1 + 0.5);
                g = (int) (G1 + 0.5);
                b = (int) (B1 + 0.5);


            //    g = (g/255 * g/255) * 255;
            //    b = (b/255 * b/255) * 255;

                //set new RGB
                p = (a<<24) | (r<<16) | (g<<8) | b;
                img.setRGB(x, y, p);
            }
        }

        //write image
        try{
            f = new File("/Users/x810we/Pictures/output.png");
            ImageIO.write(img, "png", f);
        }catch(IOException e){
            System.out.println(e);
        }
    }//main() ends here

}
