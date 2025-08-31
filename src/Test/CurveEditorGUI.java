package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.event.ChangeListener;

public class CurveEditorGUI extends JFrame {
    private final BufferedImage srcImage, dstImage;
    private final RGBCurvePanel rgbCurvePanel;
    private final JLabel imgLabel;

    public CurveEditorGUI(BufferedImage img) {
        super("Interaktiver RGB-Kurveneditor");
        this.srcImage = img;
        this.dstImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        imgLabel = new JLabel(new ImageIcon(dstImage));
        rgbCurvePanel = new RGBCurvePanel();
        rgbCurvePanel.setPreferredSize(new Dimension(256, 256));

        rgbCurvePanel.addLUTChangeListener((lutR, lutG, lutB) -> {
            applyLUT(lutR, lutG, lutB);
            imgLabel.setIcon(new ImageIcon(dstImage));
        });

        setLayout(new BorderLayout());
        add(new JScrollPane(imgLabel), BorderLayout.CENTER);
        add(rgbCurvePanel, BorderLayout.EAST);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        rgbCurvePanel.resetIdentity();
    }

    private void applyLUT(int[] lutR, int[] lutG, int[] lutB) {
        for (int y = 0; y < srcImage.getHeight(); y++) {
            for (int x = 0; x < srcImage.getWidth(); x++) {
                int c = srcImage.getRGB(x, y);
                int a = (c >> 24) & 0xFF;
                int r = lutR[(c >> 16) & 0xFF];
                int g = lutG[(c >> 8) & 0xFF];
                int b = lutB[c & 0xFF];
                dstImage.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("/Users/x810we/Pictures/IMG_0125.JPG"));
        SwingUtilities.invokeLater(() -> new CurveEditorGUI(img));
    }
}

class RGBCurvePanel extends JPanel {
    private final List<Point> redPoints = new ArrayList<>();
    private final List<Point> greenPoints = new ArrayList<>();
    private final List<Point> bluePoints = new ArrayList<>();
    private final List<LUTChangeListener> listeners = new ArrayList<>();

    public RGBCurvePanel() {
        redPoints.add(new Point(0, 255));
        redPoints.add(new Point(255, 0));
        greenPoints.add(new Point(0, 255));
        greenPoints.add(new Point(255, 0));
        bluePoints.add(new Point(0, 255));
        bluePoints.add(new Point(255, 0));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleMouse(e.getPoint());
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                handleMouse(e.getPoint());
            }
        });
    }

    public void addLUTChangeListener(LUTChangeListener l) {
        listeners.add(l);
    }

    private void notifyLUTChanged() {
        int[] lutR = buildLUT(redPoints);
        int[] lutG = buildLUT(greenPoints);
        int[] lutB = buildLUT(bluePoints);
        for (LUTChangeListener l : listeners) {
            l.lutChanged(lutR, lutG, lutB);
        }
    }

    private void handleMouse(Point p) {
        int channel = p.x / (getWidth() / 3);
        int x = Math.max(0, Math.min(255, p.x % (getWidth() / 3)));
        int y = Math.max(0, Math.min(255, getHeight() - 1 - p.y));

        switch (channel) {
            case 0:
                redPoints.set(redPoints.size() - 1, new Point(x, y));
                break;
            case 1:
                greenPoints.set(greenPoints.size() - 1, new Point(x, y));
                break;
            case 2:
                bluePoints.set(bluePoints.size() - 1, new Point(x, y));
                break;
        }

        repaint();
        notifyLUTChanged();
    }

    public void resetIdentity() {
        redPoints.clear();
        greenPoints.clear();
        bluePoints.clear();
        redPoints.add(new Point(0, 255));
        greenPoints.add(new Point(0, 255));
        bluePoints.add(new Point(0, 255));
        redPoints.add(new Point(255, 0));
        greenPoints.add(new Point(255, 0));
        bluePoints.add(new Point(255, 0));
        repaint();
        notifyLUTChanged();
    }

    private int[] buildLUT(List<Point> points) {
        int[] lut = new int[256];
        Point p0 = points.get(0), p1 = points.get(1);
        for (int i = 0; i < 256; i++) {
            double t = (double) i / 255.0;
            double val = p0.y + t * (p1.y - p0.y);
            lut[i] = (int) Math.max(0, Math.min(255, Math.round(val)));
        }
        return lut;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth(), getHeight());

        drawCurve(g, redPoints, Color.RED, 0);
        drawCurve(g, greenPoints, Color.GREEN, 1);
        drawCurve(g, bluePoints, Color.BLUE, 2);
    }

    private void drawCurve(Graphics g, List<Point> points, Color color, int channel) {
        g.setColor(color);
        for (int i = 0; i < points.size() - 1; i++) {
            Point p0 = points.get(i);
            Point p1 = points.get(i + 1);
            int x0 = p0.x + channel * (getWidth() / 3);
            int y0 = getHeight() - 1 - p0.y;
            int x1 = p1.x + channel * (getWidth() / 3);
            int y1 = getHeight() - 1 - p1.y;
            g.drawLine(x0, y0, x1, y1);
            g.fillOval(x0 - 4, y0 - 4, 8, 8);
            g.fillOval(x1 - 4, y1 - 4, 8, 8);
        }
    }
}

interface LUTChangeListener {
    void lutChanged(int[] lutR, int[] lutG, int[] lutB);
}
