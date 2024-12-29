package Test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VideoTelemetryToGPX {

    // Methode: Extrahiert Telemetriedaten mit FFmpeg
    public static String extractTelemetry(String videoPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", videoPath,
                "-map", "0:3", // Extrahiert die Telemetriedaten
                "-c", "copy",
                "-f", "rawvideo",
                "-"
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder telemetryData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            telemetryData.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg Telemetriedaten Extraktion fehlgeschlagen!");
        }

        return telemetryData.toString();
    }

    // Methode: Telemetriedaten in GPX-XML konvertieren
    public static void writeGPX(String telemetryData, String outputPath) throws Exception {
        // Parsing der Telemetriedaten (hier vereinfachte Annahme)
        String[] lines = telemetryData.split("\n");

        // GPX-XML erstellen
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Root-Element
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("gpx");
        rootElement.setAttribute("version", "1.1");
        rootElement.setAttribute("creator", "VideoTelemetryToGPX");
        rootElement.setAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
        doc.appendChild(rootElement);

        // Metadata hinzufügen
        Element metadata = doc.createElement("metadata");
        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode("Extracted Telemetry Data"));
        metadata.appendChild(name);
        rootElement.appendChild(metadata);

        // Track hinzufügen
        Element trk = doc.createElement("trk");
        rootElement.appendChild(trk);

        Element trkseg = doc.createElement("trkseg");
        trk.appendChild(trkseg);

        // Einzelne Punkte hinzufügen
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 3) { // Annahme: Latitude, Longitude, Altitude
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);
                double altitude = Double.parseDouble(parts[2]);

                Element trkpt = doc.createElement("trkpt");
                trkpt.setAttribute("lat", String.valueOf(latitude));
                trkpt.setAttribute("lon", String.valueOf(longitude));

                Element ele = doc.createElement("ele");
                ele.appendChild(doc.createTextNode(String.valueOf(altitude)));
                trkpt.appendChild(ele);

                trkseg.appendChild(trkpt);
            }
        }

        // GPX-XML speichern
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(Files.newOutputStream(Paths.get(outputPath)));

        transformer.transform(source, result);
    }

    // Main-Methode
    public static void main(String[] args) {
        /*
        if (args.length < 2) {
            System.out.println("Usage: java VideoTelemetryToGPX <videoPath> <outputGpxPath>");
            return;
        }

        String videoPath = args[0];
        String outputGpxPath = args[1];
        */
        String videoPath = "/Users/x810we/Movies/GX018244.MP4";
        String outputGpxPath = "/Users/x810we/Movies/GX018244.gpx";

        try {
            String telemetryData = extractTelemetry(videoPath);
            writeGPX(telemetryData, outputGpxPath);
            System.out.println("GPX-Datei erfolgreich erstellt: " + outputGpxPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
