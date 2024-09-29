package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImageProcessingGUI extends JFrame {

    private JTextField rField, gField, bField, alphaField, scaleField, offsetField;
    private JButton calculateButton;
    public ImageProcessingGUI() {
        // Fenster konfigurieren
        setTitle("Bildbearbeitung GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Menüleiste erstellen
        JMenuBar menuBar = new JMenuBar();

        // Datei-Menü
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem openItem = new JMenuItem("Öffnen");
        JMenuItem exitItem = new JMenuItem("Beenden");
        JMenuItem testItem = new JMenuItem("Testen");


        fileMenu.add(openItem);
        fileMenu.addSeparator();  // Trennlinie
        fileMenu.add(testItem);
        fileMenu.add(exitItem);

        // Bearbeitungs-Menü
        JMenu editMenu = new JMenu("Bearbeitung");
        JMenuItem undoItem = new JMenuItem("Rückgängig");
        editMenu.add(undoItem);

        // MischModus-Menü
        JMenu blendModeMenu = new JMenu("MischModus");
        JMenuItem saturationItem = new JMenuItem("Sättigungsmodus");
        blendModeMenu.add(saturationItem);

        fileMenu.setText("Bildbearbeitung");
        setVisible(true);


        // Aktionen für Menüeinträge hinzufügen
        exitItem.addActionListener(e -> System.exit(0));  // Beenden der Anwendung
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Datei öffnen (noch keine Funktionalität)

                JOptionPane.showMessageDialog(null, "Öffnen-Funktion noch nicht implementiert.");
            }
        });

        saturationItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Sättigungsmodus ausgewählt."));
        undoItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Rückgängig noch nicht implementiert."));

        // Menüs zur Menüleiste hinzufügen
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(blendModeMenu);

        // Menüleiste zum Frame hinzufügen
        setJMenuBar(menuBar);


    }

    public static void main(String[] args) {
        // GUI im Event-Dispatch-Thread starten
        SwingUtilities.invokeLater(() -> {
            ImageProcessingGUI gui = new ImageProcessingGUI();
            gui.setVisible(true);
        });
    }
}
