import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author alpana.chaphalkar@sky.de
 */
public class Stubs {

    /**
     * Gets queues configuration as String
     * @return queues.xml configuration in String
     */
    private String getQueueXMLConfiguration() {
        String queueXMLConfiguration = "";

        try {
            String stubsConfFilePath = Utils.getResourceFile("queues.xml").getAbsolutePath();
            queueXMLConfiguration = new String(Files.readAllBytes(Paths.get(stubsConfFilePath)));
        } catch (Exception var2) {
        }

        return queueXMLConfiguration;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("####################################################################################");
        System.out.println("                FOS to Microservice Stub stated: Sky-New CRM Project");
        System.out.println("####################################################################################");
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        System.out.println("                                                     Time Stamp: " + dateFormat.format(date));
        System.out.println("####################################################################################");
        Stubs stubs = new Stubs();
        String queuesXml = stubs.getQueueXMLConfiguration();
        Thread myThread;
        Document queueXMLDoc = null;
        String queueName = "";
        String enabled = "";
        String completed = "";
        String success = "";
        String cancelled = "";

        try {
            queueXMLDoc = Utils.getXmlAsDOMDocument(queuesXml);
        } catch (Exception e) {
            System.out.println("Exception Raised while parsing queue XML Configuration file...");
            e.printStackTrace();
        }

        queueXMLDoc.getDocumentElement().normalize();
        System.out.println("XML Queue Configuration - root element: " + queueXMLDoc.getDocumentElement().getNodeName());
        NodeList nlist = queueXMLDoc.getElementsByTagName("queue");

        for (int i = 0; i < nlist.getLength(); i++) {
            Node child = nlist.item(i);
            queueName = child.getChildNodes().item(1).getTextContent();
            enabled = child.getChildNodes().item(3).getTextContent();
            completed = child.getChildNodes().item(5).getTextContent();
            success = child.getChildNodes().item(7).getTextContent();
            cancelled = child.getChildNodes().item(9).getTextContent();
            System.out.println("Queue Name: . . " + queueName);
            System.out.println("Enable: . . . . " + enabled);
            System.out.println("Completed:. . . " + completed);
            System.out.println("Successs: . . . " + success);
            System.out.println("Cancelled:. . . " + cancelled);

            if (enabled.equals("true")) {
                EmsConsumer emsConsumer = new EmsConsumer(queueName,completed, success, cancelled);
                myThread = new Thread(emsConsumer);
                myThread.start();
            }
            System.out.println("   - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        }

    }

}
