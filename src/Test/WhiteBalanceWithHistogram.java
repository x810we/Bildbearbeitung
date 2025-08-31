package Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WhiteBalanceWithHistogram extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage adjustedImage;
    private JLabel imageLabel;

    public WhiteBalanceWithHistogram() {
        setTitle("Weißabgleich mit Histogramm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton openButton = new JButton("Bild öffnen");
        JButton saveButton = new JButton("Bild speichern");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);

        imageLabel = new JLabel("", JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        openButton.addActionListener(e -> openImage());
        saveButton.addActionListener(e -> saveImage());

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (adjustedImage != null) {
                    int x = e.getX() * adjustedImage.getWidth() / imageLabel.getWidth();
                    int y = e.getY() * adjustedImage.getHeight() / imageLabel.getHeight();
                    applyWhiteBalance(x, y);
                }
            }
        });

        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home"), "Pictures"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                originalImage = ImageIO.read(chooser.getSelectedFile());
                adjustedImage = deepCopy(originalImage);
                imageLabel.setIcon(new ImageIcon(adjustedImage));
                showHistogram(originalImage, "Histogramm (Original)");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Laden des Bildes.");
            }
        }
    }

    private void saveImage() {
        if (adjustedImage == null) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("corrected_image.png"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIO.write(adjustedImage, "png", chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Bild gespeichert!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Speichern des Bildes.");
            }
        }
    }

    private void applyWhiteBalance(int x, int y) {
        int rgb = adjustedImage.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        double gray = (r + g + b) / 3.0;
        double scaleR = gray / r;
        double scaleG = gray / g;
        double scaleB = gray / b;

        for (int i = 0; i < adjustedImage.getWidth(); i++) {
            for (int j = 0; j < adjustedImage.getHeight(); j++) {
                int pixel = originalImage.getRGB(i, j);
                int pr = (int) Math.min(255, ((pixel >> 16) & 0xFF) * scaleR);
                int pg = (int) Math.min(255, ((pixel >> 8) & 0xFF) * scaleG);
                int pb = (int) Math.min(255, (pixel & 0xFF) * scaleB);
                adjustedImage.setRGB(i, j, (0xFF << 24) | (pr << 16) | (pg << 8) | pb);
            }
        }
        imageLabel.setIcon(new ImageIcon(adjustedImage));
        showHistogram(adjustedImage, "Histogramm (Nach Weißabgleich)");
    }

    private void showHistogram(BufferedImage img, String title) {
        int[] red = new int[256];
        int[] green = new int[256];
        int[] blue = new int[256];

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                red[(rgb >> 16) & 0xFF]++;
                green[(rgb >> 8) & 0xFF]++;
                blue[rgb & 0xFF]++;
            }
        }

        JFrame histFrame = new JFrame(title);
        histFrame.setSize(600, 400);
        histFrame.add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int width = getWidth();
                int height = getHeight();

                int maxCount = Math.max(
                        Math.max(getMax(red), getMax(green)),
                        getMax(blue));

                for (int i = 0; i < 256; i++) {
                    int rHeight = (int) ((red[i] / (double) maxCount) * height);
                    int gHeight = (int) ((green[i] / (double) maxCount) * height);
                    int bHeight = (int) ((blue[i] / (double) maxCount) * height);

                    g.setColor(Color.RED);
                    g.drawLine(i * width / 256, height, i * width / 256, height - rHeight);

                    g.setColor(Color.GREEN);
                    g.drawLine(i * width / 256, height, i * width / 256, height - gHeight);

                    g.setColor(Color.BLUE);
                    g.drawLine(i * width / 256, height, i * width / 256, height - bHeight);
                }
            }
        });

        histFrame.setVisible(true);
    }

    private int getMax(int[] array) {
        int max = 0;
        for (int value : array) if (value > max) max = value;
        return max;
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        Graphics g = copy.getGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return copy;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WhiteBalanceWithHistogram::new);
    }
}
