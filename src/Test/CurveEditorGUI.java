package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class CurveEditorGUI extends JFrame {
    private final BufferedImage srcImage, dstImage;
    private final CurvePanel curvePanel;
    private final JLabel imgLabel;

    public CurveEditorGUI(BufferedImage img) {
        super("Interactive Curve Adjustment");
        this.srcImage = img;
        this.dstImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        imgLabel = new JLabel(new ImageIcon(dstImage));
        curvePanel = new CurvePanel();
        curvePanel.setPreferredSize(new Dimension(256, 256));

        // Bind custom listener: empfängt LUT und wendet sie an
        curvePanel.addLUTChangeListener(lut -> {
            applyLUT(lut);
            imgLabel.setIcon(new ImageIcon(dstImage));
        });

        setLayout(new BorderLayout());
        add(new JScrollPane(imgLabel), BorderLayout.CENTER);
        add(curvePanel, BorderLayout.EAST);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        curvePanel.resetIdentity();  // setzt die Kurve auf Identität (Linear)
    }

    private void applyLUT(int[] lut) {
        for (int y = 0; y < srcImage.getHeight(); y++) {
            for (int x = 0; x < srcImage.getWidth(); x++) {
                int c = srcImage.getRGB(x, y);
                int a = (c >> 24) & 0xFF;
                int r = lut[(c >> 16) & 0xFF];
                int g = lut[(c >>  8) & 0xFF];
                int b = lut[c & 0xFF];
                dstImage.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("/Users/x810we/Pictures/2-Advent.png"));
        SwingUtilities.invokeLater(() -> new CurveEditorGUI(img));
    }
}

interface LUTChangeListener {
    void lutChanged(int[] newLUT);
}

class CurvePanel extends JPanel {
    private final List<Point> points = new ArrayList<>();
    private final List<LUTChangeListener> listeners = new ArrayList<>();

    public CurvePanel() {
        points.add(new Point(0, 255));
        points.add(new Point(255, 0));

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
        int[] lut = buildLUT();
        for (LUTChangeListener l : listeners) {
            l.lutChanged(lut);
        }
    }

    private void handleMouse(Point p) {
        int x = Math.max(0, Math.min(255, p.x));
        int y = Math.max(0, Math.min(255, getHeight() - 1 - p.y));
        points.set(points.size() - 1, new Point(x, y));  // aktualisiert Endpunkt
        repaint();
        notifyLUTChanged();
    }

    public void resetIdentity() {
        points.clear();
        points.add(new Point(0, 255));
        points.add(new Point(255, 0));
        repaint();
        notifyLUTChanged();
    }

    int[] buildLUT() {
        int[] lut = new int[256];
        Point p0 = points.get(0), p1 = points.get(1);
        for (int i = 0; i < 256; i++) {
            double t = (double)i / 255.0;
            double val = p0.y + t * (p1.y - p0.y);
            lut[i] = (int)Math.max(0, Math.min(255, Math.round(val)));
        }
        return lut;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 255, 255);
        Point p0 = points.get(0), p1 = points.get(1);
        int x0 = p0.x, y0 = getHeight() - 1 - p0.y;
        int x1 = p1.x, y1 = getHeight() - 1 - p1.y;
        g.drawLine(x0, y0, x1, y1);
        g.fillOval(x0 - 4, y0 - 4, 8, 8);
        g.fillOval(x1 - 4, y1 - 4, 8, 8);
    }
}
