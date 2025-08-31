package Test;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class SortCsvColumn {

    public static void main(String[] args) {

        // if (args.length != 2) {
        //    System.err.println("Usage: java SortCsvColumn <input.csv> <output.csv>");
        //   System.exit(1);
       // }
        args[0] = "/Volumes/MyBook3/25_Dropbox2025-06-08/Daten/Tennis/Saison2026/TeilnehmerCSV.csv";

        String inputFile = args[0];
        String outputFile = "/Volumes/MyBook3/25_Dropbox2025-06-08/Daten/Tennis/Saison2026/TeilnehmerCSVou.csv";

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {

            String header = reader.readLine();
            if (header != null) {
                // Schreibe neuen Header, z. B. mit zusätzlicher Spalte
                writer.write(header + ",SortedColumn");
                writer.newLine();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                // Annahme: CSV ist durch Komma getrennt
                String[] parts = line.split(",", -1);
                if (parts.length < 1) {
                    // leere oder ungültige Zeile
                    writer.write(line + ",");
                    writer.newLine();
                    continue;
                }

                String col1 = parts[0].trim();
                String sorted = sortFourLetters(col1);

                // Baue die neue Zeile: original + neue Spalte
                StringBuilder sb = new StringBuilder();
                sb.append(line);
                sb.append(",");
                sb.append(sorted);

                writer.write(sb.toString());
                writer.newLine();
            }

            System.out.println("Fertig. Ausgabe in " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String sortFourLetters(String input) {
        // Erwartet z. B. "B + G + C + H" oder "B+G+C+H"
        // Entferne nicht Buchstaben und teile auf
        // Wir nehmen Großbuchstaben A-Z an
        // Filtere Buchstaben, sortiere, füge mit " + " zusammen

        List<Character> letters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                letters.add(Character.toUpperCase(c));
            }
        }

        // Falls weniger oder mehr als 4 Buchstaben, entscheide wie du willst:
        // Hier: sortiere alle, aber wir nehmen nur die ersten 4, wenn mehr
        Collections.sort(letters);

        // Wenn weniger als 4, fülle evtl. mit leerem Wert oder ignoriere
        // Hier nehmen wir einfach alle, die da sind
        String sorted;
        if (letters.isEmpty()) {
            sorted = "";
        } else {
            sorted = letters.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" + "));
        }
        return sorted;
    }
}
