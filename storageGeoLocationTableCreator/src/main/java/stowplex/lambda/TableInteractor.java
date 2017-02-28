package stowplex.lambda;

import com.amazonaws.geo.GeoDataManager;
import com.amazonaws.geo.GeoDataManagerConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import lombok.Getter;

/**
 * Created by jcchn on 2/25/17.
 */
public class TableInteractor {

    private static final String HASH_KEY="HASH_KEY";
    private static final String RANGE_KEY="RANGE_KEY";
    private static final String GEO_HASH="GEO_HASH";
    private static final String GEO_JSON="GEO_JSON";
    private static final String GEO_HASH_INDEX_NAME="GEO_HASH_INDEX_NAME";
    private static final String HASH_KEY_LENGTH="HASH_KEY_LENGTH";

    private static final String REGION="REGION";
    private static final String TABLE_NAME = "TABLE_NAME";

    private final String hashKey;
    private final String rangeKey;
    private final String geohash;
    private final String geoJson;
    private final String geohashIndexName;
    private final int hashKeyLength;

    @Getter
    private final String region;

    @Getter
    private final String tableName;

    @Getter
    private GeoDataManagerConfiguration geoDataManagerConfiguration;

    @Getter
    private GeoDataManager geoDataManager;

    @Getter
    private AmazonDynamoDB dynamoDB;

    public TableInteractor(){
        hashKey = System.getenv(HASH_KEY);
        rangeKey = System.getenv(RANGE_KEY);
        geohash = System.getenv(GEO_HASH);
        geoJson = System.getenv(GEO_JSON);
        geohashIndexName = System.getenv(GEO_HASH_INDEX_NAME);
        hashKeyLength = Integer.valueOf(System.getenv(HASH_KEY_LENGTH));

        region = System.getenv(REGION);
        tableName = System.getenv(TABLE_NAME);

        initGeoDataManager();
    }

    // this is for testing
    public TableInteractor(
            final String hashkey,
            final String rangeKey,
            final String geohash,
            final String geoJson,
            final String geohashIndexName,
            final int hashKeyLength,
            final String region,
            final String tableName){
        this.hashKey = hashkey;
        this.rangeKey = rangeKey;
        this.geohash = geohash;
        this.geoJson = geoJson;
        this.geohashIndexName = geohashIndexName;
        this.hashKeyLength = hashKeyLength;

        this.region = region;
        this.tableName = tableName;

        initGeoDataManager();
    }

    private void initGeoDataManager(){
        Regions regions = Regions.fromName(region);

        dynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(regions).build();

        geoDataManagerConfiguration = new GeoDataManagerConfiguration((AmazonDynamoDBClient)dynamoDB, tableName)
                .withHashKeyAttributeName(hashKey)
                .withRangeKeyAttributeName(rangeKey)
                .withGeohashAttributeName(geohash)
                .withGeoJsonAttributeName(geoJson)
                .withGeohashIndexName(geohashIndexName)
                .withHashKeyLength(hashKeyLength);

        geoDataManager = new GeoDataManager(geoDataManagerConfiguration);
    }
}
