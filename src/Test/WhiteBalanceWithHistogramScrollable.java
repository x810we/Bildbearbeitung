package Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class WhiteBalanceWithHistogramScrollable extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage correctedImage;
    private JLabel imageLabel;
    private JScrollPane scrollPane;

    public WhiteBalanceWithHistogramScrollable() {
        super("Weißabgleich mit Histogramm (mit Scroll)");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());

        imageLabel = new JLabel("Kein Bild geladen", SwingConstants.CENTER);
        scrollPane = new JScrollPane(imageLabel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Bild laden");
        JButton saveButton = new JButton("Ergebnis speichern");
        JButton histogramButton = new JButton("Histogramm anzeigen");

        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(histogramButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadImage());
        saveButton.addActionListener(e -> saveCorrectedImage());
        histogramButton.addActionListener(e -> {
            if (correctedImage != null) {
                showHistogram(correctedImage, "Histogramm des korrigierten Bildes");
            } else if (originalImage != null) {
                showHistogram(originalImage, "Histogramm des Originalbildes");
            } else {
                JOptionPane.showMessageDialog(this, "Kein Bild geladen!");
            }
        });

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (originalImage != null) {
                    // Mauskoordinaten relativ zur Bildgröße skalieren
                    int x = e.getX() * originalImage.getWidth() / imageLabel.getWidth();
                    int y = e.getY() * originalImage.getHeight() / imageLabel.getHeight();
                    applyWhiteBalance(x, y);
                }
            }
        });

        setVisible(true);
    }

    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                originalImage = ImageIO.read(chooser.getSelectedFile());
                correctedImage = null;
                updateImageDisplay(originalImage);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Laden: " + ex.getMessage());
            }
        }
    }

    private void saveCorrectedImage() {
        if (correctedImage == null) {
            JOptionPane.showMessageDialog(this, "Kein korrigiertes Bild vorhanden!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIO.write(correctedImage, "png", chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Bild gespeichert!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Speichern: " + ex.getMessage());
            }
        }
    }

    private void applyWhiteBalance(int x, int y) {
        int rgb = originalImage.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        double avg = (r + g + b) / 3.0;
        double rScale = avg / r;
        double gScale = avg / g;
        double bScale = avg / b;

        correctedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                int pixel = originalImage.getRGB(i, j);
                int pr = (pixel >> 16) & 0xFF;
                int pg = (pixel >> 8) & 0xFF;
                int pb = pixel & 0xFF;
                int pa = (pixel >> 24) & 0xFF;

                int nr = (int) Math.min(255, pr * rScale);
                int ng = (int) Math.min(255, pg * gScale);
                int nb = (int) Math.min(255, pb * bScale);

                int newRGB = (pa << 24) | (nr << 16) | (ng << 8) | nb;
                correctedImage.setRGB(i, j, newRGB);
            }
        }
        updateImageDisplay(correctedImage);
    }

    private void updateImageDisplay(BufferedImage img) {
        imageLabel.setIcon(new ImageIcon(img));
        imageLabel.setText("");
        imageLabel.revalidate();
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
        histFrame.setSize(700, 450);
        histFrame.add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int width = getWidth();
                int height = getHeight();
                int paddingLeft = 50;
                int paddingBottom = 40;
                int graphWidth = width - paddingLeft - 20;
                int graphHeight = height - paddingBottom - 20;

                int maxCount = Math.max(Math.max(getMax(red), getMax(green)), getMax(blue));

                g.setColor(Color.BLACK);
                g.drawLine(paddingLeft, height - paddingBottom, paddingLeft + graphWidth, height - paddingBottom);
                g.drawLine(paddingLeft, height - paddingBottom, paddingLeft, height - paddingBottom - graphHeight);

                for (int i = 0; i <= 4; i++) {
                    int y = height - paddingBottom - (i * graphHeight / 4);
                    int value = (int) (maxCount * (i / 4.0));
                    g.drawLine(paddingLeft - 5, y, paddingLeft, y);
                    g.drawString(String.valueOf(value), 5, y + 5);
                }
                g.drawString("Pixelanzahl n", 5, 20);

                for (int i = 0; i <= 4; i++) {
                    int x = paddingLeft + (i * graphWidth / 4);
                    int value = (int) (i * 100 / 4.0);
                    g.drawLine(x, height - paddingBottom, x, height - paddingBottom + 5);
                    g.drawString(value + "%", x - 10, height - paddingBottom + 20);
                }
                g.drawString("Helligkeit [%]", width / 2 - 30, height - 10);

                for (int i = 0; i < 256; i++) {
                    int x = paddingLeft + (i * graphWidth / 256);
                    int rHeight = (int) ((red[i] / (double) maxCount) * graphHeight);
                    int gHeight = (int) ((green[i] / (double) maxCount) * graphHeight);
                    int bHeight = (int) ((blue[i] / (double) maxCount) * graphHeight);

                    g.setColor(Color.RED);
                    g.drawLine(x, height - paddingBottom, x, height - paddingBottom - rHeight);
                    g.setColor(Color.GREEN);
                    g.drawLine(x, height - paddingBottom, x, height - paddingBottom - gHeight);
                    g.setColor(Color.BLUE);
                    g.drawLine(x, height - paddingBottom, x, height - paddingBottom - bHeight);
                }
            }
        });

        histFrame.setVisible(true);
    }

    private int getMax(int[] array) {
        int max = 0;
        for (int value : array) {
            max = Math.max(max, value);
        }
        return max;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WhiteBalanceWithHistogramScrollable::new);
    }
}
