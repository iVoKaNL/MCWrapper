package nl.ivoka.API;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Config {
    Document document;
    Element rootNode;
    Element currentNode;
    File file;

    /**
     * Initialize Config
     */
    public Config() { createConfig(); }

    /**
     * Initialize Config
     * @param file  String - path to file to be loaded
     * @throws DocumentException
     */
    public Config(String file) throws DocumentException, IOException { this.file = new File(file); loadConfig(new File(file)); saveConfig(); }

    /**
     * Initialize Config
     * @param file  File - to be loaded
     * @throws DocumentException
     */
    public Config(File file) throws DocumentException, IOException { this.file = file; loadConfig(file); saveConfig(); }

    /**
     * Create a new XMLFile (without writing to disk)
     * and add root element
     */
    public void createConfig() {
        document = DocumentHelper.createDocument();

        rootNode = document.addElement("root");
        currentNode = rootNode;
    }

    /**
     * Save the XMLFile to already file
     * (file is initialized when loading config)
     * @throws IOException
     */
    public void saveConfig() throws IOException { saveConfig(file); }

    /**
     * Save XMLFile to file
     * @param file  String - path to file to be written to
     * @throws IOException
     */
    public void saveConfig(String file) throws IOException { saveConfig(new File(file)); }

    /**
     * Save XMLFile to file
     * @param file  File - to be written to
     * @throws IOException
     */
    public void saveConfig(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        document.write(writer);
        writer.close();
    }

    /**
     * Load XMLFile to document
     * @param file  File - to be loaded
     * @throws DocumentException
     */
    public void loadConfig(File file) throws DocumentException {
        if (file.exists()) {
            SAXReader reader = new SAXReader();
            document = reader.read(file);

            rootNode = document.getRootElement();
            currentNode = rootNode;
        } else
            createConfig();

        this.file = file;
    }

    Element[] getElements(String key) {
        List<Element> e = new ArrayList<>();

        for (Iterator<Element> element = currentNode.elementIterator(key); element.hasNext();) {
            e.add(element.next());
        }
        return e.toArray(new Element[0]);
    }

    /**
     * Get attribute of key
     * @param key       String - element key
     * @param attrKey   String - attribute key
     */
    public String getAttribute(String key, String attrKey) { return getAttribute(key, attrKey, 0); }

    /**
     * Get attribute of key
     * @param key       String  - element key
     * @param attrKey   String  - attribute key
     * @param index     Integer - Which item in array to take
     */
    public String getAttribute(String key, String attrKey, Integer index) {
        if (currentNode.elementIterator(key).hasNext()) {
            return getElements(key)[index].attributeValue(attrKey);
        }
        return null;
    }

    /**
     * Get the value where elements key = key
     * @param key   String - element key
     * @return      String - value of element(key)
     */
    public String getValue(String key) { return getValue(key, 0); }

    /**
     * Get the value where elements key = key and checks for index
     * @param key   String  - element key
     * @param index Integer - Which item in array will be taken
     * @return      String  - value of element(key) where index
     */
    public String getValue(String key, Integer index) {
        if (currentNode.elementIterator(key).hasNext())
            return getElements(key)[index].getText();
        else
            return null;
    }

    /**
     * Get the value of a child element
     * @param parentKey String - parent element key
     * @param childKey  String - child element key
     * @return          String - value of child element
     */
    public String getChildValue(String parentKey, String childKey) { return getChildValue(parentKey, childKey, 0, 0); }

    /**
     * Get the value of a child element where indexes match
     * @param parentKey     String  - parent element key
     * @param childKey      String  - child element key
     * @param parentIndex   Integer - parent index
     * @param childIndex    Integer - child index
     * @return              String  - value of child element where indexes match
     */
    public String getChildValue(String parentKey, String childKey, Integer parentIndex, Integer childIndex) {
        String value = null;

        if (currentNode.elementIterator(parentKey).hasNext()) {
            currentNode = getElements(parentKey)[parentIndex];
            if (currentNode.elementIterator(childKey).hasNext())
                value = getElements(childKey)[childIndex].getText();
        }

        currentNode = rootNode;
        return value;
    }

    /**
     * Get all values
     * @param key   String      - element key
     * @return      String[]    - Returns a String array containing all values where conditions met
     */
    public String[] getValues(String key) {
        List<String> s = new ArrayList<>();

        for (Iterator<Element> element = currentNode.elementIterator(key); element.hasNext();) {
            s.add(element.next().getText());
        }
        return s.toArray(new String[0]);
    }

    /**
     * Get all values
     * @return String[] - Returns a String array containing all values
     */
    public String[] getValues() {
        List<String> s = new ArrayList<>();

        for (Iterator<Element> element = currentNode.elementIterator(); element.hasNext();) {
            s.add(element.next().getText());
        }
        return s.toArray(new String[0]);
    }

    /**
     * Set attributes of key
     * @param key           String              - element key
     * @param attributes    Map<String, String> - attribute key, attribute value
     */
    public void setAttributes(String key, Map<String, String> attributes) { setAttributes(key, attributes, 0); }

    /**
     * Set attributes of key
     * @param key           String              - element key
     * @param attributes    Map<String, String> - attribute key, attribute value
     * @param index         Integer             - Which item in array will be taken
     */
    public void setAttributes(String key, Map<String, String> attributes, Integer index) {
        attributes.forEach((x, y) -> {
            setAttribute(key, x, y, index);
        });
    }

    /**
     * Set attribute of key
     * @param key       String - element key
     * @param attrKey   String - attribute key
     * @param attrValue String - attribute value
     */
    public void setAttribute(String key, String attrKey, String attrValue) { setAttribute(key, attrKey, attrValue, 0); }

    /**
     * Set attribute of key
     * @param key       String  - element key
     * @param attrKey   String  - attribute key
     * @param attrValue String  - attribute value
     * @param index     Integer - Which item in array to take
     */
    public void setAttribute(String key, String attrKey, String attrValue, Integer index) {
        if (currentNode.elementIterator(key).hasNext()) {
            getElements(key)[index].addAttribute(attrKey, attrValue);
        }
    }

    /**
     * Set Values
     * @param values    Map<String, String>     - element key, element value
     *                  OR
     *                  Map<String, XMLValues>  - element key, (element value, attribute key, attribute value)
     */
    public void setValues(Map<?, ?> values) { setValues(values, 0); }

    /**
     * Set Values
     * @param values    Map<String, String>     - element key, element value
     *                  OR
     *                  Map<String, XMLValues>  - element key, (element value, attribute key, attribute value)
     * @param index     Integer                 - Which item in array to take
     */
    public void setValues(Map<?, ?> values, Integer index) {
        values.forEach((x, y) -> {
            if (x instanceof String) {
                if (y instanceof XMLValues) {
                    XMLValues xmlValues = (XMLValues)y;

                    setValue((String)x, xmlValues.value, index);
                    setAttribute((String)x, xmlValues.attrKey, xmlValues.attrValue, index);
                } else
                    setValue((String)x, (String)y, index);
            }
        });

    }

    /**
     * Set Value
     * @param key   String - element key
     * @param value String - element value
     */
    public void setValue(String key, String value) { setValue(key, value, 0); }

    /**
     * Set Value
     * @param key   String  - element key
     * @param value String  - element value
     * @param index Integer - Which item in array to take
     */
    public void setValue(String key, String value, Integer index) {
        if (currentNode.elementIterator(key).hasNext())
            getElements(key)[index].setText(value);
        else
            currentNode.addElement(key)
                    .addText(value);
    }

    /**
     * Set the value of a child element
     * @param parentKey String - parent element key
     * @param childKey  String - child element key
     * @param value     String - child element value
     */
    public void setChildValue(String parentKey, String childKey, String value) { setChildValue(parentKey, childKey, value, 0, 0); }

    /**
     * Set the value of a child element where indexes match
     * @param parentKey     String  - parent element key
     * @param childKey      String  - child element key
     * @param value         String  - child element value
     * @param parentIndex   Integer - parent index
     * @param childIndex    Integer - child index
     */
    public void setChildValue(String parentKey, String childKey, String value, Integer parentIndex, Integer childIndex) {
        if (currentNode.elementIterator(parentKey).hasNext())
            currentNode = getElements(parentKey)[parentIndex];
        else
            currentNode = currentNode.addElement(parentKey);

        if (currentNode.elementIterator(childKey).hasNext())
            getElements(childKey)[childIndex].setText(value);
        else
            currentNode.addElement(childKey)
                    .addText(value);

        currentNode = rootNode;
    }

    /**
     * Create an element
     * @param key   String - element key
     */
    public void createElement(String key) { currentNode.addElement(key); }

    /**
     * Create an element with value
     * @param key   String - element key
     * @param value String - element value
     */
    public void createElement(String key, String value) { currentNode.addElement(key).addText(value); }

    /**
     * Create a child element
     * @param parentKey String - parent element key
     * @param childKey  String - child element key
     */
    public void createChildElement(String parentKey, String childKey) { createChildElement(parentKey, childKey, 0); }

    /**
     * Create a child element
     * @param parentKey     String  - parent element key
     * @param childKey      String  - child element key
     * @param parentIndex   Integer - parent element index
     */
    public void createChildElement(String parentKey, String childKey, Integer parentIndex) {
        if (currentNode.elementIterator(parentKey).hasNext())
            currentNode = getElements(parentKey)[parentIndex];
        else
            currentNode = currentNode.addElement(parentKey);

        currentNode.addElement(childKey);

        currentNode = rootNode;
    }

    /**
     * Create a child element with value
     * @param parentKey String - parent element key
     * @param childKey  String - child element key
     * @param value     String - child element value
     */
    public void createChildElement(String parentKey, String childKey, String value) { createChildElement(parentKey, childKey, value, 0); }

    /**
     * Create a child element with value
     * @param parentKey     String  - parent element key
     * @param childKey      String  - child element key
     * @param value         String  - child element value
     * @param parentIndex   Integer - parent element index
     */
    public void createChildElement(String parentKey, String childKey, String value, Integer parentIndex) {
        if (currentNode.elementIterator(parentKey).hasNext())
            currentNode = getElements(parentKey)[parentIndex];
        else
            currentNode = currentNode.addElement(parentKey);

        currentNode.addElement(childKey).addText(value);

        currentNode = rootNode;
    }

    /**
     * Check if element exists
     * @param key   String  - element key
     * @return      boolean - true if element exists, false if element does not exist
     */
    public boolean elementExists(String key) {
        if (currentNode.elementIterator(key).hasNext())
            return true;
        else
            return false;
    }

    /**
     * Check if element exists and is not empty
     * @param key   String  - element key
     * @return      boolean - true if element exists and is not empty, false if element does not exists or if element is empty
     */
    public boolean elementExistsAndNotEmpty(String key) { return elementExistsAndNotEmpty(key, 0); }

    /**
     * Check if element exists and is not empty
     * @param key   String  - element key
     * @param index Integer - element index
     * @return      boolean - true if element exists and is not empty, false if element does not exists or if element is empty
     */
    public boolean elementExistsAndNotEmpty(String key, Integer index) {
        if (currentNode.elementIterator(key).hasNext() && !getElements(key)[index].getText().isEmpty())
            return true;
        else
            return false;
    }

    public class XMLValues {
        public String value, attrKey, attrValue;

        /**
         * This is used when calling setValues(Map<String, XMLValues>);
         * @param value     String - element value
         * @param attrKey   String - attribute key
         * @param attrValue String - attribute value
         */
        public XMLValues(String value, String attrKey, String attrValue) {
            this.value = value;
            this.attrKey = attrKey;
            this.attrValue = attrValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof XMLValues))
                return false;
            XMLValues ref = (XMLValues) obj;
            return this.value.equals(ref.value) &&
                    this.attrKey.equals(ref.attrKey) &&
                    this.attrValue.equals(ref.attrValue);
        }

        @Override
        public int hashCode() {
            return value.hashCode() ^
                    attrKey.hashCode() ^
                    attrValue.hashCode();
        }
    }
}