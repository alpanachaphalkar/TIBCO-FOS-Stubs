import com.tibco.tibjms.TibjmsConnectionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Hashtable;

/**
 * @author alpana.chaphalkar@sky.de
 */
public class EmsConsumer implements Runnable{

    private String serverUrl;
    private String userName;
    private String password;

    private String queueName;
    private String completed;
    private String success;
    private String cancelled;

    private String responseFile;
    private String responseQueue;

    private  Connection connection;
    private Session session;
    private  MessageConsumer consumer;
    private static final int ACK_MODE = 1;

    private String orderId;
    private String orderRef;
    private  String planId;
    private String planItemId;

    /**
     * Initializes the EMS Server Config, Queue Name, Response Queue name, Response Body and Response file
     * @param _queueName as String
     * @param _completed as String
     * @param _success as String
     * @param _cancelled as String
     */
    EmsConsumer(String _queueName, String _completed, String _success, String _cancelled){
        ReadFosConfig fosConfig = ReadFosConfig.getInstance();

        this.serverUrl = fosConfig.getEmsServer();
        this.userName = fosConfig.getUser();
        this.password = fosConfig.getPassword();

        this.queueName = _queueName;
        this.responseFile = getResponseFile(fosConfig, _queueName);
        this.responseQueue = getResponseQueue(fosConfig, _queueName);
        this.completed = _completed;
        this.success = _success;
        this.cancelled = _cancelled;
    }

    /**
     * Gets response file based on queue name by reading stubs conf.
     * @param fosConfig as ReadFosConfig
     * @param _queueName as String
     * @return response file as String
     */
    private String getResponseFile(ReadFosConfig fosConfig, String _queueName){
        String _responseFile;
        switch (_queueName) {
            case "suspend.request":
                _responseFile = fosConfig.getSuspensionResponseXML();
                break;
            case "activation.request":
                _responseFile = fosConfig.getActivationResponseXML();
                break;
            default:
                _responseFile = fosConfig.getExecuteResponseXML();
        }
        return _responseFile;
    }

    /**
     * Gets response queue based on queue name by reading stubs conf.
     * @param fosConfig as ReadFosConfig
     * @param _queueName as String
     * @return response queue name as String
     */
    private String getResponseQueue(ReadFosConfig fosConfig, String _queueName){
        String _responseQueue;
        switch (_queueName) {
            case "suspend.request":
                _responseQueue = fosConfig.getSuspensionRequestResponseQueue();
                break;
            case "activation.request":
                _responseQueue = fosConfig.getActivationRequestResponseQueue();
                break;
            default:
                _responseQueue = fosConfig.getExecutionRequestResponseQueue();
        }
        return _responseQueue;
    }

    /**
     * Print the connection exception status
     * @param e as JMSException
     */
    public void onException(JMSException e){
        System.out.println("Connection Exception: " + e.getMessage());
    }

    @Override
    public void run() {

        try {
            String trustedCertFilePath = Utils.getResourceFile("cert.pem").getAbsolutePath();
            Hashtable<String, Object> environment = new Hashtable<>();
            environment.put("com.tibco.tibjms.ssl.trusted_certs", trustedCertFilePath);
            environment.put("com.tibco.tibjms.ssl.expected_hostname", this.serverUrl);
            environment.put("com.tibco.tibjms.ssl.enable_verify_hostname", Boolean.FALSE);
            environment.put("com.tibco.tibjms.ssl.enable_verify_host", Boolean.FALSE);
            environment.put("com.tibco.tibjms.ssl.password", "changeit");
            TibjmsConnectionFactory tibjmsCF = new TibjmsConnectionFactory(this.serverUrl, null, environment);
            this.connection = tibjmsCF.createConnection(this.userName, this.password);
            this.session = this.connection.createSession(ACK_MODE);
            Destination queue = this.session.createQueue(this.queueName);
            this.consumer = this.session.createConsumer(queue);
            this.connection.start();
        } catch (Exception e) {
            System.out.println("Exception Raised while establishing a connection with EMS server...");
            e.printStackTrace();
        }

        System.out.println("Connection Started! Reading messages.....[" + this.queueName + "]");

        while (true) {
            TextMessage textMessage = null;
            try {
                textMessage = (TextMessage) this.consumer.receive();
                System.out.println("   - - - - - - - - - - - Message read! - - - - - - - - - - - ");
                System.out.println("   Queue Name: [" + this.queueName + "]");
                String msgXmlString = textMessage.getText();
                Document receivedXmlDom = Utils.getXmlAsDOMDocument(msgXmlString);
                receivedXmlDom.getDocumentElement().normalize();
                System.out.println("   XML as it was received:\n \n" + msgXmlString + "\n \n");
                System.out.println("   #############################################   \n \n");
                System.out.println("   Root element: . . . . . . . " + receivedXmlDom.getDocumentElement().getNodeName());
                NodeList nodeList = receivedXmlDom.getFirstChild().getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node child = nodeList.item(i);
                    if (child.getNodeName().contentEquals("ns21:orderID")){
                        this.orderId = child.getTextContent();
                    }
                    if (child.getNodeName().contentEquals("ns21:orderRef")) {
                        this.orderRef = child.getTextContent();
                    }
                    if (child.getNodeName().contentEquals("ns21:planID")) {
                        this.planId = child.getTextContent();
                    }
                    if (child.getNodeName().contentEquals("ns21:planItem")) {
                        NodeList nodeList2 = child.getChildNodes();
                        Node child2 = nodeList2.item(1);
                        this.planItemId = child2.getTextContent();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("      Data-orderID      = " + this.orderId);
            System.out.println("      Data-orderRef     = " + this.orderRef);
            System.out.println("      Data-planID       = " + this.planId);
            System.out.println("      Data-planItemID   = " + this.planItemId);

            if (textMessage == null){
                try {
                    this.connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                return;
            }

            if (ACK_MODE == 2 || ACK_MODE == 23 || ACK_MODE == 24) {
                try {
                    textMessage.acknowledge();
                } catch (JMSException var14) {
                    var14.printStackTrace();
                }

                System.out.println("   - - - - - - - - - Message Acknowledged! - - - - - - - - - ");
            }

            String respMsgXmlString = ResponsePayload.mapper(this.orderId, this.orderRef,
                    this.planId, this.planItemId,
                    this.completed, this.success, this.cancelled,
                    this.responseFile);
            System.out.println("Response: \n" + respMsgXmlString);
            try {
                Queue queue = this.session.createQueue(this.responseQueue);
                MessageProducer producer = this.session.createProducer(null);
                TextMessage replyMessage = this.session.createTextMessage();
                replyMessage.setText(respMsgXmlString);
                producer.send(queue, replyMessage);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            System.out.println("   Message replied . . . . . . . . . . . . . . . . . . . . . .");
        }

    }

}
