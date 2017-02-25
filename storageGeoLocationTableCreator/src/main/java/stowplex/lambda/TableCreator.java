package stowplex.lambda;

/**
 * Created by jcchn on 2/21/17.
 */

import com.amazonaws.geo.GeoDataManager;
import com.amazonaws.geo.GeoDataManagerConfiguration;
import com.amazonaws.geo.util.GeoTableUtil;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class TableCreator implements RequestHandler<Request, Response> {

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

    private final String region;
    private final String tableName;

    public TableCreator(){
        hashKey = System.getenv(HASH_KEY);
        rangeKey = System.getenv(RANGE_KEY);
        geohash = System.getenv(GEO_HASH);
        geoJson = System.getenv(GEO_JSON);
        geohashIndexName = System.getenv(GEO_HASH_INDEX_NAME);
        hashKeyLength = Integer.valueOf(System.getenv(HASH_KEY_LENGTH));

        region = System.getenv(REGION);
        tableName = System.getenv(TABLE_NAME);
    }

    // this is for testing
    public TableCreator(
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
    }

    public Response handleRequest(Request request, Context context) {
        LambdaLogger logger = context.getLogger();
        String greetingString = String.format("Hello this is in storageGeoLocationTableCreator\n");
        logger.log("greeting String is:"+greetingString);
        createTable(logger);
        return new Response(greetingString);
    }

    private void createTable(LambdaLogger logger){
        printToDebug(logger);
        Regions regions = Regions.fromName(region);

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(regions).build();
        logger.log("after creating client");

        GeoDataManagerConfiguration config = new GeoDataManagerConfiguration((AmazonDynamoDBClient)client, tableName)
                .withHashKeyAttributeName(hashKey)
                .withRangeKeyAttributeName(rangeKey)
                .withGeohashAttributeName(geohash)
                .withGeoJsonAttributeName(geoJson)
                .withGeohashIndexName(geohashIndexName)
                .withHashKeyLength(hashKeyLength);
        logger.log("after setting config");

        GeoDataManager geoIndexManager = new GeoDataManager(config);
        logger.log("after init manager");

        CreateTableRequest createTableRequest = GeoTableUtil.getCreateTableRequest(config);
        logger.log("after init table request");

        CreateTableResult createTableResult = client.createTable(createTableRequest);
        logger.log("after creating table ");
    }

    private void printToDebug(LambdaLogger logger){

        String log = String.format(
                "hashKey:%s, rangeKey:%s, geoHash:%s, geoJson:%s, geohashIndexName:%s, hashKeyLength:%d",
                hashKey,rangeKey,geohash,geoJson,geohashIndexName,hashKeyLength);
        logger.log("params are: "+log);
    }
}