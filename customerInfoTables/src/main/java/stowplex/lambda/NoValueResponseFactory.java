package stowplex.lambda;

/**
 * Created by jcchn on 2/25/17.
 */
public class NoValueResponseFactory {
    public static String getSuccessResponseInString(){
        return "{\"response\":\"success\"}";
    }

    public static String getFailureResponseInString(){
        return "{\"response\":\"fail\"}";
    }
}
