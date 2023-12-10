package converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * The FileConverter class provides methods for converting files between XML and JSON formats.
 */
public class FileConverter {

    /**
     * Converts the content of a file from XML to JSON or JSON to XML and prints the converted content.
     *
     * @param file the file to be converted
     */
    public static void convertFile(File file) {
        // Read the content of the file
        String input = readFile(file);

        // Check if the input is empty
        if (input.isEmpty()) {
            System.out.println("Input is Empty");
            return;
        }

        // Determine the file format based on the first character of the input
        Converter converter = switch (input.charAt(0)) {
            case '<' -> new XmlParser();
            case '{' -> new JsonParser();
            default -> throw new IllegalStateException("Unexpected value");
        };

        // Convert the content and print the result
        String converted = converter.convert(input);
        System.out.println(converted);

        // Uncomment the following line to write the converted content back to the file
        // writeToFile(file, converted);
    }

    /**
     * Writes the converted content to the specified file.
     *
     * @param file      the file to write the converted content to
     * @param converted the converted content to be written
     */
    private static void writeToFile(File file, String converted) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(converted);
            System.out.println("Conversion successful.");
        } catch (IOException e) {
            System.out.println("Cannot write to file.");
        }
    }

    /**
     * Reads the content of a file and returns it as a single string.
     *
     * @param file the file to read
     * @return the content of the file as a single string
     */
    private static String readFile(File file) {
        StringBuilder lines = new StringBuilder();
        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()) {
                lines.append(scanner.nextLine().trim());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        return lines.toString();
    }
}