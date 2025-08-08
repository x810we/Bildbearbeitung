package Test;

import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.File;

public class SeamCarvingWithMask {
    static final double HIGH_ENERGY = 1e6;

    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("input.png"));
        BufferedImage mask = ImageIO.read(new File("mask.png")); // weiß = Schutzbereich

        int newHeight = (int)(img.getHeight() * 1.2); // +20 %
        while (img.getHeight() < newHeight) {
            double[][] energy = computeEnergy(img);
            applyMaskEnergy(energy, mask);
            int[] seam = findHorizontalSeam(energy);
            img = insertHorizontalSeam(img, seam);
            mask = insertHorizontalSeam(mask, seam);
        }
        ImageIO.write(img, "png", new File("stretched.png"));
        ImageIO.write(mask, "png", new File("mask_out.png"));
    }

    static double[][] computeEnergy(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        double[][] e = new double[h][w];
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                Color c = new Color(img.getRGB(x,y));
                Color cx1 = new Color(img.getRGB(x+1, y));
                Color cx0 = new Color(img.getRGB(x-1, y));
                Color cy1 = new Color(img.getRGB(x, y+1));
                Color cy0 = new Color(img.getRGB(x, y-1));
                double dx = Math.pow(cx1.getRed() - cx0.getRed(), 2)
                        + Math.pow(cx1.getGreen() - cx0.getGreen(), 2)
                        + Math.pow(cx1.getBlue() - cx0.getBlue(), 2);
                double dy = Math.pow(cy1.getRed() - cy0.getRed(), 2)
                        + Math.pow(cy1.getGreen() - cy0.getGreen(), 2)
                        + Math.pow(cy1.getBlue() - cy0.getBlue(), 2);
                e[y][x] = Math.sqrt(dx + dy);
            }
        }
        return e;
    }

    static void applyMaskEnergy(double[][] energy, BufferedImage mask) {
        for (int y = 0; y < energy.length; y++) {
            for (int x = 0; x < energy[0].length; x++) {
                if ((mask.getRGB(x,y) & 0xFFFFFF) == 0xFFFFFF) {
                    energy[y][x] += HIGH_ENERGY;
                }
            }
        }
    }

    static int[] findHorizontalSeam(double[][] energy) {
        int h = energy.length, w = energy[0].length;
        double[][] M = new double[h][w];
        int[][] back = new int[h][w];
        for (int x = 0; x < w; x++) M[0][x] = energy[0][x];
        for (int y = 1; y < h; y++) for (int x = 0; x < w; x++) {
            double min = M[y-1][x]; int bx = x;
            if (x > 0 && M[y-1][x-1] < min) { min = M[y-1][x-1]; bx = x-1; }
            if (x < w-1 && M[y-1][x+1] < min) { min = M[y-1][x+1]; bx = x+1; }
            M[y][x] = energy[y][x] + min;
            back[y][x] = bx;
        }
        double min = Double.MAX_VALUE; int mx = 0;
        for (int x = 0; x < w; x++) if (M[h-1][x] < min) { min = M[h-1][x]; mx = x; }
        int[] seam = new int[h];
        for (int y = h - 1; y >= 0; y--) {
            seam[y] = mx; mx = back[y][mx];
        }
        return seam;
    }

    static BufferedImage insertHorizontalSeam(BufferedImage img, int[] seam) {
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage result = new BufferedImage(w, h + 1, img.getType());
        for (int y = 0; y < h; y++) {
            int sx = seam[y];
            for (int x = 0; x <= w; x++) {
                if (x < sx) result.setRGB(x, y, img.getRGB(x, y));
                else if (x == sx) {
                    result.setRGB(x, y, img.getRGB(x, y));
                    result.setRGB(x, y + 1, img.getRGB(x, y));
                } else result.setRGB(x, y + 1, img.getRGB(x - 1, y));
            }
        }
        // letzte Zeile kopieren
        for (int x = 0; x < w; x++) result.setRGB(x, h, img.getRGB(x, h - 1));
        return result;
    }
}
