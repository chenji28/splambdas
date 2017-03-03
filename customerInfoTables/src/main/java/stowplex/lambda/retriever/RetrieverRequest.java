package stowplex.lambda.retriever;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Created by jcchn on 3/2/17.
 */
@Data
@NoArgsConstructor
public class RetrieverRequest {

    @NonNull
    @JsonProperty
    private String id; // login ID

    @NonNull
    @JsonProperty
    private String provider; // login mechanism. only supports fb right now
}
