package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The XmlParser class implements the Converter interface for parsing XML content.
 */
public class XmlParser implements Converter {

    // Regular expression pattern for matching XML nodes
    private final Pattern nodePattern = Pattern.compile("<(\\w*)(\\s+([^</>]*=[^</>]*)*)?>(?:</\\1>" +
            "|((?:(?!</?\\1>).)+|(?:(?:(?!</?\\1>).)*<\\1>(?:(?!</?\\1>).)*</\\1>(?:(?!</?\\1>).)*)*)</\\1>)" +
            "|<(\\w*)(\\s+([^</>]*=[^</>]*)*)?/>");


    /**
     * Converts XML content to JSON format.
     *
     * @param toConvert The XML content to be converted.
     * @return The JSON representation of the XML content.
     */
    @Override
    public String convert(String toConvert) {
        // Parse the XML content and convert it to an Element object
        Element root = parse(toConvert);
        // Convert the Element object to JSON and return the result
        return ElementObjectParser.getJson(root);
    }

    /**
     * Parses the XML content and returns the root Element of the parsed structure.
     *
     * @param toConvert The XML content to be parsed.
     * @return The root Element of the parsed XML structure.
     */
    private Element parse(String toConvert) {
        // Create a root Element for the XML structure
        Element root = new Element("");
        // Create a Matcher using the nodePattern for matching XML nodes
        Matcher matcher = nodePattern.matcher(toConvert);

        // Iterate through the matches and recursively parse XML nodes
        while (matcher.find()) {
            recursiveParse(root, matcher.group());
            // Remove the processed XML node from the original content
            toConvert = toConvert.replace(matcher.group(), "");
        }
        return root;
    }

    /**
     * Recursively parses an XML node and adds the corresponding Element to the parent Element.
     *
     * @param root    The parent Element to which the parsed Element will be added.
     * @param content The XML content of the node to be parsed.
     */
    private void recursiveParse(Element root, String content) {
        // Create a Matcher using the nodePattern for matching XML nodes within the current content
        Matcher matcher = nodePattern.matcher(content);
        Element element;

        // Iterate through the matches and process each XML node
        while (matcher.find()) {
            String name;
            String subContent;

            // Determine the name and content of the XML node
            if (matcher.group(1) == null) {
                name = matcher.group(5);
                subContent = null;
            } else {
                name = matcher.group(1);
                subContent = matcher.group(4) == null ? "" : matcher.group(4);
            }

            // Get the attributes of the XML node
            List<String> attributes = getAttributes(matcher.group(2) == null ? matcher.group(6) : matcher.group(2));

            // Create an Element based on the parsed information
            if (subContent == null || subContent.matches("[^<>]*")) {
                element = new Element(root, name, subContent, false);
            } else {
                element = new Element(root, name, false);
                // Recursively parse the subContent of the XML node
                recursiveParse(element, subContent);
            }

            // Add attributes to the Element
            for (String attribute : attributes) {
                element.addAttribute(attribute);
            }

            // Add the Element to the parent Element
            root.addChild(element);
        }
    }

    /**
     * Extracts attributes from the given attribute group and returns a list of formatted attribute strings.
     *
     * @param group The attribute group from the XML node.
     * @return A list of formatted attribute strings.
     */
    private List<String> getAttributes(String group) {
        List<String> attributes = new ArrayList<>();

        // If the attribute group is null, return an empty list
        if (group == null) return attributes;

        // Define a pattern for matching attributes in the attribute group
        Pattern attributesPattern = Pattern.compile("\\s+(\\w*)\\s*=\\s*(\"[\\w.]*\"|'[\\w.]*')");
        Matcher matcher = attributesPattern.matcher(group);

        // Iterate through the matches and add formatted attributes to the list
        while (matcher.find()) {
            attributes.add(String.format("%s = %s",
                    matcher.group(1),
                    matcher.group(2).replaceAll("'", "\"")));
        }
        return attributes;
    }
}
