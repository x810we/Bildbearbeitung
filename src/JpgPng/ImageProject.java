package JpgPng;
/**
 * File: ImageProject.java
 *
 * Description:
 * This is a test file.
 *
 * @author Yusuf Shakeel
 * @version 1.0
 * Date: 26-01-2014 sun
 */

//import imageFX.*;
import dyimagefx.*;

public class ImageProject {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        test();
    }//main() ends here

    public static void test(){
        MyImage iobj = new MyImage(7000,4000);
        DYMosaic.myColorMosaic(iobj, DYColor.Ruby_red, 100);
        iobj.writeImage("/Users/x810we/Pictures/Mosaic-7000x4000.png");
    }
}//class ImageProject ends here