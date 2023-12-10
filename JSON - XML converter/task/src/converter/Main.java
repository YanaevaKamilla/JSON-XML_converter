package converter;

import java.io.*;

/**
 * The Main class contains the main method to demonstrate the FileConverter functionality.
 */
public class Main {
    public static void main(String[] args) {
        // Specify the path to the test file (update the path as needed)
        File file = new File(".\\test.txt");
//        File file = new File(".\\JSON - XML converter\\task\\src\\test.txt");

        // Invoke the convertFile method from FileConverter
        FileConverter.convertFile(file);
    }
}