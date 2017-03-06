package stowplex.lambda.retriever;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import stowplex.lambda.Logger;
import stowplex.lambda.NoValueResponseFactory;
import stowplex.lambda.StowplexRequestHandler;
import stowplex.lambda.TableInteractor;
import stowplex.lambda.dao.CustomerInfoDAO;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

/**
 * Created by jcchn on 3/1/17.
 */

/* Lambda test
{
  "path": "/test/customers/get",
  "httpMethod": "GET",
  "queryStringParameters": {
      "id":"10210349543627655",
      "provider": "fb"
  }
}
//id=10210349543627655&provider=fb
*/
public class Retriever implements StowplexRequestHandler{

    private static final String LOG_LABLE = "Retriver";

    public static final String PATH = "/customers/get";
    private final TableInteractor tableInteractor;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ProviderRetriever facebookInfoRetriever;

    public Retriever(TableInteractor tableInteractor) {
        this.tableInteractor = tableInteractor;
        facebookInfoRetriever = new FacebookInfoRetriever(tableInteractor);
    }

    @Override
    public String handleRequest(JSONObject event, Context context) throws InvalidPropertiesFormatException {
        LambdaLogger lambdaLogger = context.getLogger();
        Logger logger = new Logger(lambdaLogger, LOG_LABLE);

        if (event.get("queryStringParameters") == null){
            throw new InvalidPropertiesFormatException("queryStringParameters does not exist in event, event is:"+
                    event.toJSONString());
        }

        if (!event.get("httpMethod").toString().equalsIgnoreCase("GET") ) {
            throw new InvalidPropertiesFormatException(PATH +
                    " does not support this httpMethod, httpMethod is:"+
                    event.get("httpMethod").toString());
        }

        String queryInString = event.get("queryStringParameters").toString();
        try {
            RetrieverRequest request = mapper.readValue(queryInString,RetrieverRequest.class);
            CustomerInfoDAO customerInfoDAP = handleGet(request,logger);
            return mapper.writeValueAsString(customerInfoDAP);
        } catch (IOException e) {
            logger.logError("handleRequest error: "+e.toString());
            return NoValueResponseFactory.getFailureResponseInString();
        }
    }

    private CustomerInfoDAO handleGet(RetrieverRequest request, Logger logger){
        String loginProvider = request.getProvider();
        if (loginProvider.equalsIgnoreCase("fb")){
            return facebookInfoRetriever.RetrieveCustomerInfo(request,logger);
        }

        throw new NotImplementedException();
    }
}