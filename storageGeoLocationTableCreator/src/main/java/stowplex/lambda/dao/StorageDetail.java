package stowplex.lambda.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Created by jcchn on 2/25/17.
 */
@Data
@NoArgsConstructor
public class StorageDetail {

    @NonNull
    @JsonProperty
    private String description;
}
