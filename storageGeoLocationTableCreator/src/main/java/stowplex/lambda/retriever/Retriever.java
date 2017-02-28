package stowplex.lambda.retriever;

import com.amazonaws.geo.model.GeoPoint;
import com.amazonaws.geo.model.QueryRectangleRequest;
import com.amazonaws.geo.model.QueryRectangleResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import stowplex.lambda.Logger;
import stowplex.lambda.NoValueResponseFactory;
import stowplex.lambda.StowplexRequestHandler;
import stowplex.lambda.TableInteractor;
import stowplex.lambda.attributekeys.AttributeKeys;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

/**
 * Created by jcchn on 2/25/17.
 */

/* Lambda test
{
  "path": "/test/storages/get",
  "httpMethod": "GET",
  "queryStringParameters": {
      "cid":"CID",
      "minPoint": {
            "latitude":"47.6091360",
            "longitude":"-122.3440570"
        },
       "maxPoint": {
            "latitude":"47.6151360",
            "longitude":"-122.3360870"
      },
      "available":"false"
  }
}

// neigh1 == minPoint, neigh3 == maxPoint

//covers both center and neigh2
//	    neigh1: {lat: 47.6091360, lng: -122.3440570},
//      neigh3: {lat: 47.6151360, lng: -122.3360870}

//ONLY center point capture
//	    neigh1: {lat: 47.6091360, lng: -122.3440570},
//      neigh3: {lat: 47.6121360, lng: -122.3400870},

//THIS DOESNT WORK
//	    neigh1: {lat: 47.6091360, lng: -122.3400570}, <= this needs to be a minPoint
//	    neigh3: {lat: 47.6121360, lng: -122.3440870}, <= this needs to be a maxPoint
*/
public class Retriever implements StowplexRequestHandler {

    private static final String LOG_LABLE = "Retriver";

    public static final String PATH = "/test/storages/get";
    private final TableInteractor tableInteractor;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Filterer filterer = new Filterer();

    public Retriever(TableInteractor tableInteractor){
        this.tableInteractor = tableInteractor;
    }

    @Override
    public String handleRequest(JSONObject event, Context context) throws InvalidPropertiesFormatException {
        LambdaLogger lambdaLogger = context.getLogger();
        Logger logger = new Logger(lambdaLogger, LOG_LABLE);

        if (event.get("queryStringParameters") == null){
            throw new InvalidPropertiesFormatException("queryStringParameters does not exist in event, event is:"+
                    event.toJSONString());
        }

        if (!event.get("httpMethod").toString().equalsIgnoreCase("GET") ) {
            throw new InvalidPropertiesFormatException(PATH +
                    " does not support this httpMethod, httpMethod is:"+
                    event.get("httpMethod").toString());
        }

        String queryInString = event.get("queryStringParameters").toString();
        try {
            RetrieverRequest request = mapper.readValue(queryInString,RetrieverRequest.class);
            List<AttributeKeys> response = handleGet(request,logger);
            String responseInString = mapper.writeValueAsString(response);
            logger.logDebug("responseInString is:"+responseInString);
            return responseInString;
        } catch (IOException e) {
            logger.logError("handleRequest error: "+e.toString());
            return NoValueResponseFactory.getFailureResponseInString();
        }
    }

    private List<AttributeKeys> handleGet(RetrieverRequest request, Logger logger) throws JsonProcessingException {
        GeoPoint minPoint = new GeoPoint(request.getMinPoint().getLatitude(),request.getMinPoint().getLongitude());
        GeoPoint maxPoint = new GeoPoint(request.getMaxPoint().getLatitude(),request.getMaxPoint().getLongitude());
        QueryRectangleRequest queryRectangleRequest = new QueryRectangleRequest(minPoint,maxPoint);

        QueryRectangleResult queryRectangleResult = tableInteractor.getGeoDataManager().queryRectangle(queryRectangleRequest);
        List<AttributeKeys> queryRectangleFilteredResult =
                filterer.getFilteredResult(queryRectangleResult,request, logger);

        printDebug(queryRectangleFilteredResult,logger);

        return queryRectangleFilteredResult;
    }

    private void printDebug(List<AttributeKeys> queryRectangleFilteredResult, Logger logger){

        logger.logDebug("item count:"+queryRectangleFilteredResult.size());
        for(AttributeKeys item : queryRectangleFilteredResult){
            logger.logDebug("item cid:"+
                    item.getCid()+
                    ", available:"+
                    item.isAvailable()+
                    ", latitude:"+
                    item.getLatitude()+
                    ", longitude:"+
                    item.getLongitude());
        }
    }
}
