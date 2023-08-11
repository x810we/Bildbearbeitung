package Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

public class TEST001 {
    public static float R1, G1, B1, FR, FG, FB;
    public static void main(String args[])throws IOException {
        BufferedImage img = null;
        File f = null;
        int rot = 0;
        int blau = 0;

        //read image
        try{
            f = new File("/Users/x810we/Pictures/TEST001-32Bit.png");
            img = ImageIO.read(f);
        }catch(IOException e){
            System.out.println(e);
        }

        //get width and height
        int width = img.getWidth();
        int height = img.getHeight();

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

                ColorModel colorModel = img.getColorModel();
                Graphics graphics = img.getGraphics();

                int rgb22 = img.getRGB(rot, blau);
                Graphics2D graphics1 = img.createGraphics();
                /*
                if ((R1 -128) < 0) { R1 = 0;} else {R1 = R1 -128;};
                if ((G1 -128) < 0) { G1 = 0;} else {G1 = G1 -128;};
                if ((B1 -128) < 0) { B1 = 0;} else {B1 = B1 -128;};
                */
//              Berechnung der Faktoren
                FR = 131; FG = 128; FB = 119;


//                r = (int) Math.abs(R1*1.79);
                if ((R1*1.53) >= 255) { R1 = 255;} else {R1 = (float) (R1 * 1.53);};
                if ((G1*1.3) >= 255) { G1 = 255;} else {G1 = (float) (G1 * 1.15);};
                if ((B1*1.0) >= 255) { B1 = 255;} else {B1 = (float) (B1 * 0.95107);};

                r = (int) Math.abs(R1*0.925);
                g = (int) Math.abs(G1*0.925);
                b = (int) Math.abs(B1*0.925);
/*
                R1 = (R1/255 * R1/255) * 255;
                G1 = (G1/255 * G1/255) * 255;
                B1 = (B1/255 * B1/255) * 255;


                r = (int) (R1 + 0.5);
                g = (int) (G1 + 0.5);
                b = (int) (B1 + 0.5);
*/

                //    g = (g/255 * g/255) * 255;
                //    b = (b/255 * b/255) * 255;

                //set new RGB
                if (x < 80) {r = y;g = 0;b = 0;} else
                    if (x >= 80 && x < 160) {g = y;r = 0;b = 0;} else
                        if (x >= 160 && x < 256) {b = y;r = 0; g = 0;};
                //g = 0; b = y;
                a = 255;

                p = (a<<24) | (r<<16) | (g<<8) | b;
                img.setRGB(x, y, p);
            }
        }

        //write image
        try{
            f = new File("/Users/x810we/Pictures/TEST001-32Bit-OUT.PNG");
            ImageIO.write(img, "png", f);
        }catch(IOException e){
            System.out.println(e);
        }
    }//main() ends here

}
