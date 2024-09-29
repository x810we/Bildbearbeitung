package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageProcessingGUI2 extends JFrame {

    private static final String JSX_SCRIPT_PATH = "/Users/x810we/Pictures/FB/frequency_separation_script.jsx";

    public ImageProcessingGUI2() {
        // Fenster konfigurieren
        setTitle("Bildbearbeitung GUI2");
        setSize(800, 400);
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

        // Menüleiste zum Frame hinzufügen
        setJMenuBar(menuBar);

        // Panel für Bildbearbeitung erstellen
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Überschrift
        JLabel headerLabel = new JLabel("Bildbearbeitung", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(headerLabel);

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


                    try {
                        runPhotoshopScript(JSX_SCRIPT_PATH);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
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
            ImageProcessingGUI2 gui = new ImageProcessingGUI2();
            gui.setVisible(true);
        });
    }
    private static void runPhotoshopScript(String scriptPath) throws IOException, InterruptedException {

        Path photoshopExe = Paths.get("/Applications/Adobe Photoshop 2024/Adobe Photoshop 2024.app/Contents/MacOS/Adobe Photoshop 2024");
        String[] command = { photoshopExe.toString(), "-r", scriptPath };
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
    }
}
