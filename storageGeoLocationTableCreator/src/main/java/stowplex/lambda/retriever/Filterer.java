package stowplex.lambda.retriever;

import com.amazonaws.geo.model.GeoPoint;
import com.amazonaws.geo.model.GeoQueryResult;
import com.amazonaws.geo.util.GeoJsonMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import stowplex.lambda.Logger;
import stowplex.lambda.attributekeys.AttributeKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jcchn on 3/1/17.
 */
public class Filterer {

    //We need to use this method to perform filtering because the geo library for dynamoDB does not support
    // filter expression, all filter expressions were lost during the copyQueryRequest in DynamoDBUtil.java
    public List<AttributeKeys> getFilteredResult(GeoQueryResult geoQueryResult,
                                                               RetrieverRequest request,
                                                               Logger logger){
        List<AttributeKeys> filteredResult = new ArrayList<>();
        for(Map<String, AttributeValue> item : geoQueryResult.getItem()){
            if(matchAvailability(item,request) && matchType(item,request)){
                filteredResult.add(buildAttributeKeys(item));
            }
        }
        return filteredResult;
    }

    private AttributeKeys buildAttributeKeys(Map<String, AttributeValue> item){
        GeoPoint geoPoint = GeoJsonMapper.geoPointFromString(item.get("storageGeoJson").getS());
        AttributeKeys attributeKeys = new AttributeKeys();
        attributeKeys.setAvailable(item.get(AttributeKeys.AVAILABLE_KEY).getBOOL());
        attributeKeys.setCid(item.get(AttributeKeys.CID_KEY).getS());
        attributeKeys.setSid(item.get(AttributeKeys.SID_KEY).getS());
        attributeKeys.setLatitude(geoPoint.getLatitude());
        attributeKeys.setLongitude(geoPoint.getLongitude());
        attributeKeys.setDisplayPictureId(item.get(AttributeKeys.DISPLAY_PICTURE_ID_KEY).getS());
        attributeKeys.setPrice(Integer.parseInt(item.get(AttributeKeys.PRICE_KEY).getN()));
        attributeKeys.setRating(Integer.parseInt(item.get(AttributeKeys.RATING_KEY).getN()));
        attributeKeys.setTitle(item.get(AttributeKeys.TITLE_KEY).getS());
        attributeKeys.setType(item.get(AttributeKeys.TYPE_KEY).getS());

        return attributeKeys;
    }

    private boolean matchAvailability(Map<String, AttributeValue> item, RetrieverRequest request){
        return matchBooleanValue(item.get(AttributeKeys.AVAILABLE_KEY).getBOOL(),request.getAvailable());
    }

    private boolean matchType(Map<String, AttributeValue> item, RetrieverRequest request){
        return matchStringValue(item.get(AttributeKeys.TYPE_KEY).getS(),request.getType());
    }

    // util for filter expression
    private boolean matchBooleanValue(Boolean actual, Boolean expect){
        if(actual==null || expect==null) {
            return true;//attribute not set or no filter requirement, default to true
        }
        return actual.booleanValue()==expect;
    }

    // util for filter expression
    private boolean matchStringValue(String actual, String expect){
        if(actual==null || expect==null) {
            return true;//attribute not set or no filter requirement, default to true
        }
        return actual.equalsIgnoreCase(expect);
    }

}
