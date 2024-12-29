package Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VideoTelemetryToGPXGesamt {

    public static void main(String[] args) {
        String videoFile = "/Users/x810we/Movies/GX018244.MP4"; // Pfad zur Video-Datei
        String telemetryFile = "/Users/x810we/Movies/telemetry.bin"; // Zwischendatei für Telemetriedaten
        String gpxOutput = "/Users/x810we/Movies/output.gpx"; // Ausgabe-GPX-Datei

        try {
            // Schritt 1: Telemetriedaten extrahieren
            extractTelemetry(videoFile, telemetryFile);

            // Schritt 2: GPX-Datei aus Telemetriedaten erzeugen
            processTelemetry(telemetryFile, gpxOutput);

            System.out.println("GPX-Datei erfolgreich erstellt: " + gpxOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Extrahiert Telemetriedaten aus einem Video mithilfe von FFmpeg.
     */
    private static boolean extractTelemetry(String videoFilePath, String outputTelemetryPath) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-i", videoFilePath, "-map", "0:3", "-f", "rawvideo", outputTelemetryPath
        );
        processBuilder.redirectErrorStream(true); // Fehlerausgabe mit Standardausgabe kombinieren

        try {
            Process process = processBuilder.start();

            // Einen separaten Thread für das Auslesen der Ausgabe
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line); // Debug-Ausgabe
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputReader.start();

            // Warten auf Prozessende
            int exitCode = process.waitFor();
            outputReader.join(); // Warten, bis der Reader-Thread fertig ist

            if (exitCode == 0) {
                System.out.println("Telemetry extraction successful.");
                return true;
            } else {
                System.err.println("FFmpeg failed with exit code: " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Liest Telemetriedaten aus einer Binärdatei und schreibt sie in eine GPX-Datei.
     */
    private static void processTelemetry(String telemetryFile, String gpxOutput) throws IOException {
        try (FileInputStream fis = new FileInputStream(telemetryFile);
             BufferedWriter writer = new BufferedWriter(new FileWriter(gpxOutput))) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<gpx version=\"1.1\" creator=\"TelemetryToGPX\">\n");
            writer.write("  <trk>\n    <name>Telemetry Track</name>\n    <trkseg>\n");

            byte[] buffer = new byte[28]; // Puffergröße passend für GPS-Daten (z. B. 28 Bytes)
            while (fis.read(buffer) != -1) {
                ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

                // GPS-Daten extrahieren
                double latitude = bb.getInt(0) / 1e7; // Beispiel-Skalierung für Latitude
                double longitude = bb.getInt(4) / 1e7; // Beispiel-Skalierung für Longitude
                double elevation = bb.getShort(8); // Höhe in Metern
                double speed = bb.getShort(10) / 100.0; // Geschwindigkeit in m/s

                // Debug: Werte ausgeben
                System.out.printf("Lat: %.6f, Lon: %.6f, Ele: %.2f, Speed: %.2f m/s%n",
                        latitude, longitude, elevation, speed);

                // In GPX schreiben
                writer.write(String.format("      <trkpt lat=\"%.6f\" lon=\"%.6f\">\n", latitude, longitude));
                writer.write(String.format("        <ele>%.2f</ele>\n", elevation));
                writer.write(String.format("        <extensions>\n          <speed>%.2f</speed>\n        </extensions>\n", speed));
                writer.write("      </trkpt>\n");
            }

            writer.write("    </trkseg>\n  </trk>\n</gpx>");
        }
    }
}
