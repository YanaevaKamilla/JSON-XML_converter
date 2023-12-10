package converter;

/**
 * The Converter interface provides a contract for classes that implement
 * conversion functionality. Any class that implements this interface
 * must define the convert method, which takes a String toConvert as input
 * and returns the converted result as a String.
 */
public interface Converter {

    /**
     * Converts the input string to a different representation.
     *
     * @param toConvert the string to be converted
     * @return the converted string
     */
    String convert(String toConvert);
}