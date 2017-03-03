package stowplex.lambda.retriever;

import stowplex.lambda.Logger;
import stowplex.lambda.dao.CustomerInfoDAO;

/**
 * Created by jcchn on 3/2/17.
 */
public interface ProviderRetriever {
    CustomerInfoDAO RetrieveCustomerInfo(RetrieverRequest request, Logger logger);
}
