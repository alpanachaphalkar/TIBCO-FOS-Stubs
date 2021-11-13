import com.tibco.tibjms.TibjmsConnectionFactory;
import org.w3c.dom.Document;

import javax.jms.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Hashtable;

/**
 * @author alpana.chaphalkar@sky.de
 */
public class EmsProducerTest {

    private String serverUrl;
    private String userName;
    private String password;
    private String testRequestXml;
    private String testRequestQueue;

    /**
     * Initializes TIBCO EMS Server configuration along with test queue name and test request.
     * @param _serverUrl as String which is EMS Server URL
     * @param _userName as String which is username for authenticating EMS Server
     * @param _password as String which is password for authenticating EMS Server
     * @param _requestXml as String, name of request xml file.
     * @param _requestQueue as Strung, name of queue to which request will be sent.
     */
    EmsProducerTest(String _serverUrl, String _userName, String _password, String _requestXml, String _requestQueue){
        this.serverUrl = _serverUrl;
        this.userName = _userName;
        this.password = _password;
        this.testRequestXml = _requestXml;
        this.testRequestQueue = _requestQueue;
    }

    public void run() throws JMSException, URISyntaxException {
        System.out.println("########################## PUBLISHER  ##########################");
        System.out.println("Test Producing message to destination (queue): \n   " + this.testRequestQueue + "\n");
        String trustedCertFilePath = Utils.getResourceFile("cert.pem").getAbsolutePath();
        Hashtable<String, Object> environment = new Hashtable<>();
        environment.put("com.tibco.tibjms.ssl.trusted_certs", trustedCertFilePath);
        environment.put("com.tibco.tibjms.ssl.expected_hostname", this.serverUrl);
        environment.put("com.tibco.tibjms.ssl.enable_verify_hostname", Boolean.FALSE);
        environment.put("com.tibco.tibjms.ssl.enable_verify_host", Boolean.FALSE);
        environment.put("com.tibco.tibjms.ssl.password", "changeit");
        TibjmsConnectionFactory tibCF = new TibjmsConnectionFactory(this.serverUrl, null, environment);
        Connection connection = tibCF.createConnection(this.userName, this.password);
        Session session = connection.createSession(1);
        Queue queue = session.createQueue(this.testRequestQueue);
        MessageProducer producer = session.createProducer(queue);
        TextMessage textMessage = session.createTextMessage();
        textMessage.setText(readXml());
        producer.send(textMessage);
        System.out.println("\n   Message Produced and sent to Queue!. . . . . . . . . . . . . . . . . . . . . .");
        connection.close();
        System.out.println("   Connection Closed, exiting EMS Producer Test . . . . . .");
    }

    /**
     * Reads the test request xml file and return that as string.
     * @return xmlPayload as String
     */
    private String readXml() {
        String xmlPayload = "Default Text";
        System.out.println("XML Payload file, to simulate OM:\n   File:              " + this.testRequestXml);

        try {
            File testRequestXmlFile = Utils.getResourceFile(this.testRequestXml);
            Document xmlDoc = Utils.getXmlAsDOMDocument(testRequestXmlFile);
            xmlDoc.getDocumentElement().normalize();
            System.out.println("   Root element:      " + xmlDoc.getDocumentElement().getNodeName());
            System.out.println("   Msg XML Encoding:  " + xmlDoc.getXmlEncoding());
            xmlPayload = getXmlAsString(xmlDoc);
            System.out.println("\nXML Payload from test:\n   " + xmlPayload);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlPayload;
    }

    /**
     * Reads the test request xml document and returns it as String
     * @param document as Document
     * @return document as String
     * @throws Exception from Transformer
     */
    private String getXmlAsString(Document document) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString().replaceAll("\n|\r", "");
    }

    public static void main(String[] args){
        ReadFosConfig fosConfig = ReadFosConfig.getInstance();
        EmsProducerTest emsProducerTest = new EmsProducerTest(
                fosConfig.getEmsServer(),
                fosConfig.getUser(), fosConfig.getPassword(),
                fosConfig.getTestRequestXML(), fosConfig.getTestRequestQueue()
        );
        System.out.println("################################################################");
        System.out.println("Producer running from main method . . . ");
        try {
            emsProducerTest.run();
        } catch (JMSException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
