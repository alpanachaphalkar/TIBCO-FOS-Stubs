import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author alpana.chaphalkar@sky.de
 */
public class ReadFosConfig {

    private String stubsConfig;
    private Properties properties;
    private static ReadFosConfig fosConfig = new ReadFosConfig();

    /**
     * Default constructor
     */
    ReadFosConfig(){
        readStubsConfig();
    }

    /**
     * Get the instance of class ReadFosConfig
     * @return FOS Stubs Config
     */
    public static ReadFosConfig getInstance() {
        return fosConfig;
    }

    /**
     * Reads the stubs configuration and load them as properties.
     */
    private void readStubsConfig(){
        this.stubsConfig = "stubs.properties";
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        InputStream stubsResourceAsStream = contextClassLoader.getResourceAsStream(this.stubsConfig);
        if (stubsResourceAsStream != null){
            properties = new Properties();
            try {
                properties.load(stubsResourceAsStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads stubs conf as properties and gets EMS server connection url.
     * @return EMS server connection url.
     */
    public String getEmsServer() {
        String TIBCO_EMS_SERVER_HOST = System.getenv("TIBCO_EMS_SERVER_HOST");
        String TIBCO_EMS_SERVER_PORT = System.getenv("TIBCO_EMS_SERVER_PORT");
        if (TIBCO_EMS_SERVER_HOST.isEmpty() || TIBCO_EMS_SERVER_PORT.isEmpty()) {
            return null;
        }
        return  "ssl://" + TIBCO_EMS_SERVER_HOST + ":" + TIBCO_EMS_SERVER_PORT;
    }

    /**
     * Reads stubs conf as properties and gets EMS server user for authentication.
     * @return EMS server user.
     */
    public String getUser(){
        return System.getenv("TIBCO_EMS_USERNAME").isEmpty() ? null : System.getenv("TIBCO_EMS_USERNAME");
    }

    /**
     * Reads stubs conf as properties and gets EMS server user password authentication.
     * @return EMS server password.
     */
    public String getPassword(){
        return System.getenv("TIBCO_EMS_PASSWORD").isEmpty() ? null : System.getenv("TIBCO_EMS_PASSWORD");
    }

    /*
     * *********************************************************************************************
     * Response templates, XMLs with replaceable holders...  i.e. #ORDER_ID#, #ORDER_REF#, #PLAN_ID#
     * *********************************************************************************************
     */

    /**
     * Reads stubs conf as properties and gets response template to Execution Request XML
     * @return response template for execution request
     */
    public String getExecuteResponseXML(){
        return properties != null ? properties.getProperty("planItemExecuteResponseXML") : null;
    }

    /**
     * Reads stubs conf as properties and gets response template to Activation Request XML
     * @return activation response template
     */
    public String getActivationResponseXML(){
        return properties != null ? properties.getProperty("planItemActivateResponseXML") : null ;
    }

    /**
     * Reads stubs conf as properties and gets response template to Suspension Request XML
     * @return suspension response template
     */
    public String getSuspensionResponseXML() {
        return properties != null ? properties.getProperty("planItemSuspendResponseXML") : null ;
    }

    /*
     * *********************************************************************************************
     * Response queues to all types of requests: Execution, Activation and Suspension
     * *********************************************************************************************
     */

    /**
     * Reads stubs conf as properties and gets queue for execution request response
     * @return Execution Request Response Queue
     */
    public String getExecutionRequestResponseQueue(){
        return properties != null ? properties.getProperty("executionRequestResponseQueue") : null ;
    }

    /**
     * Reads stubs conf as properties and gets queue for activation request response
     * @return Activation request response
     */
    public String getActivationRequestResponseQueue() {
        return properties != null ? properties.getProperty("activationRequestResponseQueue") : null ;
    }

    /**
     * Reads stubs conf as properties and gets queue for suspension request response
     * @return Suspension request response
     */
    public String getSuspensionRequestResponseQueue() {
        return properties != null ? properties.getProperty("suspensionRequestResponseQueue") : null ;
    }

    /*
     * *********************************************************************************************
     * Test ONLY Section. Only used by EmsProducerTest.java Application
     * *********************************************************************************************
     */

    /**
     * OM XML Payload copy (This will normally come from OM) used to test this application
     * @return OM XML Payload copy
     */
    public String getTestRequestXML(){
        return properties != null ? properties.getProperty("testRequest") : null ;
    }

    /**
     *  Queue to configure where to send a message to. (This will normally come from OM)  used to test this application.
     * @return Queue for sending request
     */
    public String getTestRequestQueue(){
        return properties != null ? properties.getProperty("testQueue") : null ;
    }
}
