package stowplex.lambda.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by jcchn on 3/4/17.
 */
@Data
@NoArgsConstructor
public class StorageGeoJson {
    private String type;
    private List<Double> coordinates;
}
/*

{"type":"Point","coordinates":[47.610136,-122.342057]}
 */