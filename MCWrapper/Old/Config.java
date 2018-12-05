package nl.ivoka.API;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config_old {

    public Document doc;
    public Node rootNode;

    public Config_old() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
    }
    public Config_old(String filePath)
            throws ParserConfigurationException, SAXException, IOException{
        loadConfig(filePath);
        rootNode = doc.getElementsByTagName("root").item(0);
    }

    public void createNewConfig() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
        rootNode = doc.createElement("root");
        doc.appendChild(rootNode);
    }

    public void saveConfig(String filePath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(String filePath) throws ParserConfigurationException, SAXException, IOException{
        File xmlFile = new File(filePath);
        if (xmlFile.exists() && !xmlFile.isDirectory()) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(filePath);
        } else {
            createNewConfig();
            saveConfig(filePath);
        }
    }

    public String getValue(String key) {
        if (doc.getElementsByTagName(key).item(0) != null)
            return doc.getElementsByTagName(key).item(0).getNodeValue();
        else
            return null;
    }
    public String[] getValues() {
        List<String> values = new ArrayList<>();
        NodeList nodeList = rootNode.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (((Node)nodeList.item(i)).getNodeType() == Node.TEXT_NODE)
                values.add(((Node)nodeList.item(i)).getNodeValue());
        }
        return values.toArray(new String[0]);
    }

    public void setValue(String key, String value) {
        if (doc.getElementsByTagName(key).item(0) == null)
            rootNode.appendChild(doc.createElement(key));
        doc.getElementsByTagName(key).item(0).setNodeValue(value);
    }
}
