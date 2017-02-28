package stowplex.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import org.json.simple.JSONObject;

import java.util.InvalidPropertiesFormatException;

/**
 * Created by jcchn on 2/25/17.
 */
public interface StowplexRequestHandler {
    String handleRequest(JSONObject event, Context context) throws InvalidPropertiesFormatException;
}
