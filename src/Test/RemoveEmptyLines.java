package Test;
import java.io.*;

public class RemoveEmptyLines {
    public static void main(String[] args) {
        // Datei-Pfade festlegen
        String inputFile = "/Users/x810we/Temp/psl/daten.txt";
        String outputFile  = "/Users/x810we/Temp/psl/daten.txt";


        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Nur Zeilen schreiben, die nicht leer sind
                if (!line.trim().isEmpty()) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Die Datei wurde erfolgreich bereinigt und in daten_cleaned.txt gespeichert.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
