package stowplex.lambda.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Created by jcchn on 3/4/17.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = stowplex.lambda.dao.StorageGeoLocationDAO.TABLE_NAME)
public class StorageGeoLocationDAO {
    public static final String TABLE_NAME = "StorageGeoLocation";

    @JsonProperty
    @DynamoDBHashKey
    private int storageIdHashKey;

    @NonNull
    @DynamoDBRangeKey
    @JsonProperty
    private String storageId; // storage ID

    @NonNull
    @DynamoDBAttribute
    @JsonProperty
    private boolean available;

    @NonNull
    @DynamoDBAttribute
    @JsonProperty
    private String cid; // customer ID

    @DynamoDBAttribute
    @JsonProperty
    @DynamoDBMarshalling(marshallerClass = StorageDetailJsonMarshaller.class)
    private StorageDetail storageDetail;

    @DynamoDBAttribute
    @JsonProperty
    private String displayPictureId;

    @DynamoDBAttribute
    @JsonProperty
    private int price;

    @DynamoDBAttribute
    @JsonProperty
    private int rating;

    @JsonProperty
    @DynamoDBAttribute
    @DynamoDBMarshalling(marshallerClass = StorageGeoJsonMarshaller.class)
    private StorageGeoJson storageGeoJson;

    @DynamoDBAttribute
    @JsonProperty
    private String title;

    @DynamoDBAttribute
    @JsonProperty
    private String type;
}
