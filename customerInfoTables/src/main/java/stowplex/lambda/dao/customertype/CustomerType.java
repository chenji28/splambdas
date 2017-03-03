package stowplex.lambda.dao.customertype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jcchn on 3/2/17.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerType {

    @JsonProperty
    Boolean host;

    @JsonProperty
    Boolean guest;
}
