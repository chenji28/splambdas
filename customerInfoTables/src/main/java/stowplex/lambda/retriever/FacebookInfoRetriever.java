package stowplex.lambda.retriever;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import stowplex.lambda.Logger;
import stowplex.lambda.TableInteractor;
import stowplex.lambda.dao.CustomerInfoDAO;
import stowplex.lambda.dao.FacebookProfileDAO;
import stowplex.lambda.dao.customertype.CustomerType;
import stowplex.lambda.dao.login.Login;

import java.util.HashSet;

/**
 * Created by jcchn on 3/1/17.
 */
public class FacebookInfoRetriever implements ProviderRetriever {
    private static final String NAME = FacebookProfileDAO.TABLE_NAME;
    private final DynamoDBMapper dynamoDBMapper;

    public FacebookInfoRetriever(TableInteractor tableInteractor) {
        dynamoDBMapper = new DynamoDBMapper(tableInteractor.getDynamoDB());
    }

    public CustomerInfoDAO RetrieveCustomerInfo(RetrieverRequest request, Logger logger){
        // find whether the id exists in fb table
        // if not then this is a new customer and we will create one in customer table
        /*TODO: even there is no record in fb table, there could still be other login mechanisms exist previously
            for the same customer, we should check whether other mechanisms exist
        */
        FacebookProfileDAO facebookProfileDAO = dynamoDBMapper.load(FacebookProfileDAO.class, request.getId(),
                new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT));
        if (facebookProfileDAO!=null) {
            //id already exist we can just retrieve it from customerInfoRetriever
            return dynamoDBMapper.load(CustomerInfoDAO.class, facebookProfileDAO.getCID(),
                    new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT));
        }

        // create a new customerInfoDAO and a new facebookProfileDAO
        CustomerInfoDAO customerInfoDAO = customerInfoDAOBuilder(request.getId());
        dynamoDBMapper.save(customerInfoDAO);

        // set cid in fb table
        facebookProfileDAO = FacebookProfileDAO.builder()
                .cID(customerInfoDAO.getCid())
                .id(request.getId())
                .build();
        dynamoDBMapper.save(facebookProfileDAO);

        return customerInfoDAO;
    }

    private CustomerInfoDAO customerInfoDAOBuilder(String fbId){
        CustomerInfoDAO customerInfoDAO = CustomerInfoDAO.builder()
                .customerType(CustomerType
                        .builder()
                        .guest(true)
                        .host(false)
                        .build())
                .logins(new HashSet<Login>())
                .build();

        customerInfoDAO
                .getLogins()
                .add(Login
                        .builder()
                        .id(fbId)
                        .provider(NAME)
                        .build());

        return customerInfoDAO;
    }
}
