package stowplex.lambda.retriever;

import com.amazonaws.geo.model.GeoPoint;
import com.amazonaws.geo.model.QueryRectangleRequest;
import com.amazonaws.geo.model.QueryRectangleResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import stowplex.lambda.Logger;
import stowplex.lambda.NoValueResponseFactory;
import stowplex.lambda.StowplexRequestHandler;
import stowplex.lambda.TableInteractor;
import stowplex.lambda.dao.StorageGeoLocationDAO;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

/**
 * Created by jcchn on 2/25/17.
 */

/* Lambda test
{
  "path": "/storages/get",
  "httpMethod": "GET",
  "queryStringParameters": {
      "minPoint": {
            "latitude":"47.6091360",
            "longitude":"-122.3440570"
        },
       "maxPoint": {
            "latitude":"47.6151360",
            "longitude":"-122.3360870"
      },
      "available":"true",
      "type":"storage"
  }
}

// which is equivalent to
{
  "path": "/storages/get",
  "httpMethod": "GET",
  "queryStringParameters": {
      "minPoint": "%7B%22latitude%22%3A%2247.6091360%22%2C%22longitude%22%3A%22-122.3440570%22%7D",
       "maxPoint": "%7B%22latitude%22%3A%2247.6151360%22%2C%22longitude%22%3A%22-122.3360870%22%7D",
      "available":"true",
      "type":"storage"
  }
}

//minPoint=%7B%22latitude%22%3A%2247.6091360%22%2C%22longitude%22%3A%22-122.3440570%22%7D&maxPoint=%7B%22latitude%22%3A%2247.6151360%22%2C%22longitude%22%3A%22-122.3360870%22%7D&available=true&type=storage

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

    private static final String LOG_LABLE = "Retriever";

    public static final String PATH = "/storages/get";
    private final TableInteractor tableInteractor;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Filterer filterer;

    public Retriever(TableInteractor tableInteractor){
        this.tableInteractor = tableInteractor;
        this.filterer = new Filterer(tableInteractor);
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

        //TODO: clean up this unnecessary marshall/unmarshall
        String queryInString = event.get("queryStringParameters").toString();
        try {
            RetrieverRequest request = mapper.readValue(queryInString,RetrieverRequest.class);
            List<StorageGeoLocationDAO> response = handleGet(request,logger);
            String responseInString = mapper.writeValueAsString(response);
            logger.logDebug("responseInString is:"+responseInString);
            return responseInString;
        } catch (IOException e) {
            logger.logError("handleRequest error: "+e.toString());
            return NoValueResponseFactory.getFailureResponseInString();
        }
    }

    private List<StorageGeoLocationDAO> handleGet(RetrieverRequest request, Logger logger) throws IOException {
        RetrieverRequestGeoPoint requestMinPoint = RetrieverRequestGeoPoint
                .builder()
                .encodedGeoPoint(request.getMinPoint())
                .build();
        RetrieverRequestGeoPoint requestMaxPoint = RetrieverRequestGeoPoint
                .builder()
                .encodedGeoPoint(request.getMaxPoint())
                .build();

        GeoPoint minPoint = new GeoPoint(requestMinPoint.getLatitude(),requestMinPoint.getLongitude());
        GeoPoint maxPoint = new GeoPoint(requestMaxPoint.getLatitude(),requestMaxPoint.getLongitude());
        QueryRectangleRequest queryRectangleRequest = new QueryRectangleRequest(minPoint,maxPoint);

        QueryRectangleResult queryRectangleResult = tableInteractor.getGeoDataManager().queryRectangle(queryRectangleRequest);
        List<StorageGeoLocationDAO> queryRectangleFilteredResult =
                filterer.getFilteredResult(queryRectangleResult,request, logger);

        printDebug(queryRectangleFilteredResult,logger);

        return queryRectangleFilteredResult;
    }

    private void printDebug(List<StorageGeoLocationDAO> queryRectangleFilteredResult, Logger logger){

        logger.logDebug("item count:"+queryRectangleFilteredResult.size());
        for(StorageGeoLocationDAO item : queryRectangleFilteredResult){
            logger.logDebug("item cid:"+
                    item.getCid()+
                    ", available:"+
                    item.isAvailable()+
                    ", latitude:"+
                    item.getStorageGeoJson().getCoordinates().get(0)+
                    ", longitude:"+
                    item.getStorageGeoJson().getCoordinates().get(1));
        }
    }
}
