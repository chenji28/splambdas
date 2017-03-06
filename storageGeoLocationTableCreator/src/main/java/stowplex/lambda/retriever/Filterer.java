package stowplex.lambda.retriever;

import com.amazonaws.geo.model.GeoQueryResult;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import stowplex.lambda.Logger;
import stowplex.lambda.TableInteractor;
import stowplex.lambda.dao.StorageGeoLocationDAO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jcchn on 3/1/17.
 */
public class Filterer {

    private final DynamoDBMapper dynamoDBMapper;
    public Filterer(TableInteractor tableInteractor){
        this.dynamoDBMapper = new DynamoDBMapper(tableInteractor.getDynamoDB());
    }

    //We need to use this method to perform filtering because the geo library for dynamoDB does not support
    // filter expression, all filter expressions were lost during the copyQueryRequest in DynamoDBUtil.java
    public List<StorageGeoLocationDAO> getFilteredResult(GeoQueryResult geoQueryResult,
                                                 RetrieverRequest request,
                                                 Logger logger) {

        List<StorageGeoLocationDAO> filteredResults = dynamoDBMapper
                .marshallIntoObjects(StorageGeoLocationDAO.class, geoQueryResult.getItem())
                .stream()
                .filter(storageGeoLocation ->
                        matchBooleanValue(storageGeoLocation.isAvailable(), request.getAvailable()) &&
                        matchStringValue(storageGeoLocation.getType(), request.getType()))
                .collect(Collectors.toList());

        return filteredResults;
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
