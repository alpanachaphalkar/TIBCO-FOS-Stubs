import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author alpana.chaphalkar@sky.de
 */
public class ResponsePayload {

    /**
     * Map and build the response template with actual values.
     * @param orderId as String
     * @param orderRef as String
     * @param planId as String
     * @param planItemId as String
     * @param completed as String
     * @param success as String
     * @param cancelled as String
     * @param responseFile as String
     * @return response as String
     */
    public static String mapper( String orderId, String orderRef,
                                 String planId, String planItemId,
                                 String completed, String success,
                                 String cancelled, String responseFile ) {

        System.out.println("\n########################### MAPPER ############################");
        System.out.println("Response File: " + responseFile);
        String R0, R1, R2, R3, R4, R5, R6, R7 = "";

        try {
            String responseFileXmlPath = Utils.getResourceFile(responseFile).getAbsolutePath();
            R0 = new String(Files.readAllBytes(Paths.get(responseFileXmlPath)));
            R1 = R0.replace("#ORDER_ID#"		, orderId);
            R2 = R1.replace("#ORDER_REF#"     , orderRef);
            R3 = R2.replace("#PLAN_ID#"       , planId);
            R4 = R3.replace("#PLAN_ITEM_ID#"  , planItemId);
            R5 = R4.replace("#COMPLETED#"     , completed);
            R6 = R5.replace("#SUCCESS#"	    , success);
            R7 = R6.replace("#CANCELLED#"     , cancelled);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return R7;
    }
}
