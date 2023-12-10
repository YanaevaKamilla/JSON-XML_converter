package converter;

import java.util.List;
import java.util.Objects;

/**
 * The ElementObjectParser class provides static methods for converting Element objects to JSON and XML formats.
 */
public class ElementObjectParser {

    /**
     * Converts an Element object to its JSON representation.
     *
     * @param root the root element of the hierarchy
     * @return the JSON representation of the element hierarchy
     */
    public static String getJson(Element root) {
        return buildJson(root, "", true, false);
    }

    /**
     * Converts an Element object to its XML representation.
     *
     * @param root the root element of the hierarchy
     * @return the XML representation of the element hierarchy
     */
    public static String getXml(Element root) {
        return buildXml(root, "");
    }

    /**
     * Recursively builds the XML representation of an Element and its hierarchy.
     *
     * @param element the current element being processed
     * @param tabs    the indentation string based on the hierarchy level
     * @return the XML representation of the current element and its children
     */
    private static String buildXml(Element element, String tabs) {
        StringBuilder attributes = new StringBuilder();
        String name = element.getName();
        String value = element.getValue();
        // Remove quotes from the value for XML
        if (value != null) {
            value = value.replaceAll("\"", "");
        }
        element.getAttributes().forEach(a -> attributes.append(" ").append(a));
        if (name.isEmpty()) {
            // Use "root" as the default name if the element has multiple children
            if (element.getChildren().size() > 1) {
                name = "root";
            } else {
                return buildXml(element.getChildren().get(0), tabs);
            }
        }
        if (element.isLeaf()) {
            if (Objects.equals(value, "null")) {
                return String.format("%s<%s%s/>", tabs, name, attributes);
            } else {
                return String.format("%s<%s%s>%s</%s>", tabs, name, attributes, value, name);
            }
        } else {
            StringBuilder string = new StringBuilder(String.format("%s<%s%s>", tabs, name, attributes));
            element.getChildren().forEach(e -> string.append("\n").append(buildXml(e, tabs + "\t")));
            string.append(String.format("\n%s</%s>", tabs, name));
            return string.toString();
        }
    }

    /**
     * Recursively builds the JSON representation of an Element and its hierarchy.
     *
     * @param element        the current element being processed
     * @param tabs           the indentation string based on the hierarchy level
     * @param isLast         a flag indicating if the current element is the last in its parent's list
     * @param isArrayElement a flag indicating if the current element is part of an array
     * @return the JSON representation of the current element and its children
     */
    private static String buildJson(Element element, String tabs, boolean isLast, boolean isArrayElement) {
        String name = element.getName();
        String value = element.getValue();
        StringBuilder string = new StringBuilder(String.format("%s", tabs));
        // Add the element name to the JSON string, if it exists
        if (!isArrayElement) {
            string.append(String.format("\"%s\": ", name));
        }
        // Remove the last character if the element name is null or blank
        if (name == null || name.isBlank()) {
            string.delete(0, string.length() - 1);
        }

        // Handle arrays in JSON
        if (element.isArray() && element.getAttributes().isEmpty()) {
            string.append("[");
            appendChildren(element, tabs, string, true);
        }
        // Handle non-array elements without attributes
        else if (element.getAttributes().isEmpty()) {
            if (element.isLeaf()) {
                string.append(value);
            } else {
                string.append("{");
                appendChildren(element, tabs, string, false);
            }
        }
        // Handle elements with attributes
        else {
            string.append("{");
            appendAttributes(element, tabs, string);
            if (element.isLeaf()) {
                string.append(String.format("%n%s\t\"#%s\": %s%n%s}", tabs, name, value, tabs));
            } else {
                boolean isArray = element.isArray();
                String openBracket = isArray ? "[" : "{";
                string.append(String.format("%n%s\t\"#%s\": %s", tabs, name, openBracket));
                appendChildren(element, tabs + "\t", string, isArray);
                string.append(String.format("%n%s}", tabs));
            }
        }
        // Add a comma if the element is not the last in its parent's list
        if (!isLast) string.append(",");
        return string.toString();
    }

    /**
     * Appends the JSON representation of an Element's children to the string.
     *
     * @param element      the current element whose children are being processed
     * @param tabs         the indentation string based on the hierarchy level
     * @param string       the StringBuilder to which the JSON representation is appended
     * @param isArray      a flag indicating if the current element is part of an array
     */
    private static void appendChildren(Element element, String tabs, StringBuilder string, boolean isArray) {
        List<Element> children = element.getChildren();
        for (int i = 0; i < children.size(); i++) {
            string.append(String.format("%n%s",
                    buildJson(children.get(i), tabs + "\t", i == children.size() - 1, isArray)));
        }
        string.append(String.format("%n%s%s", tabs, isArray ? "]" : "}"));
    }

    /**
     * Appends the JSON representation of an Element's attributes to the string.
     *
     * @param element the current element whose attributes are being processed
     * @param tabs    the indentation string based on the hierarchy level
     * @param string  the StringBuilder to which the JSON representation is appended
     */
    private static void appendAttributes(Element element, String tabs, StringBuilder string) {
        element.getAttributes().forEach(e -> {
            String[] attribute = e.split("=");
            String attributeFormatted = String.format("\"@%s\": %s", attribute[0].trim(), attribute[1].trim());
            string.append(String.format("%n%s\t%s,", tabs, attributeFormatted));
        });
    }
}