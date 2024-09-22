package Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PhotoshopAutomation {

    // Paths to your frequency separation images
    private static final String PORTRAIT_IMAGE_PATH = "/Users/x810we/Pictures/FB/Portrait.jpg";
    private static final String LOW_FREQUENCY_IMAGE_PATH = "/Users/x810we/Pictures/FB/lowPortrait.jpg";
    private static final String HIGH_FREQUENCY_IMAGE_PATH = "/Users/x810we/Pictures/FB/highPortrait.jpg";

    // Photoshop JSX script path
    private static final String JSX_SCRIPT_PATH = "/Users/x810we/Pictures/FB/frequency_separation_script.jsx";





    public static void main(String[] args) {
        try {
            // Generate the JSX script dynamically
            generateJSXScript(PORTRAIT_IMAGE_PATH, LOW_FREQUENCY_IMAGE_PATH, HIGH_FREQUENCY_IMAGE_PATH, JSX_SCRIPT_PATH);

            // Execute the JSX script using Photoshop's command-line interface
            runPhotoshopScript(JSX_SCRIPT_PATH);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Function to generate the JSX script that Photoshop will run
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

    // Function to run the Photoshop script
    private static void runPhotoshopScript(String scriptPath) throws IOException, InterruptedException {

        Path photoshopExe = Paths.get("/Applications/Adobe Photoshop 2024/Adobe Photoshop 2024.app/Contents/MacOS/Adobe Photoshop 2024");
        String[] command = { photoshopExe.toString(), "-r", scriptPath };
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();







    }


}
