package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class ImageCalculationDialog extends JFrame {
    private JTextField rField, gField, bField, alphaField, scaleField, offsetField;
    private JButton calculateButton;

    private JLabel calculateLable;

    public ImageCalculationDialog() {
        setTitle("Image Calculation RGB Channels");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2));
        setIconImage("/Users/x810we/Pictures/FB/Grauchart.png");

        // Labels
        add(new JLabel("Red:"));
        add(new JLabel("Green:"));
        add(new JLabel("Blue:"));
        add(new JLabel("Alpha:"));
        add(new JLabel("Scale:"));
        add(new JLabel("Offset:"));

        // Text fields for RGB and Alpha input
        rField = new JTextField();
        gField = new JTextField();
        bField = new JTextField();
        alphaField = new JTextField();
        scaleField = new JTextField("1.0"); // default scale 1.0
        offsetField = new JTextField("0");  // default offset 0

        add(rField);
        add(gField);
        add(bField);
        add(alphaField);
        add(scaleField);
        add(offsetField);

        // Calculate button
        calculateButton = new JButton("Calculate");
        add(calculateButton);

        // Action listener for the calculate button
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int r = Integer.parseInt(rField.getText());
                    int g = Integer.parseInt(gField.getText());
                    int b = Integer.parseInt(bField.getText());
                    int alpha = Integer.parseInt(alphaField.getText());
                    double scale = Double.parseDouble(scaleField.getText());
                    int offset = Integer.parseInt(offsetField.getText());

                    // Call your image calculation logic here
                    ImageCalculationRGBChannels calculation = new ImageCalculationRGBChannels();
                    calculation.applyCalculation(r, g, b, alpha, scale, offset);

                    JOptionPane.showMessageDialog(null, "Calculation completed successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        setVisible(true);
    }

    private void setIconImage(String s) {
    }

    public static void main(String[] args) {

        new ImageCalculationDialog();
        System.out.println("Programm beendet");
    }
}

class ImageCalculationRGBChannels {

    public void applyCalculation(int r, int g, int b, int alpha, double scale, int offset) throws IOException {
        // Example logic - integrate your actual calculation here
        System.out.println("Calculating with RGB: (" + r + ", " + g + ", " + b + "), Alpha: " + alpha);
        System.out.println("Scale: " + scale + ", Offset: " + offset);

        BufferedImage image1 = ImageIO.read(new File("image1.png"));
        BufferedImage image2 = ImageIO.read(new File("image2.png"));

        BufferedImage result = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_ARGB);

        //int offset = 50;   Example offset value
        //float scale = 1.0f;   Example scale value

        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                int rgb1 = image1.getRGB(x, y);
                int rgb2 = image2.getRGB(x, y);

                // Extract individual channels (ARGB)
                int a1 = (rgb1 >> 24) & 0xFF;
                int r1 = (rgb1 >> 16) & 0xFF;
                int g1 = (rgb1 >> 8) & 0xFF;
                int b1 = rgb1 & 0xFF;

                int a2 = (rgb2 >> 24) & 0xFF;
                int r2 = (rgb2 >> 16) & 0xFF;
                int g2 = (rgb2 >> 8) & 0xFF;
                int b2 = rgb2 & 0xFF;

                // Example: Applying subtraction or addition to individual channels
                int resultR = Math.min(255, Math.max(0, (int) ((r1 + r2) * scale + offset)));
                int resultG = Math.min(255, Math.max(0, (int) ((g1 + g2) * scale + offset)));
                int resultB = Math.min(255, Math.max(0, (int) ((b1 + b2) * scale + offset)));
                int resultA = Math.min(255, Math.max(0, (int) ((a1 + a2) * scale + offset)));

                // Combine back the channels into a single ARGB value
                int newRGB = (resultA << 24) | (resultR << 16) | (resultG << 8) | resultB;
                result.setRGB(x, y, newRGB);

                // Example output based on the inputs - add your image calculation here
                int calculatedR = (int) Math.min(255, Math.max(0, (r * scale + offset)));
                int calculatedG = (int) Math.min(255, Math.max(0, (g * scale + offset)));
                int calculatedB = (int) Math.min(255, Math.max(0, (b * scale + offset)));

                System.out.println("Calculated RGB: (" + calculatedR + ", " + calculatedG + ", " + calculatedB + ")");
            }
        }
    }
}