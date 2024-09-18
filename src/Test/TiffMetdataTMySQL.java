package Test;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import java.sql.*;
import java.io.File;
import java.util.Iterator;

public class TiffMetdataTMySQL {

    public static void main(String[] args) throws Exception {
        File tiffFile = new File("/Users/x810we/Pictures/IMG_1903.tiff");

        // Verbindung zur MySQL-Datenbank herstellen
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Bild", "root", "soswind22");

        // TIFF-Datei einlesen
        ImageInputStream inputStream = ImageIO.createImageInputStream(tiffFile);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);
        if (!readers.hasNext()) {
            throw new RuntimeException("Kein TIFF ImageReader gefunden!");
        }

        ImageReader reader = readers.next();
        reader.setInput(inputStream, false);

        // Durchlaufe alle Bilder im TIFF-Bild (für Mehrseitige TIFF-Dateien)
        for (int i = 0; i < reader.getNumImages(true); i++) {
            System.out.println("Metadaten für Bildindex " + i + ":");

            IIOMetadata metadata = reader.getImageMetadata(i);
            String[] metadataFormatNames = metadata.getMetadataFormatNames();
            for (String formatName : metadataFormatNames) {
                Node metadataTree = metadata.getAsTree(formatName);
                if (metadataTree != null) {
                    saveMetadataToDatabase(metadataTree, connection, i);
                }
            }
        }

        inputStream.close();
        connection.close();
    }

    // Methode zur Speicherung der Metadaten in die Datenbank
    private static void saveMetadataToDatabase(Node node, Connection connection, int imageIndex) throws SQLException {
        if (node == null) return;

        // Hole alle Attribute des Knotens
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                String tagName = attr.getNodeName();
                String tagValue = attr.getNodeValue();

                // Metadaten in die Datenbank einfügen
                String insertQuery = "INSERT INTO tiff_metadata (image_index, tag_name, tag_value) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                    pstmt.setInt(1, imageIndex);
                    pstmt.setString(2, tagName);
                    pstmt.setString(3, tagValue);
                    pstmt.executeUpdate();
                }
            }
        }

        // Rekursion für Kindknoten
        Node child = node.getFirstChild();
        while (child != null) {
            saveMetadataToDatabase(child, connection, imageIndex);
            child = child.getNextSibling();
        }
    }
}
