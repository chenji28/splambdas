package stowplex.lambda.poster;

import com.amazonaws.geo.model.GeoPoint;
import com.amazonaws.geo.model.PutPointRequest;
import com.amazonaws.geo.model.PutPointResult;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import stowplex.lambda.*;
import stowplex.lambda.attributekeys.AttributeKeys;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

/**
 * Created by jcchn on 2/25/17.
 */

/* Lambda test
{
  "path": "/test/storages/post",
  "httpMethod": "POST",
  "queryStringParameters": {
      "sid":"center-SID",
      "cid":"center",
      "latitude":"47.6101360",
      "longitude":"-122.3420570",
      "type":"storage",
      "price":"100",
      "title":"this is a test storage",
      "rating":"80",
      "displayPictureId":"displayPictureID",
      "storageDetail":{
        "description": "this is the description of the storage"
      },
      "available":"true"
  }
}
{
  "path": "/test/storages/post",
  "httpMethod": "POST",
  "queryStringParameters": {
      "sid":"neigh2-SID",
      "cid":"neigh2",
      "latitude":"47.6141360",
      "longitude":"-122.3390570",
      "type":"storage",
      "price":"100",
      "title":"this is a test storage",
      "rating":"80",
      "displayPictureId":"displayPictureID",
      "storageDetail":{
        "description": "this is the description of the storage"
      },
      "available":"true"
  }
}
//	    center: {lat: 47.6101360, lng: -122.3420570},
//	    neigh1: {lat: 47.6091360, lng: -122.3400570},
//	    neigh2: {lat: 47.6141360, lng: -122.3390570},
//	    neigh3: {lat: 47.6121360, lng: -122.3440870},
*/
public class Poster implements StowplexRequestHandler {

    private static final String LOG_LABLE = "Poster";

    public static final String PATH = "/test/storages/post";
    private final TableInteractor tableInteractor;
    private final ObjectMapper mapper = new ObjectMapper();

    public Poster(TableInteractor tableInteractor){
        this.tableInteractor = tableInteractor;
    }
    @Override
    public String handleRequest(JSONObject event, Context context) throws InvalidPropertiesFormatException {
        LambdaLogger lambdaLogger = context.getLogger();
        Logger logger = new Logger(lambdaLogger, LOG_LABLE);

        if (event.get("queryStringParameters") == null){
            throw new InvalidPropertiesFormatException("queryStringParameters does not exist in event, event is:"+event.toJSONString());
        }

        if (!event.get("httpMethod").toString().equalsIgnoreCase("POST") ) {
            throw new InvalidPropertiesFormatException(PATH +
                    " does not support this httpMethod, httpMethod is:"+
                    event.get("httpMethod").toString());
        }

        String storageInString = event.get("queryStringParameters").toString();
        try {
            AttributeKeys request = mapper.readValue(storageInString,AttributeKeys.class);
            handlePost(request);
            return NoValueResponseFactory.getSuccessResponseInString();
        } catch (IOException e) {
            logger.logError("handleRequest error: "+e.toString());
            return NoValueResponseFactory.getFailureResponseInString();
        }
    }

    private void handlePost(AttributeKeys request) throws JsonProcessingException {
        GeoPoint geoPoint = new GeoPoint(request.getLatitude(), request.getLongitude());

        AttributeValue rangeKeyValue = new AttributeValue().withS(request.getSid());
        PutPointRequest putPointRequest = new PutPointRequest(geoPoint, rangeKeyValue);

        AttributeValue cid = new AttributeValue().withS(request.getCid());
        AttributeValue type = new AttributeValue().withS(request.getType());
        AttributeValue price = new AttributeValue().withN(String.valueOf(request.getPrice()));
        AttributeValue title = new AttributeValue().withS(request.getTitle());
        AttributeValue rating = new AttributeValue().withN(String.valueOf(request.getRating()));
        AttributeValue displayPictureId = new AttributeValue().withS(request.getDisplayPictureId());

        String detailsInString = mapper.writeValueAsString(request.getStorageDetail());
        AttributeValue details = new AttributeValue().withS(detailsInString);

        AttributeValue available = new AttributeValue().withBOOL(request.isAvailable());

        PutItemRequest putItemRequest = putPointRequest.getPutItemRequest();
        putItemRequest.addItemEntry("cid", cid);
        putItemRequest.addItemEntry("type", type);
        putItemRequest.addItemEntry("price", price);
        putItemRequest.addItemEntry("title", title);
        putItemRequest.addItemEntry("rating", rating);
        putItemRequest.addItemEntry("displayPictureId", displayPictureId);
        putItemRequest.addItemEntry("details", details);
        putItemRequest.addItemEntry("available", available);

        PutPointResult putPointResult = tableInteractor.getGeoDataManager().putPoint(putPointRequest);
    }
}
