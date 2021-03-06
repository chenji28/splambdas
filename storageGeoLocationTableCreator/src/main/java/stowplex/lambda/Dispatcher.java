package stowplex.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import org.json.simple.JSONObject;
import stowplex.lambda.creator.TableCreator;
import stowplex.lambda.poster.Poster;
import stowplex.lambda.retriever.Retriever;

import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;

/**
 * Created by jcchn on 2/25/17.
 */
public class Dispatcher {
    private static final Map<String, StowplexRequestHandler> pathHandlerMap;
    private static final TableInteractor tableInteractor;

    static
    {
        tableInteractor = new TableInteractor();
        pathHandlerMap = new HashMap<>();
        pathHandlerMap.put(TableCreator.PATH, new TableCreator(tableInteractor));
        pathHandlerMap.put(Poster.PATH, new Poster(tableInteractor));
        pathHandlerMap.put(Retriever.PATH, new Retriever(tableInteractor));
    }

    public String dispatch(JSONObject event, Context context) throws InvalidPropertiesFormatException {
        if (event.get("path") == null){
            throw new InvalidPropertiesFormatException("path does not exist in event, event is:"+event.toJSONString());
        }

        String path = (String)event.get("path");
        if (pathHandlerMap.containsKey(path)){
            return pathHandlerMap.get(path).handleRequest(event, context);
        }

        throw new InvalidPropertiesFormatException("path does not exist in event, event is:"+event.toJSONString());
    }
}
