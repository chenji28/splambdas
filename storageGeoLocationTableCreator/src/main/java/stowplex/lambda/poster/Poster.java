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
import stowplex.lambda.Logger;
import stowplex.lambda.NoValueResponseFactory;
import stowplex.lambda.StowplexRequestHandler;
import stowplex.lambda.TableInteractor;
import stowplex.lambda.dao.StorageGeoLocationDAO;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

/**
 * Created by jcchn on 2/25/17.
 */

/* Lambda test
{
  "path": "/storages/post",
  "httpMethod": "POST",
  "body": {
      "storageId":"center-SID",
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
  "path": "/storages/post",
  "httpMethod": "POST",
  "body": {
      "storageId":"neigh2-SID",
      "available":"true",
      "cid":"neigh2",
      "storageDetail":{
        "description": "this is the description of the storage"
      },
      "displayPictureId":"displayPictureID",
      "price":"100",
      "rating":"80",
      "storageGeoJson":{
        "type":"Point",
        "coordinates":[47.6141360,-122.3390570]
      },
      "title":"this is a test storage",
      "type":"storage"
  }
}

//      "latitude":"47.6141360",
//      "longitude":"-122.3390570",
//	    center: {lat: 47.6101360, lng: -122.3420570},
//	    neigh1: {lat: 47.6091360, lng: -122.3400570},
//	    neigh2: {lat: 47.6141360, lng: -122.3390570},
//	    neigh3: {lat: 47.6121360, lng: -122.3440870},
*/
public class Poster implements StowplexRequestHandler {

    private static final String LOG_LABLE = "Poster";

    public static final String PATH = "/storages/post";
    private final TableInteractor tableInteractor;
    private final ObjectMapper mapper = new ObjectMapper();

    public Poster(TableInteractor tableInteractor){
        this.tableInteractor = tableInteractor;
    }
    @Override
    public String handleRequest(JSONObject event, Context context) throws InvalidPropertiesFormatException {
        LambdaLogger lambdaLogger = context.getLogger();
        Logger logger = new Logger(lambdaLogger, LOG_LABLE);

        if (event.get("body") == null){
            throw new InvalidPropertiesFormatException("body does not exist in event, event is:"+event.toJSONString());
        }

        if (!event.get("httpMethod").toString().equalsIgnoreCase("POST") ) {
            throw new InvalidPropertiesFormatException(PATH +
                    " does not support this httpMethod, httpMethod is:"+
                    event.get("httpMethod").toString());
        }

        String storageInString = event.get("body").toString();
        try {
            StorageGeoLocationDAO request = mapper.readValue(storageInString,StorageGeoLocationDAO.class);
            handlePost(request);
            return NoValueResponseFactory.getSuccessResponseInString();
        } catch (IOException e) {
            logger.logError("handleRequest error: "+e.toString());
            return NoValueResponseFactory.getFailureResponseInString();
        }
    }

    private void handlePost(StorageGeoLocationDAO request) throws JsonProcessingException {
        GeoPoint geoPoint = new GeoPoint(request.getStorageGeoJson().getCoordinates().get(0),
                request.getStorageGeoJson().getCoordinates().get(1));

        AttributeValue rangeKeyValue = new AttributeValue().withS(request.getStorageId());
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
