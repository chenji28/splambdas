package stowplex.lambda.creator;

/**
 * Created by jcchn on 2/21/17.
 */

import com.amazonaws.geo.util.GeoTableUtil;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.json.simple.JSONObject;
import stowplex.lambda.Logger;
import stowplex.lambda.NoValueResponseFactory;
import stowplex.lambda.StowplexRequestHandler;
import stowplex.lambda.TableInteractor;

/*
lambda testing
{
    "path": "/storages/createTable",
    "httpMethod": "GET"
}
*/
public class TableCreator implements StowplexRequestHandler {

    public static final String PATH = "/storages/createTable";

    private static final String LOG_LABLE = "TableCreator";
    private static final int MILLSECOND_TO_SECOND = 1000;
    private static final int CREATE_TABLE_TIMEOUT_S = 10 * MILLSECOND_TO_SECOND;
    private static final int POLL_TABLE_TIMEOUT_S = 1 * MILLSECOND_TO_SECOND;

    private final TableInteractor tableInteractor;

    public TableCreator(TableInteractor tableInteractor){
        this.tableInteractor = tableInteractor;
    }

    @Override
    public String handleRequest(JSONObject event, Context context) {
        LambdaLogger lambdaLogger = context.getLogger();
        Logger logger = new Logger(lambdaLogger, LOG_LABLE);
        logger.logInfo("Hello this is in TableCreator");

        createTable(logger);
        String responseInJson = NoValueResponseFactory.getSuccessResponseInString();

        logger.logInfo("response is:" + responseInJson);
        return responseInJson;
    }

    private void createTable(Logger logger){

        AmazonDynamoDB dynamoDB = tableInteractor.getDynamoDB();

        CreateTableRequest createTableRequest = GeoTableUtil.getCreateTableRequest(tableInteractor.getGeoDataManagerConfiguration());
        logger.logDebug("after init table request");

        TableUtils.createTableIfNotExists(dynamoDB,createTableRequest);

        // wait for the table to move into ACTIVE state
        try {
            TableUtils.waitUntilActive(dynamoDB, tableInteractor.getTableName(), CREATE_TABLE_TIMEOUT_S, POLL_TABLE_TIMEOUT_S);
            logger.logInfo("table is active now");
        } catch (InterruptedException e) {
            //ignore
        } catch(TableUtils.TableNeverTransitionedToStateException e){
            throw e;
        }
    }
}