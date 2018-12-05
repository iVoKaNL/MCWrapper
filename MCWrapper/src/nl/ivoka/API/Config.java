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
    File file;

    public Config() { createConfig(); }
    public Config(String file) throws DocumentException { loadConfig(new File(file)); }
    public Config(File file) throws DocumentException { loadConfig(file); }

    public void createConfig() {
        document = DocumentHelper.createDocument();
        rootNode = document.addElement("root");
    }

    public void saveConfig() throws IOException { saveConfig(file); }
    public void saveConfig(String file) throws IOException { saveConfig(new File(file)); }
    public void saveConfig(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        document.write(writer);
        writer.close();
    }

    public void loadConfig(File file) throws DocumentException {
        if (file.exists()) {
            SAXReader reader = new SAXReader();
            document = reader.read(file);
            rootNode = document.getRootElement();
        } else
            createConfig();

        this.file = file;
    }

    Element[] getElements(String key) {
        List<Element> e = new ArrayList<>();

        for (Iterator<Element> element = rootNode.elementIterator(key); element.hasNext();) {
            e.add(element.next());
        }
        return e.toArray(new Element[0]);
    }

    public String getValue(String key) {
        for (Iterator<Element> element = rootNode.elementIterator(key); element.hasNext();) {
            return element.next().getText();
        }
        return null;
    }
    public String[] getValues(String key) {
        List<String> s = new ArrayList<>();

        for (Iterator<Element> element = rootNode.elementIterator(key); element.hasNext();) {
            s.add(element.next().getText());
        }
        return s.toArray(new String[0]);
    }
    public String[] getValues() {
        List<String> s = new ArrayList<>();

        for (Iterator<Element> element = rootNode.elementIterator(); element.hasNext();) {
            s.add(element.next().getText());
        }
        return s.toArray(new String[0]);
    }

    public void setAttributes(String key, Map<String, String> attributes) { setAttributes(key, attributes, 0); }
    public void setAttributes(String key, Map<String, String> attributes, Integer index) {
        attributes.forEach((x, y) -> {
            setAttribute(key, x, y, index);
        });
    }
    public void setAttribute(String key, String attrKey, String attrValue) { setAttribute(key, attrKey, attrValue, 0); }
    public void setAttribute(String key, String attrKey, String attrValue, Integer index) {
        if (rootNode.elementIterator(key).hasNext()) {
            getElements(key)[index].addAttribute(attrKey, attrValue);
        }
    }

    public void setValues(Map<String, String> values) { setValues(values, 0); }
    public void setValues(Map<String, String> values, Integer index) {
        values.forEach((x, y) -> {
            setValue(x, y, index);
        });
    }
    public void setValue(String key, String value) { setValue(key, value, 0); }
    public void setValue(String key, String value, Integer index) {
        if (rootNode.elementIterator(key).hasNext()) {
            getElements(key)[index].setText(value);
        }
    }
}

// https://www.baeldung.com/java-xml-libraries
// https://dom4j.github.io