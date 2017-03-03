package stowplex.lambda;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * Created by jcchn on 2/25/17.
 */
public class Logger {
    private final LambdaLogger logger;
    private final String logInfoTag = "INFO";
    private final String logErrorTag = "ERROR";
    private final String logDebugTag = "DEBUG";

    private static final boolean debug;

    static{
        debug = true;
    }
    private String logLable;

    public Logger(LambdaLogger logger, String logLable){
        this.logger = logger;
        this.logLable = logLable;
    }

    public void logInfo (String str){
        log(logInfoTag,str);
    }

    public void logDebug(String str){
        if (debug) {
            log(logDebugTag, str);
        }
    }

    public void logError(String str){
        log(logErrorTag,str);
    }

    private void log(String tag, String str){
        String content = String.format("[%s] [%s] %s \n",tag, logLable, str);
        logger.log(content);
    }
}
