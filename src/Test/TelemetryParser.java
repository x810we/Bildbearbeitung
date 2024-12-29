package Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TelemetryParser {
    public static void main(String[] args) {
        String telemetryFilePath = "/Users/x810we/Movies/Telemetrie/Telemetrie.bin";
        parseTelemetry(telemetryFilePath);
    }

    public static void parseTelemetry(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[12]; // GPS-Datensatz könnte z.B. 12 Bytes umfassen: [Lat (4), Long (4), Alt (4)]
            while (fis.read(buffer) != -1) {
                ByteBuffer bb = ByteBuffer.wrap(buffer);
                bb.order(ByteOrder.BIG_ENDIAN); // Prüfe ob BIG_ENDIAN korrekt ist

                // Beispiel für GPS-Extraktion (Skalierung notwendig)
                double latitude = bb.getInt() / 1e7; // Skalierung für GPS in GoPro
                double longitude = bb.getInt() / 1e7;
                double altitude = bb.getInt(); // Höhe in Metern

                System.out.println("Lat: " + latitude + ", Long: " + longitude + ", Alt: " + altitude);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Telemetriedaten: " + e.getMessage());
        }
    }
}
