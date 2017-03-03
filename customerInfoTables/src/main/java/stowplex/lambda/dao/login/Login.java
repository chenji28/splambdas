package stowplex.lambda.dao.login;

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
public class Login {
    String provider;
    String id;
}
