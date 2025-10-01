package Test;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class CurveEditorGUI extends JFrame {
    private final BufferedImage srcImage;
    private final BufferedImage dstImage;
    private final JLabel imgLabel;
    private boolean showOriginal = false;

    public CurveEditorGUI(BufferedImage img) {
        super("Curve Editor mit Original-Toggle");
        this.srcImage = img;
        this.dstImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        imgLabel = new JLabel(new ImageIcon(dstImage));

        // Toggle-Button, um Original anzeigen / bearbeitet umschalten
        JToggleButton toggleOriginal = new JToggleButton("Original anzeigen");
        toggleOriginal.addActionListener(e -> {
            showOriginal = toggleOriginal.isSelected();
            updateImageDisplay();
        });

        // Panel für Curve und Steuerung
        CurvePanel curvePanel = new CurvePanel();
        curvePanel.setPreferredSize(new Dimension(256, 256));
        curvePanel.addLUTChangeListener(lut -> {
            applyLUT(lut);
            // An dieser Stelle: updateImageDisplay wird von applyLUT aufgerufen
        });

        setLayout(new BorderLayout());
        add(new JScrollPane(imgLabel), BorderLayout.CENTER);
        add(curvePanel, BorderLayout.EAST);
        add(toggleOriginal, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Starte mit Identitätskurve (kein Effekt)
        curvePanel.resetIdentity();
    }

    private void updateImageDisplay() {
        if (showOriginal) {
            imgLabel.setIcon(new ImageIcon(srcImage));
        } else {
            imgLabel.setIcon(new ImageIcon(dstImage));
        }
    }

    private void applyLUT(int[] lut) {
        int w = srcImage.getWidth();
        int h = srcImage.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = srcImage.getRGB(x, y);
                int a = (c >> 24) & 0xFF;
                int r = (c >> 16) & 0xFF;
                int g = (c >> 8) & 0xFF;
                int b = (c) & 0xFF;
                r = lut[r];
                g = lut[g];
                b = lut[b];
                int newRgb = (a << 24) | (r << 16) | (g << 8) | b;
                dstImage.setRGB(x, y, newRgb);
            }
        }
        updateImageDisplay();
    }

    public static void main(String[] args) throws Exception {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            System.exit(0);
        }
        File f = fc.getSelectedFile();
        BufferedImage img = ImageIO.read(f);
        SwingUtilities.invokeLater(() -> new CurveEditorGUI(img));
    }
}

// ● Listener-Interface, das beim LUT-Wechsel benachrichtigt
interface LUTChangeListener {
    void lutChanged(int[] lut);
}

class CurvePanel extends JPanel {
    private java.util.List<Point> points = new java.util.ArrayList<>();
    private java.util.List<LUTChangeListener> listeners = new java.util.ArrayList<>();

    public CurvePanel() {
        points.add(new Point(0, 255));
        points.add(new Point(255, 0));

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                handleMouse(e.getPoint());
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
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
        int x = p.x;
        int y = getHeight() - 1 - p.y;
        x = Math.max(0, Math.min(255, x));
        y = Math.max(0, Math.min(255, y));
        // Wir aktualisieren den zweiten Punkt (Punkt 1)
        points.set(1, new Point(x, y));
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
        Point p0 = points.get(0);
        Point p1 = points.get(1);
        for (int i = 0; i < 256; i++) {
            double t = i / 255.0;
            // Lineare Interpolation zwischen p0.y und p1.y
            double val = p0.y + t * (p1.y - p0.y);
            lut[i] = (int) Math.max(0, Math.min(255, Math.round(val)));
        }
        return lut;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 255, 255);

        Point p0 = points.get(0);
        Point p1 = points.get(1);
        int x0 = p0.x;
        int y0 = getHeight() - 1 - p0.y;
        int x1 = p1.x;
        int y1 = getHeight() - 1 - p1.y;
        g.setColor(Color.BLUE);
        g.drawLine(x0, y0, x1, y1);
        g.fillOval(x0 - 4, y0 - 4, 8, 8);
        g.fillOval(x1 - 4, y1 - 4, 8, 8);
    }
}
