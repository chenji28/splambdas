package stowplex.lambda;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Created by jcchn on 2/21/17.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Response {

    @NonNull
    private String greetings;
}
