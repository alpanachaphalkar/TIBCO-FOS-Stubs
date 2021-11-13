import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * @author alpana.chaphalkar@sky.de
 */
public class Utils {

    /**
     * Gets actual file from resource placed in resources folder of the project.
     * @param resourceFileName as String
     * @return actual file.
     * @throws URISyntaxException
     */
    public static File getResourceFile(String resourceFileName) throws URISyntaxException {
        URL res = ResponsePayload.class.getClassLoader().getResource(resourceFileName);
        return Paths.get(res.toURI()).toFile();
    }

    /**
     * Parses the XML as String and builds the XML DOM Document.
     * @param xmlString as String
     * @return XML Dom Document
     * @throws Exception from DocumentBuilder and parsing.
     */
    public static Document getXmlAsDOMDocument(Object xmlString) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        if (xmlString instanceof String){
            return documentBuilder.parse(new InputSource(new StringReader((String) xmlString)));
        }
        if (xmlString instanceof File) {
            return documentBuilder.parse((File) xmlString);
        }
        return null;
    }
}
