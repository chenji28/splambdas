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
public class RetrieverRequestGeoPoint {

    @NonNull
    @JsonProperty
    private double latitude;

    @NonNull
    @JsonProperty
    private double longitude;
}
