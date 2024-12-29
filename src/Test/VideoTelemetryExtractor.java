package Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VideoTelemetryExtractor {

    public static void main(String[] args) {
        String videoPath = "/Users/x810we/Movies/Telemetrie/GX015443.MP4";  // Pfad zum Video
        String telemetryFilePath = "/Users/x810we/Movies/Telemetrie/Telemetrie.bin";  // Zielpfad für die Telemetrie-Daten
        String scriptPath = "/Users/x810we/extract_telemetry.sh";  // Pfad zum Shell-Script

        try {
            // Schritt 1: Führe das Shell-Skript aus, um die Telemetrie-Daten zu extrahieren
            System.out.println("Starte Telemetrie-Daten Extraktion...");
            runShellScript(scriptPath, videoPath, telemetryFilePath);

            // Schritt 2: Verarbeite die extrahierten Telemetrie-Daten und erzeuge das GPX-File
            System.out.println("Erzeuge GPX-Datei...");
            processTelemetryData(telemetryFilePath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Funktion, um das Shell-Skript auszuführen
    private static void runShellScript(String scriptPath, String videoPath, String telemetryFilePath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(scriptPath, videoPath, telemetryFilePath);
        processBuilder.inheritIO(); // Ermöglicht das Übernehmen der Konsolenausgabe
        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Telemetrie-Daten wurden erfolgreich extrahiert.");
        } else {
            System.err.println("Fehler beim Extrahieren der Telemetrie-Daten. Exit Code: " + exitCode);
        }
    }

    // Funktion, um die Telemetrie-Daten zu verarbeiten und eine GPX-Datei zu erstellen
    private static void processTelemetryData(String telemetryFilePath) {
        // Logik zur Verarbeitung der Telemetrie-Daten und Erzeugung der GPX-Datei
        // Hier kannst du die von dir bereits getesteten Funktionen einbauen
        System.out.println("Verarbeite Telemetrie-Daten...");

        // Beispiel: Um die Telemetrie-Daten in eine GPX-Datei zu konvertieren
        // (Du kannst die logische Verarbeitung hier implementieren, die du bereits getestet hast)
        // createGPXFromTelemetry(telemetryFilePath);
        String telemetryFile = "/Users/x810we/Movies/Telemetrie/telemetrie.bin"; // Pfad zur Binärdatei
        String gpxOutput = "/Users/x810we/Movies/Telemetrie/telemetrie.gpx";

        try {
            processTelemetry(telemetryFile, gpxOutput);
            System.out.println("GPX-Datei erfolgreich erstellt: " + gpxOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private static void processTelemetry(String telemetryFile, String gpxOutput) throws IOException {
        try (FileInputStream fis = new FileInputStream(telemetryFile);
             BufferedWriter writer = new BufferedWriter(new FileWriter(gpxOutput))) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<gpx version=\"1.1\" creator=\"TelemetryToGPX\">\n");
            writer.write("  <trk>\n    <name>Telemetry Track</name>\n    <trkseg>\n");

            byte[] buffer = new byte[16]; // Anpassung der Größe für zusätzliche Geschwindigkeit
            while (fis.read(buffer) != -1) {
                ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

                // Beispiel: Extrahiere GPS-Daten
                double latitude = bb.getInt(0) / 1e7; // Beispiel-Skalierung
                double longitude = bb.getInt(4) / 1e7;
                double elevation = bb.getShort(8); // Beispiel: Höhe aus Short-Wert
                double speed = bb.getShort(10) / 100.0; // Beispiel: Geschwindigkeit in m/s (Skalierung anpassen)

                // Debug: Zeige die Werte
                System.out.printf("Lat: %.6f, Lon: %.6f, Ele: %.2f, Speed: %.2f m/s%n",
                        latitude, longitude, elevation, speed);

                writer.write(String.format("      <trkpt lat=\"%.6f\" lon=\"%.6f\">\n", latitude, longitude));
                writer.write(String.format("        <ele>%.2f</ele>\n", elevation));
                writer.write(String.format("        <extensions>\n          <speed>%.2f</speed>\n        </extensions>\n", speed));
                writer.write("      </trkpt>\n");
            }

            writer.write("    </trkseg>\n  </trk>\n</gpx>");
        }
    }
}
