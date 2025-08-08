package Test;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageBlenderGUI extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private BufferedImage image1;
    private BufferedImage image2;
    private BufferedImage blendedImage;
    private final JLabel imageLabel;
    private final JSlider opacitySlider;
    private final JButton saveButton;

    public ImageBlenderGUI() {
        setTitle("Bildmischer");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton loadButton1 = new JButton("Bild 1 laden");
        JButton loadButton2 = new JButton("Bild 2 laden");
        JButton blendButton = new JButton("Bilder mischen");
        saveButton = new JButton("Bild speichern");
        saveButton.setEnabled(false);

        opacitySlider = new JSlider(0, 100, 50);
        opacitySlider.setMajorTickSpacing(25);
        opacitySlider.setPaintTicks(true);
        opacitySlider.setPaintLabels(true);

        JPanel topPanel = new JPanel();
        topPanel.add(loadButton1);
        topPanel.add(loadButton2);
        topPanel.add(new JLabel("Opacity:"));
        topPanel.add(opacitySlider);
        topPanel.add(blendButton);
        topPanel.add(saveButton);

        imageLabel = new JLabel("", SwingConstants.CENTER);
        JScrollPane scrollPane = new JScrollPane(imageLabel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadButton1.addActionListener(e -> {
            BufferedImage loaded = loadImage();
            if (loaded != null) {
                image1 = loaded;
                imageLabel.setIcon(new ImageIcon(image1));
            }
        });
        loadButton2.addActionListener(e -> {
            BufferedImage loaded = loadImage();
            if (loaded != null) {
                image2 = loaded;
                imageLabel.setIcon(new ImageIcon(image2));
            }
        });

        blendButton.addActionListener(e -> {
            if (image1 != null && image2 != null) {
                float opacity = opacitySlider.getValue() / 100f;
                blendedImage = blendImages(image1, image2, opacity);
                imageLabel.setIcon(new ImageIcon(blendedImage));
                saveButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Bitte beide Bilder laden!");
            }
        });

        saveButton.addActionListener(e -> {
            if (blendedImage != null) {
                saveImage(blendedImage);
            }
        });

        setVisible(true);
    }

    @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
    private BufferedImage loadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled(false);
        chooser.setCurrentDirectory(new File(System.getProperty("user.home"), "Pictures"));
        chooser.setFileFilter(new FileNameExtensionFilter("Bilddateien (PNG, JPG)", "png", "jpg", "jpeg"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                return ImageIO.read(chooser.getSelectedFile());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Laden: " + ex.getMessage());
            }
        }
        return null;
    }

    @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
    private void saveImage(BufferedImage image) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled(false);
        chooser.setCurrentDirectory(new File(System.getProperty("user.home"), "Pictures"));
        chooser.setDialogTitle("Gemischtes Bild speichern");
        chooser.setSelectedFile(new File("blended.png"));
        chooser.setFileFilter(new FileNameExtensionFilter("PNG-Bild", "png"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            // Füge .png-Erweiterung hinzu, falls nicht vorhanden
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            try {
                ImageIO.write(image, "png", file);
                JOptionPane.showMessageDialog(this, "Bild gespeichert: " + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Speichern: " + ex.getMessage());
            }
        }
    }

    private BufferedImage blendImages(BufferedImage img1, BufferedImage img2, float opacity) {
        int width = Math.min(img1.getWidth(), img2.getWidth());
        int height = Math.min(img1.getHeight(), img2.getHeight());

        BufferedImage blended = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                Color c1 = new Color(rgb1, true);
                Color c2 = new Color(rgb2, true);

                int r = (int) (c1.getRed() * (1 - opacity) + c2.getRed() * opacity);
                int g = (int) (c1.getGreen() * (1 - opacity) + c2.getGreen() * opacity);
                int b = (int) (c1.getBlue() * (1 - opacity) + c2.getBlue() * opacity);
                int a = (int) (c1.getAlpha() * (1 - opacity) + c2.getAlpha() * opacity);

                Color blendedColor = new Color(r, g, b, a);
                blended.setRGB(x, y, blendedColor.getRGB());
            }
        }

        return blended;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageBlenderGUI::new);
    }
}
