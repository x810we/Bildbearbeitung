package Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.BiFunction;

public class MaskGenUI2 extends JFrame {
    private BufferedImage srcImage, maskImage;

    public MaskGenUI2() {
        super("MaskGenUI mit Kurven-Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        initUI();
    }

    private void initUI() {
        JButton btnLoad = new JButton("Bild & Maske laden");
        JButton btnCurveThenMask = new JButton("Curve → Mask");
        JButton btnMaskThenCurve = new JButton("Mask → Curve");
        JPanel panel = new JPanel();
        panel.add(btnLoad);
        panel.add(btnCurveThenMask);
        panel.add(btnMaskThenCurve);
        add(panel, BorderLayout.NORTH);

        btnLoad.addActionListener(e -> {
            loadImageAndMask();
        });

        btnCurveThenMask.addActionListener(e -> {
            if (srcImage == null || maskImage == null) return;
            BufferedImage result = applyToAllPixels(this::applyCurveThenMask);
            showImageDialog(result, "Curve → Mask Ergebnis");
        });

        btnMaskThenCurve.addActionListener(e -> {
            if (srcImage == null || maskImage == null) return;
            BufferedImage result = applyToAllPixels(this::applyMaskThenCurve);
            showImageDialog(result, "Mask → Curve Ergebnis");
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadImageAndMask() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            srcImage = ImageIO.read(fc.getSelectedFile());
            if (srcImage == null) throw new Exception("Ungültiges Bild");
            // Maske laden:
            fc.setSelectedFile(null);
            fc.setDialogTitle("Maske wählen (8-Bit Graustufen PNG)");
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
            maskImage = ImageIO.read(fc.getSelectedFile());
            if (maskImage == null) throw new Exception("Ungültige Maske");
            JOptionPane.showMessageDialog(this, "Bild & Maske geladen", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Laden: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BufferedImage applyToAllPixels(BiFunction<Integer,Integer,Integer> func) {
        int w = srcImage.getWidth(), h = srcImage.getHeight();
        BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int orig = srcImage.getRGB(x,y) & 0xFF;
            int mask = maskImage.getRGB(x,y) & 0xFF;
            int out = func.apply(orig, mask);
            int rgba = (0xFF<<24) | (out<<16) | (out<<8) | out;
            res.setRGB(x, y, rgba);
        }
        return res;
    }

    private int applyCurveThenMask(int orig, int maskVal) {
        double x = orig / 255.0;
        double y = 1.0 / (1.0 + Math.exp(-12 * (x - 0.1)));
        int adj = (int) Math.round(y * 255);
        double m = maskVal / 255.0;
        double out = (1 - m) * orig + m * adj;
        return (int) Math.round(out);
    }

    private int applyMaskThenCurve(int orig, int maskVal) {
        double mx = maskVal / 255.0;
        double my = 1.0 / (1.0 + Math.exp(-12 * (mx - 0.5)));
        double m = my;
        double x = orig / 255.0;
        double y = 1.0 / (1.0 + Math.exp(-12 * (x - 0.5)));
        int adj = (int) Math.round(y * 255);
        double out = (1 - m) * orig + m * adj;
        return (int) Math.round(out);
    }

    private void showImageDialog(BufferedImage img, String title) {
        JDialog d = new JDialog(this, title, true);
        d.getContentPane().add(new JScrollPane(new JLabel(new ImageIcon(img))));
        d.setSize(Math.min(800, img.getWidth()), Math.min(600, img.getHeight()));
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MaskGenUI2::new);
    }
}
