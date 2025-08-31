package Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.nio.file.attribute.FileTime;


import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class SortPhotosByDate {

    public static void main(String[] args) {
        // Pfade anpassen
        Path sourceDir = Paths.get("/Users/x810we/Pictures");
        Path targetBase = Paths.get("/Users/x810we/Pictures");

        // Optional: erlaubte Bildendungen
        String[] extensions = new String[] { ".jpg", ".jpeg", ".png", ".heic" };

        try {
            Files.walk(sourceDir)
                    .filter(Files::isRegularFile)
                    .forEach(sourcePath -> {
                        String fname = sourcePath.toString().toLowerCase();
                        boolean okExt = false;
                        for (String ext : extensions) {
                            if (fname.endsWith(ext)) { okExt = true; break; }
                        }
                        if (!okExt) return; // überspringen, keine Bilddatei

                        // Zeitpunkt ermitteln
                        LocalDate photoDate = null;

                        try {
                            photoDate = getPhotoDateFromExif(sourcePath);
                        } catch (Exception ex) {
                            System.err.println("EXIF-Lesen fehlgeschlagen für " + sourcePath + " : " + ex.getMessage());
                        }

                        if (photoDate == null) {
                            // fallback: Dateisystem-Erstellungszeit
                            try {
                                BasicFileAttributes attrs = Files.readAttributes(sourcePath, BasicFileAttributes.class);
                                FileTime ft = attrs.creationTime();
                                // Bei manchen Dateisystemen kann creationTime == lastModifiedTime sein
                                Instant inst = ft.toInstant();
                                photoDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();
                            } catch (IOException ioe) {
                                System.err.println("Fehler beim Lesen der Dateiattribute: " + sourcePath + " : " + ioe.getMessage());
                                // wenn das auch nicht klappt, setze auf aktuelles Datum
                                photoDate = LocalDate.now();
                            }
                        }

                        // Zielordner bauen: Jahr/Monat/Tag
                        DateTimeFormatter fYear = DateTimeFormatter.ofPattern("yyyy");
                        DateTimeFormatter fMonth = DateTimeFormatter.ofPattern("MM");
                        DateTimeFormatter fDay = DateTimeFormatter.ofPattern("dd");

                        Path destDir = targetBase
                                .resolve(photoDate.format(fYear))
                                .resolve(photoDate.format(fMonth))
                                .resolve(photoDate.format(fDay));

                        try {
                            Files.createDirectories(destDir);
                        } catch (IOException ioe) {
                            System.err.println("Ordner erstellen fehlgeschlagen: " + destDir + " : " + ioe.getMessage());
                            return;
                        }

                        // Datei kopieren
                        Path destFile = destDir.resolve(sourcePath.getFileName());
                        try {
                            // falls existiert, ggf. einfügen eines Suffixes o.ä., hier einfach überschreiben vermeiden
                            if (!Files.exists(destFile)) {
                                Files.copy(sourcePath, destFile);
                                System.out.println("kopiert: " + sourcePath + " → " + destFile);
                            } else {
                                System.out.println("Ziel existiert schon, wird übersprungen: " + destFile);
                            }
                        } catch (IOException ioe) {
                            System.err.println("Kopieren fehlgeschlagen: " + sourcePath + " → " + destFile + " : " + ioe.getMessage());
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Versucht, aus EXIF-Daten das Aufnahmedatum auszulesen.
     * Gibt null zurück, wenn nicht vorhanden.
     */
    private static LocalDate getPhotoDateFromExif(Path imagePath) {
        try {
            File imgFile = imagePath.toFile();
            Metadata metadata = ImageMetadataReader.readMetadata(imgFile);
            // ExifSubIFDDirectory enthält typischerweise Tag für Original-Aufnahmedatum
            ExifSubIFDDirectory exifDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifDir != null) {
                Date date = exifDir.getDateOriginal();  // kann null sein
                if (date != null) {
                    Instant inst = date.toInstant();
                    return inst.atZone(ZoneId.systemDefault()).toLocalDate();
                }
            }
        } catch (Exception e) {
            // Ignoriere Fehler, gebe null zurück, damit Fallback läuft
        }
        return null;
    }
}
