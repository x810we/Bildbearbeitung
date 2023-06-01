package Test;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Sepia{

    public static String longToIp(long i) {

        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);

    }



    private static void printPrettyBinary(String binary) {

        String s1 = String.format("%64s", binary).replace(' ', '0');
        System.out.format("%8s %8s %8s %8s %8s  %8s %8s %8s %n",
                s1.substring(0, 8), s1.substring(8, 16),
                s1.substring(16, 24), s1.substring(24, 32),
                s1.substring(32, 40),s1.substring(40, 48),
                s1.substring(48, 56),s1.substring(56, 64));
    }

    public static void main(String args[])throws IOException{
        BufferedImage img = null;
        File f = null;
        long ipAddress = 3232235778L;

        //read image
        try{
            f = new File("/Users/x810we/Pictures/TestBild-T-020.png");
            img = ImageIO.read(f);
        }catch(IOException e){
            System.out.println(e);
        }

        //get width and height of the image
        int width = img.getWidth();
        int height = img.getHeight();

        //convert to sepia
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p = img.getRGB(x,y);

                long ipAddressInLong = p;
                System.out.println(ipAddressInLong);
                String binary = Long.toBinaryString(ipAddressInLong);
                printPrettyBinary(binary);

                String ipAddressInString = longToIp(ipAddressInLong);
                System.out.println(ipAddressInString);
//                String ipAddressInString = "192.168.1.2";

                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                ipAddress = p;
                System.out.println("Alpha = " + ((ipAddress>>24) & 0xFF));
                System.out.println((ipAddress>>16) & 0xFF);
                System.out.println((ipAddress>>8) & 0xFF);
                System.out.println((ipAddress) & 0xFF);

                System.out.println("Alpha= " + a + " Rot = " + r + " Gruen= " + g + " Blau=" + b);

                //calculate tr, tg, tb

/*                int tr = (int)(0.393*r + 0.769*g + 0.189*b);
                int tg = (int)(0.349*r + 0.686*g + 0.168*b);
                int tb = (int)(0.272*r + 0.534*g + 0.131*b);

                //check condition
                if(tr > 255){
                    r = 255;
                }else{
                    r = tr;
                }

                if(tg > 255){
                    g = 255;
                }else{
                    g = tg;
                }

                if(tb > 255){
                    b = 255;
                }else{
                    b = tb;
                }
*/
                //set new RGB value

                a = 255;
                r = 19;
                g = 136;
                b = 17;
                p = (a<<24) | (r<<16) | (g<<8) | b;

                img.setRGB(x, y, p);
            }
        }

        //write image
        try{
            f = new File("/Users/x810we/Pictures/Output.png");
            ImageIO.write(img, "png", f);
        }catch(IOException e){
            System.out.println(e);
        }
    }//main() ends here
}//class ends here