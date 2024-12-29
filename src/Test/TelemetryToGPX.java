package Test;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TelemetryToGPX {

    public static void main(String[] args) {
/*
        try {
            Process process = new ProcessBuilder("./Users/x810we/extract_telemetry.sh", "/Users/x810we/Movies/GX018244.MP4", "/Users/x810we/Movies/telemetry.bin").start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
*/

        String telemetryFile = "/Users/x810we/telemetry.bin"; // Pfad zur Binärdatei
        String gpxOutput = "/Users/x810we/telemetry.gpx";

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

            byte[] buffer = new byte[28]; // Anpassung der Größe für zusätzliche Geschwindigkeit
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

