package Test;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.swing.*;

/**
 * Lightroom_CliffordPickett_Collector.java (mit GUI + Chunking)
 *
 * Durchsucht rekursiv ein angegebenes Volume (Quellpfad) und kopiert alle
 * Bilddateien in Zielverzeichnisse mit jeweils max. 3000 Dateien.
 *
 * Unterstützte Dateiendungen (case-insensitive):
 * png, dng, psd, jpg, jpeg, heic, arw
 */
public class Lightroom_CliffordPickett_Collector {

    private static final Set<String> EXTENSIONS = new HashSet<>();
    private static final int FILES_PER_DIR = 3000;

    static {
        EXTENSIONS.add("png");
        EXTENSIONS.add("dng");
        EXTENSIONS.add("psd");
        EXTENSIONS.add("jpg");
        EXTENSIONS.add("jpeg");
        EXTENSIONS.add("heic");
        EXTENSIONS.add("arw");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            // Quellordner wählen
            JOptionPane.showMessageDialog(null, "Bitte Quellverzeichnis auswählen (Volume durchsuchen)");
            if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                System.exit(0);
            }
            Path sourceRoot = chooser.getSelectedFile().toPath();

            // Zielordner wählen
            JOptionPane.showMessageDialog(null, "Bitte Zielverzeichnis auswählen");
            if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                System.exit(0);
            }
            Path targetDir = chooser.getSelectedFile().toPath();

            runCollector(sourceRoot, targetDir);
        });
    }

    private static void runCollector(Path sourceRoot, Path targetDir) {
        if (!Files.exists(sourceRoot)) {
            JOptionPane.showMessageDialog(null, "Source path does not exist: " + sourceRoot,
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to create target directory: " + e.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CollectorFileVisitor visitor = new CollectorFileVisitor(targetDir);
        try {
            Files.walkFileTree(sourceRoot, visitor);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Scan failed: " + e.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String summary = String.format("Fertig.\nFiles inspected: %d\nFiles copied: %d\nFiles skipped (identical size): %d\nErrors: %d",
                visitor.filesInspected, visitor.filesCopied, visitor.filesSkipped, visitor.errors);
        JOptionPane.showMessageDialog(null, summary, "Zusammenfassung", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class CollectorFileVisitor extends SimpleFileVisitor<Path> {
        private Path baseTargetDir;
        private Path currentTargetDir;
        private int dirCounter = 1;
        private int filesInCurrentDir = 0;

        long filesInspected = 0;
        long filesCopied = 0;
        long filesSkipped = 0;
        long errors = 0;

        CollectorFileVisitor(Path targetDir) {
            this.baseTargetDir = targetDir;
            this.currentTargetDir = targetDir;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            filesInspected++;
            try {
                if (!attrs.isRegularFile()) return FileVisitResult.CONTINUE;

                String filename = file.getFileName().toString();
                String ext = getFileExtension(filename).toLowerCase(Locale.ROOT);
                if (!EXTENSIONS.contains(ext)) return FileVisitResult.CONTINUE;

                // ggf. neues Zielverzeichnis anlegen
                if (filesInCurrentDir >= FILES_PER_DIR) {
                    dirCounter++;
                    currentTargetDir = baseTargetDir.getParent().resolve(baseTargetDir.getFileName() + "_" + dirCounter);
                    try {
                        if (!Files.exists(currentTargetDir)) {
                            Files.createDirectories(currentTargetDir);
                        }
                    } catch (IOException e) {
                        errors++;
                        System.err.println("Failed to create subdir: " + currentTargetDir);
                        return FileVisitResult.CONTINUE;
                    }
                    filesInCurrentDir = 0;
                }

                Path targetFile = currentTargetDir.resolve(filename);

                if (Files.exists(targetFile)) {
                    long sourceSize = Files.size(file);
                    long targetSize = Files.size(targetFile);
                    if (sourceSize == targetSize) {
                        filesSkipped++;
                        return FileVisitResult.CONTINUE;
                    } else {
                        targetFile = resolveWithCounter(currentTargetDir, filename);
                    }
                }

                Files.copy(file, targetFile, StandardCopyOption.COPY_ATTRIBUTES);
                filesCopied++;
                filesInCurrentDir++;

            } catch (IOException e) {
                errors++;
                System.err.println("Error handling file " + file + ": " + e.getMessage());
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            errors++;
            System.err.println("Cannot access file " + file + ": " + exc.getMessage());
            return FileVisitResult.CONTINUE;
        }

        private static Path resolveWithCounter(Path dir, String originalFilename) {
            String base;
            String ext = "";
            int dot = originalFilename.lastIndexOf('.');
            if (dot >= 0) {
                base = originalFilename.substring(0, dot);
                ext = originalFilename.substring(dot);
            } else {
                base = originalFilename;
            }

            int counter = 1;
            Path candidate;
            do {
                String newName = String.format("%s_%d%s", base, counter, ext);
                candidate = dir.resolve(newName);
                counter++;
            } while (Files.exists(candidate));
            return candidate;
        }

        private static String getFileExtension(String filename) {
            int dot = filename.lastIndexOf('.');
            if (dot >= 0 && dot < filename.length() - 1) {
                return filename.substring(dot + 1);
            }
            return "";
        }
    }
}
