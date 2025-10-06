package Test;

/**
 * -----------------------------------------------------------------------------
 *  CliffordPickerttLightroom - Foto-Transfer- und Duplikatfilter-Tool
 * -----------------------------------------------------------------------------
 *
 *  🧭 ZWECK:
 *  Dieses Programm dient dazu, große Fotoarchive automatisiert in ein neues
 *  Zielverzeichnis zu übertragen, doppelte Inhalte zu vermeiden und Lightroom-
 *  kompatibel in Blöcken aufzuteilen — inklusive Erhalt der Original-Zeitstempel.
 *
 *  ⚡ HAUPTFUNKTIONEN:
 *  - ✅ Rekursive Verarbeitung aller Unterordner (robust mit walkFileTree)
 *  - ⚠️ Automatisches Überspringen geschützter macOS-Ordner (z. B. ~/Library)
 *  - 📝 Filterung nach Dateitypen & Mindestgröße
 *  - 🧠 Inhaltsduplikat-Erkennung per SHA-256
 *  - 🪄 Namensduplikat-Handling
 *  - 🕓 Original-Erstell- und Änderungsdatum erhalten ✨
 *  - 🪵 Log mit Statusmeldungen & Debug-Zusammenfassung
 *
 *  📦 Getestet mit macOS Sequoia & OpenJDK 25
 * -----------------------------------------------------------------------------
 */

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

public class CliffordPickerttLightroomAll2 extends JFrame {

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

    public CliffordPickerttLightroomAll2() {
        setTitle("CliffordPickertt Lightroom Transfer");
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

        String[] exts = {
                "jpg", "jpeg", "png", "heic",
                "psd", "dng", "arw",
                "mp4", "mov", "mp3"
        };

        for (String e : exts) {
            JCheckBox cb = new JCheckBox(e.toUpperCase());
            if (!e.equals("mp3") && !e.equals("mov") && !e.equals("mp4")) cb.setSelected(true);
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
                .toList();
        List<File> filteredBySize = filteredByExt.stream()
                .filter(f -> f.length() > MIN_FILE_SIZE)
                .toList();

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

        // --- Kopieren mit Duplikat- & Namensprüfung + Datumserhalt
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

                Path sourcePath = file.toPath();
                Path targetPath = resolveCollision(currentTarget.toPath().resolve(file.getName()));

                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                // 🕓 Ursprüngliche Datei-Zeitstempel übernehmen
                try {
                    BasicFileAttributes attrs = Files.readAttributes(sourcePath, BasicFileAttributes.class);
                    Files.setAttribute(targetPath, "basic:creationTime", attrs.creationTime());
                    Files.setLastModifiedTime(targetPath, attrs.lastModifiedTime());
                } catch (IOException e) {
                    log("⚠️ Konnte Zeitstempel nicht setzen für: " + targetPath);
                }

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
    //  Datei-Sammlung (robust mit walkFileTree)
    // -------------------------------------------------------------------------

    private List<File> collectAllFiles(File dir) {
        List<File> collected = new ArrayList<>();
        try {
            Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) {
                    String pathStr = directory.toString();
                    if (pathStr.contains("/Library") || pathStr.contains("/.Trash") || pathStr.contains("/Time Machine")) {
                        log("⚠️ Überspringe geschützten Ordner: " + pathStr);
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (attrs.isRegularFile()) collected.add(file.toFile());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    log("⚠️ Zugriff verweigert, überspringe: " + file);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            log("❌ Fehler beim Durchsuchen: " + e.getMessage());
        }
        return collected;
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
        if (!Files.exists(desired)) return desired;
        String fileName = desired.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String base = (dot == -1) ? fileName : fileName.substring(0, dot);
        String ext = (dot == -1) ? "" : fileName.substring(dot);
        int idx = 1;
        while (Files.exists(desired.getParent().resolve(base + "_" + idx + ext))) idx++;
        return desired.getParent().resolve(base + "_" + idx + ext);
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
        SwingUtilities.invokeLater(() -> new CliffordPickerttLightroomAll2().setVisible(true));
    }
}
