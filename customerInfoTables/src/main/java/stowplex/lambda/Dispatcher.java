package stowplex.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.json.simple.JSONObject;
import stowplex.lambda.retriever.Retriever;

import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;

/**
 * Created by jcchn on 2/25/17.
 */
public class Dispatcher {
    private static final String LOG_LABLE = "Dispatcher";
    private static final Map<String, StowplexRequestHandler> pathHandlerMap;
    private static final TableInteractor tableInteractor;

    static
    {
        tableInteractor = new TableInteractor();
        pathHandlerMap = new HashMap<>();
        pathHandlerMap.put(Retriever.PATH, new Retriever(tableInteractor));
    }

    public String dispatch(JSONObject event, Context context) throws InvalidPropertiesFormatException {
        LambdaLogger lambdaLogger = context.getLogger();
        Logger logger = new Logger(lambdaLogger,LOG_LABLE);

        if (event.get("path") == null){
            logger.logError("path does not exist in event, event is:"+event.toJSONString());
            throw new InvalidPropertiesFormatException("path does not exist in event, event is:"+event.toJSONString());
        }

        String path = (String)event.get("path");
        if (pathHandlerMap.containsKey(path)){
            return pathHandlerMap.get(path).handleRequest(event, context);
        }

        throw new InvalidPropertiesFormatException("path does not exist in event, event is:"+event.toJSONString());
    }
}
