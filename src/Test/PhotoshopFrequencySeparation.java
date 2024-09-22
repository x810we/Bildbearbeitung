package Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhotoshopFrequencySeparation {

    public static void main(String[] args) {
        // Paths to the images you want to load into Photoshop
        List<String> imagePaths = Arrays.asList(
                "/Users/x810we/Pictures/FB/Portrait.jpg",
                "/Users/x810we/Pictures/FB/lowPortrait.jpg",
                "/Users/x810we/Pictures/FB/highPortrait.jpg"
        );

        // Path to the JSX script that will be generated
        String scriptPath = "/Users/x810we/Pictures/FB/frequency_separation_script.jsx";

        // Generate the JSX script
        generateJSXScript(scriptPath, imagePaths);

        // Start Photoshop and execute the script
        runPhotoshopScript(scriptPath);
    }

    // Method to generate the JSX script file
    private static void generateJSXScript(String scriptPath, List<String> imagePaths) {
        StringBuilder jsxScript = new StringBuilder();

        jsxScript.append("function openAsLayers(filePaths) {\n")
                .append("    var baseFile = new File(filePaths[0]);\n")
                .append("    var baseDoc = open(baseFile);\n")
                .append("    for (var i = 1; i < filePaths.length; i++) {\n")
                .append("        var newFile = new File(filePaths[i]);\n")
                .append("        var newDoc = open(newFile);\n")
                .append("        newDoc.activeLayer.duplicate(baseDoc);\n")
                .append("        newDoc.close(SaveOptions.DONOTSAVECHANGES);\n")
                .append("    }\n")
                .append("    baseDoc.activeDocument = baseDoc;\n")
                .append("}\n");

        // Pass the file paths as a JavaScript array
        jsxScript.append("var filePaths = [\n");
        for (String path : imagePaths) {
            jsxScript.append("    '").append(path.replace("\\", "/")).append("',\n");
        }
        jsxScript.append("];\n");
        jsxScript.append("openAsLayers(filePaths);\n");

        // Write the JSX script to a file
        try (FileWriter writer = new FileWriter(scriptPath)) {
            writer.write(jsxScript.toString());
            System.out.println("JSX script generated at: " + scriptPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to run Photoshop with the JSX script
    private static void runPhotoshopScript(String scriptPath) {
        try {
            // Path to Photoshop's executable on MacOS
            Path photoshopExe = Paths.get("/Applications/Adobe Photoshop 2024/Adobe Photoshop 2024.app/Contents/MacOS/Adobe Photoshop 2024");

            // Construct the command to run Photoshop and execute the JSX script
            String[] command = { photoshopExe.toString(), "-r", scriptPath };

            // Execute the process
            Process process = new ProcessBuilder(command).start();

            // Wait for the process to finish
            process.waitFor(1, TimeUnit.MINUTES);
            System.out.println("Photoshop script executed successfully.");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
