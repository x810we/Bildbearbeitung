package Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExtractTelemetry {
    public static void main(String[] args) {
       /*
        if (args.length != 2) {
            System.err.println("Usage: java TelemetryExtractor <videoFilePath> <outputTelemetryPath>");
            System.exit(1);
        }

        String videoFilePath = args[0];
        String outputTelemetryPath = args[1];

        String videoFilePath = "/Users/x810we/Movies/GX018244.MP4";
        String outputTelemetryPath = "/Users/x810we/telemetry.bin";
*/

        String videoFilePath = "/Users/x810we/Movies/GX018244.MP4";
        String outputTelemetryPath = "/Users/x810we/telemetry.bin";

        String command = String.format("ffmpeg -i %s -map 0:3 -f rawvideo %s", videoFilePath, outputTelemetryPath);

        try {
            Process process = Runtime.getRuntime().exec(command);

            // Log the output of the process
            logProcessOutput(process);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Telemetry extracted successfully to: " + outputTelemetryPath);
            } else {
                System.err.println("FFmpeg process failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void logProcessOutput(Process process) {
        Thread stdoutThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[FFMPEG OUT] " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread stderrThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("[FFMPEG ERR] " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stdoutThread.start();
        stderrThread.start();

        try {
            stdoutThread.join();
            stderrThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
