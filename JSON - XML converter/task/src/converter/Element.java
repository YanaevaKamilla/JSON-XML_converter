package converter;

import java.util.ArrayList;
import java.util.List;

/**
 * The Element class represents a hierarchical structure used in a custom format.
 * Each element has a name, path, value, attributes, and children.
 */
public class Element {
    private final String name;
    private final String path;
    private String value;
    private final List<String> attributes;
    private final List<Element> children;
    private boolean isArray;

    /**
     * Constructs an Element with the specified name, value, and isArray flag.
     *
     * @param root    the root element, can be null for top-level elements
     * @param name    the name of the element
     * @param value   the value of the element
     * @param isArray a boolean indicating whether the element is an array
     */
    public Element(Element root, String name, String value, boolean isArray) {
        // Ensure the name follows a specific pattern
        this.name = name.matches("element\\(\\d+\\)") ? "element" : name;
        this.path = (root == null || root.getPath().isEmpty()) ? name : root.getPath() + ", " + name;
        this.value = formatValue(value);
        this.attributes = new ArrayList<>();
        this.children = new ArrayList<>();
        this.isArray = isArray;
    }

    /**
     * Constructs an Element with the specified name and isArray flag, without a value.
     *
     * @param root    the root element, can be null for top-level elements
     * @param name    the name of the element
     * @param isArray a boolean indicating whether the element is an array
     */
    public Element(Element root, String name, Boolean isArray) {
        this(root, name, "", isArray);
    }

    /**
     * Constructs a leaf Element with the specified name and no parent.
     *
     * @param name the name of the element
     */
    public Element(String name) {
        this(null, name, null, false);
    }

    /**
     * Checks if the element is an array.
     *
     * @return true if the element is an array, false otherwise
     */
    public boolean isArray() {
        return isArray;
    }

    /**
     * Gets the name of the element.
     *
     * @return the name of the element
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the element.
     *
     * @param value the new value of the element
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Adds an attribute to the element.
     *
     * @param attribute the attribute to be added
     */
    public void addAttribute(String attribute) {
        this.attributes.add(attribute);
    }

    /**
     * Adds a child element to the current element.
     *
     * @param child the child element to be added
     */
    public void addChild(Element child) {
        this.children.add(child);
        this.isArray = this.children.size() > 1
                && this.children.stream().allMatch(c -> c.getName().equals(child.getName()));
    }

    /**
     * Gets the path of the element within the hierarchy.
     *
     * @return the path of the element
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the value of the element.
     *
     * @return the value of the element
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the list of attributes associated with the element.
     *
     * @return the list of attributes
     */
    public List<String> getAttributes() {
        return attributes;
    }

    /**
     * Gets the list of child elements.
     *
     * @return the list of child elements
     */
    public List<Element> getChildren() {
        return children;
    }

    /**
     * Checks if the element is a leaf (has no children).
     *
     * @return true if the element is a leaf, false otherwise
     */
    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    /**
     * Generates a string representation of the element and its hierarchy.
     *
     * @return the string representation of the element
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!this.path.isEmpty()) {
            stringBuilder.append("Element:\n");
            stringBuilder.append(String.format("path = %s%n", this.path));
            if (isLeaf()) {
                stringBuilder.append(String.format("value = %s%n", this.value));
            }
            if (!this.attributes.isEmpty()) {
                stringBuilder.append("attributes:\n");
                for (String attribute : attributes) {
                    stringBuilder.append(attribute).append("\n");
                }
            }
            stringBuilder.append("\n");
        }
        for (Element element : children) {
            stringBuilder.append(element.toString());
        }
        return stringBuilder.toString();
    }

    /**
     * Formats the value of the element, adding quotes if necessary.
     *
     * @param value the value to be formatted
     * @return the formatted value
     */
    private String formatValue(String value) {
        if (value != null) {
            value = value.matches("\".*\"|null") ? value : String.format("\"%s\"", value);
        }
        return value;
    }
}