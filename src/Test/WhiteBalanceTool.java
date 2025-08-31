package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class WhiteBalanceTool extends JFrame {

    private BufferedImage originalImage;
    private BufferedImage correctedImage;
    private JLabel imageLabel;

    public WhiteBalanceTool() {
        setTitle("Weißabgleich Tool");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Bild laden");
        JButton saveButton = new JButton("Bild speichern");

        loadButton.addActionListener(e -> loadImage());
        saveButton.addActionListener(e -> saveImage());

        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.NORTH);

        imageLabel = new JLabel("", JLabel.CENTER);
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (originalImage != null) {
                    int x = e.getX();
                    int y = e.getY();
                    if (x >= 0 && x < originalImage.getWidth() && y >= 0 && y < originalImage.getHeight()) {
                        performWhiteBalance(x, y);
                    }
                }
            }
        });

        add(new JScrollPane(imageLabel), BorderLayout.CENTER);
    }

    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                originalImage = ImageIO.read(chooser.getSelectedFile());
                correctedImage = null;
                imageLabel.setIcon(new ImageIcon(originalImage));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Laden des Bildes.");
            }
        }
    }

    private void performWhiteBalance(int x, int y) {
        int rgb = originalImage.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        double gray = (r + g + b) / 3.0;
        double rFactor = gray / r;
        double gFactor = gray / g;
        double bFactor = gray / b;

        correctedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                int pixel = originalImage.getRGB(i, j);
                int pr = (pixel >> 16) & 0xFF;
                int pg = (pixel >> 8) & 0xFF;
                int pb = pixel & 0xFF;

                int nr = (int) Math.min(255, pr * rFactor);
                int ng = (int) Math.min(255, pg * gFactor);
                int nb = (int) Math.min(255, pb * bFactor);

                correctedImage.setRGB(i, j, (nr << 16) | (ng << 8) | nb);
            }
        }

        imageLabel.setIcon(new ImageIcon(correctedImage));
    }

    private void saveImage() {
        if (correctedImage == null) {
            JOptionPane.showMessageDialog(this, "Bitte zuerst einen Weißabgleich durchführen!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Speichern unter...");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                ImageIO.write(correctedImage, "png", file);
                JOptionPane.showMessageDialog(this, "Bild gespeichert: " + file.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Fehler beim Speichern des Bildes.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WhiteBalanceTool().setVisible(true));
    }
}
