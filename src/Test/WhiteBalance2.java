package Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WhiteBalance2 {
    public static float R1, G1, B1, FR, FG, FB;
    public static void main(String args[])throws IOException {
        BufferedImage img = null;
        File f = null;

        //read image
        try{
            f = new File("/Users/x810we/Pictures/Sony/automatischerImportordner/7R300149.ARW");
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
                /*
                if ((R1 -128) < 0) { R1 = 0;} else {R1 = R1 -128;};
                if ((G1 -128) < 0) { G1 = 0;} else {G1 = G1 -128;};
                if ((B1 -128) < 0) { B1 = 0;} else {B1 = B1 -128;};
                */
//              Berechnung der Faktoren
                FR = 161; FG = 148; FB = 159;


//                r = (int) Math.abs(R1*1.79);
                if ((R1*FG/FR) >= 255) { R1 = 255;} else {R1 = (float) (R1 * FG/FR);};
                if ((G1* 0.9) >= 255) { G1 = 255;} else {G1 = (float) (G1 * 0.9);};
                if ((B1*FG/FB) >= 255) { B1 = 255;} else {B1 = (float) (B1 * FG/FB);};

                r = (int) Math.abs(R1);
                g = (int) Math.abs(G1);
                b = (int) Math.abs(B1);
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
                p = (a<<24) | (r<<16) | (g<<8) | b;
                img.setRGB(x, y, p);
            }
        }

        //write image
        try{
            f = new File("/Users/x810we/Pictures/Sony/automatischerImportordner/7R300149-OUT.ARW");
            ImageIO.write(img, "png", f);
        }catch(IOException e){
            System.out.println(e);
        }
    }//main() ends here

}
