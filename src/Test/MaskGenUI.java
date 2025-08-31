package Test;

/*
 * MaskGenUI — Swing-GUI zum Erzeugen von Luminanzmasken (PNG) für Photoshop
 *
 * Funktionen:
 *  - Bild laden (beliebige sRGB-Formate, ImageIO)
 *  - Vorschau: Original links, Maske rechts (live)
 *  - Modi: luminance | constant | threshold | range | lights | darks | invert
 *  - Parameter steuerbar per UI (Slider/Spinner)
 *  - Speicherung als 8‑Bit Graustufen-PNG (TYPE_BYTE_GRAY), kompatibel zu Photoshop
 *  - Rechenlast in SwingWorker (keine UI-Blockade)
 *
 * Build:   javac MaskGenUI.java
 * Run:     java MaskGenUI
 *
 * Hinweis: Die Berechnung verwendet sRGB→linear, Rec.709-Luminanz und zurück → sRGB
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class MaskGenUI extends JFrame {
    // UI
    private final JLabel lblOriginal = new JLabel("(Original)", SwingConstants.CENTER);
    private final JLabel lblMask = new JLabel("(Maske)", SwingConstants.CENTER);

    private final JButton btnOpen = new JButton("Bild öffnen …");
    private final JButton btnSave = new JButton("Maske speichern …");

    private final JComboBox<String> cbMode = new JComboBox<>(
            new String[]{"luminance", "constant", "threshold", "range", "lights", "darks"}
    );
    private final JCheckBox chkInvert = new JCheckBox("invert");

    private final JSpinner spConst = new JSpinner(new SpinnerNumberModel(128, 0, 255, 1));
    private final JSlider slThr = new JSlider(0, 100, 50);
    private final JSlider slLow = new JSlider(0, 100, 30);
    private final JSlider slHigh = new JSlider(0, 100, 70);
    private final JSlider slGamma = new JSlider(1, 600, 150); // 0.01–6.00 via scale 100

    private final JLabel lbThrVal = new JLabel("0.50");
    private final JLabel lbLowVal = new JLabel("0.30");
    private final JLabel lbHighVal = new JLabel("0.70");
    private final JLabel lbGammaVal = new JLabel("1.50");

    // Daten
    private BufferedImage srcImage;     // geladenes Bild
    private BufferedImage maskImage;    // letzte berechnete Maske

    private SwingWorker<BufferedImage, Void> worker; // für Hintergrundberechnung

    public MaskGenUI() {
        super("Luminanzmasken Generator — Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        setLocationByPlatform(true);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                wrapInPanel(lblOriginal, "Original"),
                wrapInPanel(lblMask, "Maske (Vorschau)"));
        split.setResizeWeight(0.5);

        JPanel controls = buildControlsPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(split, BorderLayout.CENTER);
        getContentPane().add(controls, BorderLayout.SOUTH);

        wireEvents();
    }

    private JPanel wrapInPanel(JComponent comp, String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new TitledBorder(title));
        p.add(new JScrollPane(comp), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildControlsPanel() {
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.gridy = 0; c.gridx = 0; c.anchor = GridBagConstraints.WEST;

        p.add(btnOpen, c);
        c.gridx++;
        p.add(btnSave, c);
        c.gridx++;
        p.add(new JLabel("Modus:"), c);
        c.gridx++;
        p.add(cbMode, c);
        c.gridx++;
        p.add(chkInvert, c);

        // Zeile 2: constant
        c.gridy++; c.gridx = 0;
        p.add(new JLabel("constant (0–255):"), c);
        c.gridx++;
        p.add(spConst, c);

        // Zeile 3: threshold
        c.gridy++; c.gridx = 0;
        p.add(new JLabel("threshold (0–1):"), c);
        c.gridx++;
        slThr.setPreferredSize(new Dimension(220, 24));
        p.add(slThr, c);
        c.gridx++;
        p.add(lbThrVal, c);

        // Zeile 4: range
        c.gridy++; c.gridx = 0;
        p.add(new JLabel("range low/high (0–1):"), c);
        c.gridx++;
        slLow.setPreferredSize(new Dimension(180, 24));
        p.add(slLow, c);
        c.gridx++;
        slHigh.setPreferredSize(new Dimension(180, 24));
        p.add(slHigh, c);
        c.gridx++;
        JPanel midVals = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        midVals.add(new JLabel("low:")); midVals.add(lbLowVal);
        midVals.add(new JLabel("high:")); midVals.add(lbHighVal);
        p.add(midVals, c);

        // Zeile 5: gamma
        c.gridy++; c.gridx = 0;
        p.add(new JLabel("gamma (1.00–6.00):"), c);
        c.gridx++;
        slGamma.setPreferredSize(new Dimension(220, 24));
        p.add(slGamma, c);
        c.gridx++;
        p.add(lbGammaVal, c);

        return p;
    }

    private void wireEvents() {
        btnOpen.addActionListener(e -> onOpen());
        btnSave.addActionListener(e -> onSave());

        ItemListener reCalc = e -> scheduleRecalc();
        ChangeListenerAdapter reCalcChange = new ChangeListenerAdapter(() -> scheduleRecalc());

        cbMode.addItemListener(reCalc);
        chkInvert.addItemListener(reCalc);
        slThr.addChangeListener(ev -> { lbThrVal.setText(fmt01(slThr.getValue()/100.0)); scheduleRecalc(); });
        slLow.addChangeListener(ev -> { lbLowVal.setText(fmt01(slLow.getValue()/100.0)); scheduleRecalc(); });
        slHigh.addChangeListener(ev -> { lbHighVal.setText(fmt01(slHigh.getValue()/100.0)); scheduleRecalc(); });
        slGamma.addChangeListener(ev -> { lbGammaVal.setText(fmt02(slGamma.getValue()/100.0)); scheduleRecalc(); });
        spConst.addChangeListener(reCalcChange);

        // Doppelklick auf Maske → 1:1 anzeigen
        lblMask.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && maskImage != null) showImageInDialog(maskImage, "Maske 1:1");
            }
        });
        lblOriginal.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && srcImage != null) showImageInDialog(srcImage, "Original 1:1");
            }
        });
    }

    private void onOpen() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Bilddateien", ImageIO.getReaderFileSuffixes()));
        int rv = fc.showOpenDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                srcImage = ImageIO.read(f);
                if (srcImage == null) throw new IOException("Unbekanntes Format");
                lblOriginal.setIcon(new ImageIcon(scaleToFit(srcImage, lblOriginal.getSize())));
                lblOriginal.setText(null);
                scheduleRecalc();
            } catch (IOException ex) {
                showError("Konnte Bild nicht lesen: " + ex.getMessage());
            }
        }
    }

    private void onSave() {
        if (maskImage == null) { showError("Keine Maske berechnet."); return; }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("maske.png"));
        fc.setFileFilter(new FileNameExtensionFilter("PNG", "png"));
        int rv = fc.showSaveDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".png")) {
                f = new File(f.getParentFile(), f.getName() + ".png");
            }
            try {
                ImageIO.write(maskImage, "png", f);
                JOptionPane.showMessageDialog(this, "Gespeichert: " + f.getAbsolutePath(), "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Konnte PNG nicht schreiben: " + ex.getMessage());
            }
        }
    }

    private void scheduleRecalc() {
        if (srcImage == null) return;
        // cancel laufende
        if (worker != null && !worker.isDone()) worker.cancel(true);

        final String mode = (String) cbMode.getSelectedItem();
        final boolean invert = chkInvert.isSelected();
        final int constGray = (int) spConst.getValue();
        final double thr = slThr.getValue() / 100.0;
        final double low = slLow.getValue() / 100.0;
        final double high = slHigh.getValue() / 100.0;
        final double gamma = slGamma.getValue() / 100.0; // 1.00–6.00

        worker = new SwingWorker<>() {
            @Override protected BufferedImage doInBackground() {
                return buildMask(srcImage, mode, invert, constGray, thr, low, high, gamma);
            }
            @Override protected void done() {
                if (isCancelled()) return;
                try {
                    maskImage = get();
                    lblMask.setIcon(new ImageIcon(scaleToFit(maskImage, lblMask.getSize())));
                    lblMask.setText(null);
                } catch (Exception ex) {
                    showError("Fehler bei Berechnung: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    // --- Bildverarbeitung ----------------------------------------------------
    private static BufferedImage buildMask(BufferedImage src, String mode, boolean invert,
                                           int constGray, double thr, double low, double high, double gamma) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = out.getRaster();
        byte[] row = new byte[w];

        if (low > high) { double t = low; low = high; high = t; }

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = src.getRGB(x, y);
                int r8 = (argb >> 16) & 0xFF;
                int g8 = (argb >> 8) & 0xFF;
                int b8 = (argb) & 0xFF;

                double r = srgbToLinear(r8 / 255.0);
                double g = srgbToLinear(g8 / 255.0);
                double b = srgbToLinear(b8 / 255.0);
                double Y = 0.2126 * r + 0.7152 * g + 0.0722 * b; // linear‑light 0..1

                double m;
                switch (mode) {
                    case "luminance":
                        m = linearToSrgb(Y);
                        break;
                    case "constant":
                        m = clamp01(constGray / 255.0);
                        break;
                    case "threshold":
                        m = (Y >= thr) ? 1.0 : 0.0;
                        break;
                    case "range":
                        if (Y <= low || Y >= high) m = 0.0;
                        else {
                            double mid = (low + high) * 0.5;
                            if (Y <= mid) m = (Y - low) / (mid - low);
                            else m = (high - Y) / (high - mid);
                            m = clamp01(m);
                        }
                        break;
                    case "lights": {
                        double Ys = linearToSrgb(Y);
                        m = Math.pow(Ys, Math.max(1e-6, gamma));
                        break;
                    }
                    case "darks": {
                        double Ys = linearToSrgb(Y);
                        m = 1.0 - Math.pow(1.0 - Ys, Math.max(1e-6, gamma));
                        break;
                    }
                    default:
                        m = linearToSrgb(Y);
                }
                if (invert) m = 1.0 - m;
                row[x] = (byte) (int) Math.round(m * 255.0);
            }
            wr.setDataElements(0, y, w, 1, row);
        }
        return out;
    }

    private static double clamp01(double v) { return Math.max(0.0, Math.min(1.0, v)); }

    // sRGB EOTF/OETF
    public static double srgbToLinear(double c) { return (c <= 0.04045) ? c / 12.92 : Math.pow((c + 0.055)/1.055, 2.4); }
    public static double linearToSrgb(double c) { return (c <= 0.0031308) ? 12.92*c : 1.055*Math.pow(c, 1/2.4) - 0.055; }

    // --- Darstellungs-Helfer -------------------------------------------------
    private static Image scaleToFit(BufferedImage img, Dimension target) {
        if (target == null || target.width <= 0 || target.height <= 0) return img;
        double sx = target.getWidth() / img.getWidth();
        double sy = target.getHeight() / img.getHeight();
        double s = Math.min(sx, sy);
        int w = Math.max(1, (int) Math.round(img.getWidth() * s));
        int h = Math.max(1, (int) Math.round(img.getHeight() * s));
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, w, h, null);
        g2.dispose();
        return scaled;
    }

    private static void showImageInDialog(BufferedImage img, String title) {
        JDialog d = new JDialog((Frame) null, title, true);
        JLabel l = new JLabel(new ImageIcon(img));
        d.getContentPane().add(new JScrollPane(l));
        d.setSize(new Dimension(Math.min(1200, img.getWidth()+50), Math.min(900, img.getHeight()+80)));
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }

    private static void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    private static String fmt01(double v) { return new DecimalFormat("0.00").format(v); }
    private static String fmt02(double v) { return new DecimalFormat("0.00").format(v); }

    // --- Start ---------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MaskGenUI().setVisible(true));
    }

    // kleiner Helfer für JSpinner-ChangeListener → Runnable
    private static class ChangeListenerAdapter implements javax.swing.event.ChangeListener {
        private final Runnable r; ChangeListenerAdapter(Runnable r){this.r=r;} @Override public void stateChanged(javax.swing.event.ChangeEvent e){r.run();}
    }
}

