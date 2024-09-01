package Test;

import java.awt.Color;
public class RGBtoHSB {
    public static void main (String[] args) {
// Beispiel-RGB-Werte
        int r = 255; // Rotwert (0-255)
        int g = 100; // Grünwert (0-255)
        int b = 50; // Blauwert (0-255)
// Umrechnung von RGB nach HSB
        float[] hsbValues = Color.RGBtoHSB(r, g, b, null);
// Ausgeben der HSB-Werte
        float hue = hsbValues [0];
// Farbton (0.0 - 1.0)
        float saturation = hsbValues[1]; // Sättigung (0.0 - 1.0)
        float brightness = hsbValues [2]; // Helligkeit (0.0 - 1.0)
        System.out.println("Farbton (Hue): " + hue * 360);
        System.out.println("Sättigung (Saturation): " + saturation * 100 + "%");
        System.out.println("Helligkeit (Brightness): " + brightness * 100 + "©");
    }
}