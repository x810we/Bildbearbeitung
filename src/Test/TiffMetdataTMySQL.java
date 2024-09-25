package Test;

import java.text.DateFormat;
import java.util.GregorianCalendar;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import java.sql.*;
import java.io.File;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TiffMetdataTMySQL {

    static String printSimpleDateFormat() {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        System.out.println(formatter.format(currentTime));
        return formatter.format(currentTime);// 2012.04.14 - 21:34:07
    }

    public static void main(String[] args) throws Exception {
      //  File tiffFile = new File("/Users/x810we/Pictures/IMG_1903.tiff");
        File tiffFile = new File("/Users/x810we/Pictures/FB/Farbchart.tif");
        // Verbindung zur MySQL-Datenbank herstellen
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Bild", "x810we", "soswind22");

        // TIFF-Datei einlesen ttt
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


        GregorianCalendar now = new GregorianCalendar();
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);

        // Hole alle Attribute des Knotens
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                String tagName = attr.getNodeName();
                String tagValue = attr.getNodeValue();

                String tagValue2 = "2024-09-25 10:00:00.000000000";

                tagValue2 = String.valueOf(Timestamp.valueOf( tagValue2));
                tagValue2 = printSimpleDateFormat();



                // Metadaten in die Datenbank einfügen
                String insertQuery = "INSERT INTO tiff_metadata (image_index, tag_name, tag_value, insert_time) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                    pstmt.setInt(1, imageIndex);
                    pstmt.setString(2, tagName);
                    pstmt.setString(3, tagValue);
                    pstmt.setTimestamp(4, Timestamp.valueOf(tagValue2));
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
