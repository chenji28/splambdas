package stowplex.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;


/**
 * Created by jcchn on 2/25/17.
 */
public class StorageTableProxy implements RequestStreamHandler {
    private static final String LOG_LABLE = "StorageTableProxy";
    private JSONParser parser = new JSONParser();
    private Dispatcher dispatcher = new Dispatcher();

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        LambdaLogger lambdaLogger = context.getLogger();
        Logger logger = new Logger(lambdaLogger,LOG_LABLE);
        logger.logInfo("Loading Java Lambda handler of ProxyWithStream");

        JSONObject responseJson = new JSONObject();
        String responseCode = "200";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            JSONObject event = (JSONObject)parser.parse(reader);

            String responseBodyInString = dispatcher.dispatch(event,context);

            JSONObject headerJson = new JSONObject();
            headerJson.put("x-custom-response-header", "my custom response header value");

            responseJson.put("statusCode", responseCode);
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBodyInString);

        } catch(ParseException pex) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", pex);
        }

        logger.logInfo(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toJSONString());
        writer.close();
    }

}
