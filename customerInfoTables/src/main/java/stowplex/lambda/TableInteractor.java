package stowplex.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import lombok.Getter;

/**
 * Created by jcchn on 2/25/17.
 */
public class TableInteractor {

    private static final String REGION="REGION";

    @Getter
    private final String region;

    @Getter
    private AmazonDynamoDB dynamoDB;

    public TableInteractor(){
        region = System.getenv(REGION);

        initDynamoDB();
    }

    // this is for testing
    public TableInteractor(
            final String region,
            final String tableName){

        this.region = region;

        initDynamoDB();
    }

    private void initDynamoDB(){
        Regions regions = Regions.fromName(region);

        dynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(regions).build();
    }
}
