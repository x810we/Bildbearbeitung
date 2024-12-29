package Test;

import java.io.*;
import java.util.*;

public class VideoTelemetryTOGPX2 {
    private static final String VIDEO_FILE = "/Users/x810we/Movies/GX018244.MP4";
    private static final String TELEMETRY_FILE = "/Users/x810we/telemetry.bin";
    private static final String GPX_OUTPUT = "/Users/x810we/telemetry.gpx";

    public static void main(String[] args) {
        try {
            extractTelemetry(VIDEO_FILE, TELEMETRY_FILE);
            writeGPX(TELEMETRY_FILE, GPX_OUTPUT);
            System.out.println("GPX-Datei erfolgreich erstellt: " + GPX_OUTPUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractTelemetry(String videoFile, String telemetryFile) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", videoFile, "-map", "0:3", "-f", "data", telemetryFile
        );
        pb.redirectErrorStream(true);

        Process process = pb.start();

        int counter = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((((line = reader.readLine())) != null) && (counter < 20)) {
                counter = counter + 1;
                System.out.println("FFmpeg: " + line);
            }  System.out.println("FFmpeg: " + "Ende");
        }

       /* int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg Telemetriedaten Extraktion fehlgeschlagen! Exit-Code: " + exitCode);
        }*/
    }

    private static void writeGPX(String telemetryFile, String gpxOutput) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(telemetryFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(gpxOutput))) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<gpx version=\"1.1\" creator=\"JavaTelemetryToGPX\">\n");
            writer.write("  <trk>\n    <name>Telemetry Track</name>\n    <trkseg>\n");

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Debug Telemetry Line: " + line);
                String[] tokens = line.trim().split(",");
                if (tokens.length < 3) {
                    System.err.println("Ungültiger Datensatz übersprungen: " + line);
                    continue;
                }

                try {
                    double latitude = Double.parseDouble(tokens[0]);
                    double longitude = Double.parseDouble(tokens[1]);
                    double elevation = tokens.length > 2 ? Double.parseDouble(tokens[2]) : 0.0;

                    writer.write(String.format("      <trkpt lat=\"%.6f\" lon=\"%.6f\">\n", latitude, longitude));
                    writer.write(String.format("        <ele>%.2f</ele>\n", elevation));
                    writer.write("      </trkpt>\n");
                } catch (NumberFormatException e) {
                    System.err.println("Fehler beim Parsen von Daten: " + line);
                }
            }

            writer.write("    </trkseg>\n  </trk>\n</gpx>");
        }
    }
}
