package Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GausTest {

    public static void main(String[] args) {

        GaussianBlur blur = new GaussianBlur();
        double[][] weights = GaussianBlur.getInstance().generateweightMatrix(3,Math.sqrt(3));
        System.out.println(GaussianBlur.getInstance().gaussianModel(1,1,3));
        double var = 1.5;
    }
}
