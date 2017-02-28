package stowplex.lambda.attributekeys;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Created by jcchn on 3/1/17.
 */

@Data
@NoArgsConstructor
public class AttributeKeys {
    public static final String SID_KEY = "storageId";
    public static final String CID_KEY = "cid";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String TYPE_KEY = "type";
    public static final String PRICE_KEY = "price";
    public static final String TITLE_KEY = "title";
    public static final String RATING_KEY = "rating";
    public static final String DISPLAY_PICTURE_ID_KEY = "displayPictureId";
    public static final String STORAGE_DETAIL_KEY = "storageDetail";
    public static final String AVAILABLE_KEY = "available";

    @NonNull
    @JsonProperty
    private String sid; // storage ID

    @NonNull
    @JsonProperty
    private String cid; // customer ID

    @NonNull
    @JsonProperty
    private double latitude;

    @NonNull
    @JsonProperty
    private double longitude;

    @JsonProperty
    private String type;

    @JsonProperty
    private int price;

    @JsonProperty
    private String title;

    @JsonProperty
    private int rating;

    @JsonProperty
    private String displayPictureId;

    @JsonProperty
    private StorageDetail storageDetail;

    @JsonProperty
    private boolean available;
}
