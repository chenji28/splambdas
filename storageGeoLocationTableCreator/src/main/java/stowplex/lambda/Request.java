package stowplex.lambda;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jcchn on 2/21/17.
 */

@Data
@NoArgsConstructor
public class Request {
    private String firstName;
    private String lastName;
}
