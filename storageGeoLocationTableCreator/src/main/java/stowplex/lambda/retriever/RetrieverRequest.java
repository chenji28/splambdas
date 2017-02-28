package stowplex.lambda.retriever;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Created by jcchn on 2/25/17.
 */
@Data
@NoArgsConstructor
public class RetrieverRequest {

    @NonNull
    @JsonProperty
    private String cid; // customer ID

    @NonNull
    @JsonProperty
    private RetrieverRequestGeoPoint minPoint;

    @NonNull
    @JsonProperty
    private RetrieverRequestGeoPoint maxPoint;

    @JsonProperty
    private Boolean available;

    @JsonProperty
    private String type;
}
