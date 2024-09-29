package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageProcessingGUI3 extends JFrame {
    private static final String JSX_SCRIPT_PATH = "/Users/x810we/Pictures/FB/AAA.jsx";
    private static final String PORTRAIT_IMAGE_PATH = "/Users/x810we/Pictures/FB/Portrait.jpg";
    private static final String LOW_FREQUENCY_IMAGE_PATH = "/Users/x810we/Pictures/FB/lowPortrait.jpg";
    private static final String HIGH_FREQUENCY_IMAGE_PATH = "/Users/x810we/Pictures/FB/highPortrait.jpg";

    public ImageProcessingGUI3() {
        // Fenster konfigurieren
        setTitle("Bildbearbeitung GUI");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Menüleiste erstellen
        JMenuBar menuBar = new JMenuBar();

        // Datei-Menü
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem openItem = new JMenuItem("Öffnen");
        JMenuItem exitItem = new JMenuItem("Beenden");

        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Bearbeitungs-Menü
        JMenu editMenu = new JMenu("Bearbeitung");
        JMenuItem undoItem = new JMenuItem("Rückgängig");
        editMenu.add(undoItem);

        // MischModus-Menü
        JMenu blendModeMenu = new JMenu("MischModus");
        JMenuItem saturationItem = new JMenuItem("Sättigungsmodus");
        blendModeMenu.add(saturationItem);

        // Aktionen für Menüeinträge
        exitItem.addActionListener(e -> System.exit(0));
        openItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Öffnen-Funktion noch nicht implementiert."));
        saturationItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Sättigungsmodus ausgewählt."));
        undoItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Rückgängig noch nicht implementiert."));

        // Menüleiste hinzufügen
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(blendModeMenu);

        // Menüleiste zum Frame hinzufügen
        setJMenuBar(menuBar);

        // Panel für Bildbearbeitung erstellen
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Überschrift
        JLabel headerLabel = new JLabel("Bildbearbeitung IW", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(headerLabel);

        JLabel headerLabelIW = new JLabel("", JLabel.RIGHT);
        headerLabelIW.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(headerLabelIW);

        // Fünf Zeilen mit Button, Textfeld und Parameterfeldern
        for (int i = 1; i <= 5; i++) {
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new FlowLayout());

            JButton button = new JButton("Starte Programm " + i);
            JTextField textField = new JTextField(40);
            JTextField param1Field = new JTextField(10);
            JTextField param2Field = new JTextField(10);

            // ActionListener für Button
            int programNumber = i;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = textField.getText();
                    String param1 = param1Field.getText();
                    String param2 = param2Field.getText();
                    if (programNumber == 1){
                        PhotoshopAuto();
                    }

                    // Simuliert das Starten eines anderen Programms
                    JOptionPane.showMessageDialog(null, "Starte Programm " + programNumber + "\n"
                            + "Eingabetext: " + text + "\n"
                            + "Parameter 1: " + param1 + "\n"
                            + "Parameter 2: " + param2);
                }
            });

            // Zeile zum Panel hinzufügen
            rowPanel.add(button);
            rowPanel.add(textField);
            rowPanel.add(param1Field);
            rowPanel.add(param2Field);
            mainPanel.add(rowPanel);
        }

        // Panel zum Frame hinzufügen
        add(mainPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // GUI im Event-Dispatch-Thread starten
        SwingUtilities.invokeLater(() -> {
            ImageProcessingGUI3 gui = new ImageProcessingGUI3();
            gui.setVisible(true);
        });
    }

    private static void runPhotoshopScript(String scriptPath) throws IOException, InterruptedException {

        Path photoshopExe = Paths.get("/Applications/Adobe Photoshop 2024/Adobe Photoshop 2024.app/Contents/MacOS/Adobe Photoshop 2024");
        String[] command = { photoshopExe.toString(), "-r", scriptPath };
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
    }

    private static void generateJSXScript(String portraitPath, String lowFreqPath, String highFreqPath, String scriptPath) throws IOException {
        FileWriter writer = new FileWriter(scriptPath);
        writer.write(
                "var doc = app.documents.add();\n" +
                        "var portrait = app.open(File('" + portraitPath.replace("\\", "/") + "'));\n" +
                        "var lowFrequency = app.open(File('" + lowFreqPath.replace("\\", "/") + "'));\n" +
                        "var highFrequency = app.open(File('" + highFreqPath.replace("\\", "/") + "'));\n" +

                        // Copy portrait layer into new document
                        "portrait.artLayers[0].duplicate(doc, ElementPlacement.PLACEATEND);\n" +
                        "portrait.close(SaveOptions.DONOTSAVECHANGES);\n" +

                        // Copy lowFrequency layer into new document
                        "lowFrequency.artLayers[0].duplicate(doc, ElementPlacement.PLACEATEND);\n" +
                        "lowFrequency.close(SaveOptions.DONOTSAVECHANGES);\n" +

                        // Copy highFrequency layer into new document
                        "highFrequency.artLayers[0].duplicate(doc, ElementPlacement.PLACEATEND);\n" +
                        "highFrequency.close(SaveOptions.DONOTSAVECHANGES);\n" +

                        // Rearrange the layers if necessary, e.g., highFrequency at the top
                        "doc.artLayers[0].name = 'High Frequency';\n" +
                        "doc.artLayers[1].name = 'Low Frequency';\n" +
                        "doc.artLayers[2].name = 'Portrait';\n"
        );
        writer.close();
    }

    public static void PhotoshopAuto() {
        try {
            // Generate the JSX script dynamically

            generateJSXScript(PORTRAIT_IMAGE_PATH, LOW_FREQUENCY_IMAGE_PATH, HIGH_FREQUENCY_IMAGE_PATH, JSX_SCRIPT_PATH);
            // Execute the JSX script using Photoshop's command-line interface
            runPhotoshopScript(JSX_SCRIPT_PATH);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Methode zum Starten des externen Programms
    public void startDatabaseProgram() {
        String command = "java -jar /Users/x810we/IdeaProjects/MySql/out/artifacts/MySql_jar/MySql.jar";

        try {
            // Startet das externe Programm
            Process process = Runtime.getRuntime().exec(command);
            // Wartet, bis das Programm beendet ist
            int exitCode = process.waitFor();
            System.out.println("Programm beendet mit Exit-Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
