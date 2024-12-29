package Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GPMFParser {

    public static void main(String[] args) {
        String telemetryFile = "/Users/x810we/telemetry.bin";

        try (FileInputStream fis = new FileInputStream(telemetryFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                ByteBuffer bb = ByteBuffer.wrap(buffer, 0, bytesRead);
                bb.order(ByteOrder.nativeOrder()); // Ensure correct endianness

                // Parse data
                while (bb.remaining() >= 8) { // At least enough for a tag and length
                    int tag = bb.getInt();
                    int length = bb.getInt();

                    // Sanity check for the length field
                    if (length < 0 || length > bb.remaining()) {
                        System.err.println("Invalid length field: " + length);
                        break; // Exit the parsing loop to prevent errors
                    }

                    byte[] data = new byte[length];
                    bb.get(data); // Extract the data

                    System.out.printf("Tag: %c%c%c%c, Length: %d, Data: [First byte: %02X]%n",
                            (char) ((tag >> 24) & 0xFF), (char) ((tag >> 16) & 0xFF),
                            (char) ((tag >> 8) & 0xFF), (char) (tag & 0xFF),
                            length, data.length > 0 ? data[0] : 0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading telemetry file: " + e.getMessage());
        }
    }
}
