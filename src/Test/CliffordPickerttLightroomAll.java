package Test;





/**
 * -----------------------------------------------------------------------------
 *  CliffordPickerttLightroom - Foto-Transfer- und Duplikatfilter-Tool
 * -----------------------------------------------------------------------------
 *
 *  🧭 ZWECK:
 *  Dieses Programm dient dazu, große Fotoarchive (z. B. aus mehreren Festplatten)
 *  automatisiert in ein neues Zielverzeichnis zu übertragen, doppelte Inhalte
 *  zu vermeiden und Lightroom-kompatibel in Blöcken aufzuteilen.
 *
 *  ⚡ HAUPTFUNKTIONEN:
 *  - ✅ Rekursive Verarbeitung aller Unterordner
 *  - 📁 Automatische Aufteilung in Zielordner nach je 3500 Bildern
 *  - 📝 Auswahl relevanter Dateitypen (jpg, jpeg, png, psd, dng, arw, mp3, mov)
 *  - 📏 Filterung nach Mindestgröße (standardmäßig >800 kB)
 *  - 🧠 Erkennung und Vermeidung inhaltlicher Duplikate per SHA-256-Hash
 *  - 🪄 Automatische Umbenennung bei Namensduplikaten
 *  - 🪵 Log-Anzeige mit Zeitstempeln und Zwischenstatus alle 10 Sekunden
 *  - 📊 Debug-Zusammenfassung: Anzahl gefundener Dateien, Filterergebnisse, Beispielpfade
 *
 *  🧰 TECHNISCHE DETAILS:
 *  - GUI mit Swing
 *  - Java >= 11 empfohlen (getestet mit OpenJDK 25)
 *  - SHA-256 Hashing für Duplikaterkennung, Zwischenspeicherung in HashSet (RAM)
 *  - Threads: Kopiervorgang läuft asynchron, GUI bleibt reaktiv
 *
 *  🚀 TYPISCHER EINSATZ:
 *  1. Starten (Doppelklick auf JAR oder IntelliJ)
 *  2. Quellverzeichnis mit verschachtelten Unterordnern auswählen
 *  3. Zielverzeichnis wählen
 *  4. Dateitypen nach Bedarf auswählen
 *  5. "Dateien übertragen" klicken
 *  6. Kopiervorgang abwarten → Statusmeldungen + abschließende Statistik
 *
 *  📦 BUILD HINWEIS (IntelliJ):
 *  - Build → Build Artifacts… → Jar → From modules with dependencies
 *  - Main-Class: CliffordPickerttLightroom
 *  - Artefakt erstellen → JAR-Datei auf Desktop oder GitHub speichern
 *
 *  📝 VERSION:
 *  - Version: 2.0
 *  - Datum: 2025-10-04
 *  - Autor: ChatGPT (OpenAI) + Ingo Weyck
 *
 * -----------------------------------------------------------------------------
 */

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CliffordPickerttLightroomAll extends JFrame {

    // GUI Komponenten
    private final JTextField sourceField = new JTextField();
    private final JTextField targetField = new JTextField();
    private final JTextArea logArea = new JTextArea();
    private final JButton startButton = new JButton("Dateien übertragen");
    private final Map<String, JCheckBox> extensionCheckboxes = new LinkedHashMap<>();

    // Filtereinstellungen
    private static final long MIN_FILE_SIZE = 800L * 1024L; // 800 kB
    private static final int MAX_FILES_PER_FOLDER = 3500;

    // Status
    private Timer statusTimer;
    private final AtomicInteger totalCopied = new AtomicInteger(0);
    private final AtomicInteger copiedInCurrentFolder = new AtomicInteger(0);

    // Hashspeicher für Inhaltsduplikate
    private final Set<String> seenHashes = Collections.synchronizedSet(new HashSet<>());

    // -------------------------------------------------------------------------

    public CliffordPickerttLightroomAll() {
        setTitle("CliffordPickertt Lightroom Transfer All");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // --- Top Panel (Quell- und Zielauswahl)
        JPanel top = new JPanel(new GridLayout(2, 3, 6, 6));
        JButton srcBtn = new JButton("Quelle wählen");
        JButton tgtBtn = new JButton("Ziel wählen");
        srcBtn.addActionListener(e -> chooseDirectory(sourceField));
        tgtBtn.addActionListener(e -> chooseDirectory(targetField));
        top.add(new JLabel("Quellordner:"));
        top.add(sourceField);
        top.add(srcBtn);
        top.add(new JLabel("Zielordner:"));
        top.add(targetField);
        top.add(tgtBtn);
        add(top, BorderLayout.NORTH);

        // --- Center Panel (Extension-Checkboxen + Log)
        JPanel center = new JPanel(new GridLayout(1, 2, 8, 8));
        JPanel extPanel = new JPanel(new GridLayout(0, 3));
        extPanel.setBorder(new TitledBorder("Dateitypen auswählen"));
        String[] exts = {"jpg", "jpeg", "png", "psd", "dng", "arw", "mp3", "mov"};
        for (String e : exts) {
            JCheckBox cb = new JCheckBox(e.toUpperCase());
            if (!e.equals("mp3") && !e.equals("mov")) cb.setSelected(true);
            extensionCheckboxes.put(e, cb);
            extPanel.add(cb);
        }
        center.add(new JScrollPane(extPanel));
        logArea.setEditable(false);
        center.add(new JScrollPane(logArea));
        add(center, BorderLayout.CENTER);

        // --- Bottom Panel (Startbutton)
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            logArea.setText("");
            totalCopied.set(0);
            copiedInCurrentFolder.set(0);
            seenHashes.clear();
            new Thread(this::startTransfer).start();
        });
        bottom.add(startButton);
        add(bottom, BorderLayout.SOUTH);
    }

    // -------------------------------------------------------------------------
    //  Transfer-Logik
    // -------------------------------------------------------------------------

    private void startTransfer() {
        File sourceDir = new File(sourceField.getText().trim());
        File initialTargetDir = new File(targetField.getText().trim());

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            log("❌ Ungültiges Quellverzeichnis: " + sourceDir.getAbsolutePath());
            SwingUtilities.invokeLater(() -> startButton.setEnabled(true));
            return;
        }
        if (initialTargetDir.getAbsolutePath().isEmpty()) {
            log("❌ Bitte Zielverzeichnis angeben.");
            SwingUtilities.invokeLater(() -> startButton.setEnabled(true));
            return;
        }

        try {
            if (!initialTargetDir.exists()) Files.createDirectories(initialTargetDir.toPath());
        } catch (IOException e) {
            log("❌ Zielverzeichnis konnte nicht angelegt werden: " + e.getMessage());
            SwingUtilities.invokeLater(() -> startButton.setEnabled(true));
            return;
        }

        Set<String> selectedExts = getSelectedExtensions();
        log("🔎 Starte Suche in: " + sourceDir.getAbsolutePath());
        log("🔎 Gewählte Endungen: " + String.join(", ", selectedExts));

        // --- Dateien sammeln + Debugausgabe
        List<File> allFiles = collectAllFiles(sourceDir);
        int totalFound = allFiles.size();
        List<File> filteredByExt = allFiles.stream()
                .filter(f -> selectedExts.contains(getFileExtensionLower(f.getName())))
                .collect(Collectors.toList());
        List<File> filteredBySize = filteredByExt.stream()
                .filter(f -> f.length() > MIN_FILE_SIZE)
                .collect(Collectors.toList());

        log("📊 Debug-Zusammenfassung:");
        log("   Gefundene Dateien insgesamt: " + totalFound);
        log("   Nach Extension gefiltert: " + filteredByExt.size());
        log("   Nach Mindestgröße gefiltert: " + filteredBySize.size());
        log("   Beispielpfade:");
        filteredBySize.stream().limit(10).forEach(f -> log("      " + f.getAbsolutePath()));

        if (filteredBySize.isEmpty()) {
            log("ℹ️ Keine passenden Dateien gefunden. Bitte Filter prüfen.");
            SwingUtilities.invokeLater(() -> startButton.setEnabled(true));
            return;
        }

        // --- Statusmeldungen alle 10 s
        statusTimer = new Timer(true);
        statusTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                log("⏳ Dateien werden noch übertragen … " + totalCopied.get() + " bereits kopiert");
            }
        }, 10000, 10000);

        // --- Kopieren mit Duplikat- & Namensprüfung
        String baseName = initialTargetDir.getName();
        File parentDir = Optional.ofNullable(initialTargetDir.getParentFile()).orElse(new File("."));
        int folderIndex = 1;
        File currentTarget = initialTargetDir;

        for (File file : filteredBySize) {
            try {
                String hash = computeSHA256(file);
                if (seenHashes.contains(hash)) {
                    log("⚠️ Duplikat erkannt (identischer Inhalt): " + file.getAbsolutePath());
                    continue;
                }

                if (copiedInCurrentFolder.get() >= MAX_FILES_PER_FOLDER) {
                    folderIndex++;
                    currentTarget = new File(parentDir, baseName + "_" + folderIndex);
                    if (!currentTarget.exists()) currentTarget.mkdirs();
                    copiedInCurrentFolder.set(0);
                    log("📂 Neuer Zielordner: " + currentTarget.getAbsolutePath());
                }

                Path targetFile = resolveCollision(currentTarget.toPath().resolve(file.getName()));
                Files.copy(file.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);
                seenHashes.add(hash);
                totalCopied.incrementAndGet();
                copiedInCurrentFolder.incrementAndGet();

            } catch (IOException e) {
                log("❌ Fehler beim Kopieren: " + file.getAbsolutePath() + " - " + e.getMessage());
            }
        }

        if (statusTimer != null) statusTimer.cancel();
        log("✅ Transfer abgeschlossen. Insgesamt kopiert: " + totalCopied.get());
        SwingUtilities.invokeLater(() -> startButton.setEnabled(true));
    }

    // -------------------------------------------------------------------------
    //  Hilfsmethoden
    // -------------------------------------------------------------------------

    private void chooseDirectory(JTextField field) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private List<File> collectAllFiles(File dir) {
        try (Stream<Path> s = Files.walk(dir.toPath())) {
            return s.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .sorted(Comparator.comparing(File::getAbsolutePath))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log("❌ Fehler beim Durchsuchen: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private Set<String> getSelectedExtensions() {
        Set<String> out = new HashSet<>();
        extensionCheckboxes.forEach((ext, cb) -> {
            if (cb.isSelected()) out.add(ext.toLowerCase());
        });
        return out;
    }

    private String getFileExtensionLower(String name) {
        int dot = name.lastIndexOf('.');
        if (dot == -1 || dot == name.length() - 1) return "";
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private Path resolveCollision(Path desired) {
        Path p = desired;
        if (!Files.exists(p)) return p;
        String fileName = p.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String base = (dot == -1) ? fileName : fileName.substring(0, dot);
        String ext = (dot == -1) ? "" : fileName.substring(dot);
        int idx = 1;
        while (Files.exists(p.getParent().resolve(base + "_" + idx + ext))) idx++;
        return p.getParent().resolve(base + "_" + idx + ext);
    }

    private String computeSHA256(File file) throws IOException {
        try (InputStream in = Files.newInputStream(file.toPath())) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) > 0) digest.update(buffer, 0, n);
            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 nicht verfügbar", e);
        }
    }

    private void log(String msg) {
        String t = new SimpleDateFormat("HH:mm:ss").format(new Date());
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + t + "] " + msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CliffordPickerttLightroomAll().setVisible(true));
    }
}
