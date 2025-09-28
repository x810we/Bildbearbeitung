package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class CurveEditorGUI extends JFrame {
    private final BufferedImage srcImage, dstImage;
    private final JLabel imgLabel;

    public CurveEditorGUI(BufferedImage img) {
        this.srcImage = img;
        this.dstImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        imgLabel = new JLabel(new ImageIcon(dstImage));

            imgLabel.setIcon(new ImageIcon(dstImage));
        });

        setLayout(new BorderLayout());
        add(new JScrollPane(imgLabel), BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

        for (int y = 0; y < srcImage.getHeight(); y++) {
            for (int x = 0; x < srcImage.getWidth(); x++) {
                int c = srcImage.getRGB(x, y);
                int a = (c >> 24) & 0xFF;
                dstImage.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> new CurveEditorGUI(img));
    }
}

    private final List<LUTChangeListener> listeners = new ArrayList<>();


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
        for (LUTChangeListener l : listeners) {
        }
    }

    private void handleMouse(Point p) {
        int y = Math.max(0, Math.min(255, getHeight() - 1 - p.y));
        repaint();
        notifyLUTChanged();
    }

    public void resetIdentity() {
        repaint();
        notifyLUTChanged();
    }

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
            g.drawLine(x0, y0, x1, y1);
            g.fillOval(x0 - 4, y0 - 4, 8, 8);
            g.fillOval(x1 - 4, y1 - 4, 8, 8);
        }
    }
