package stowplex.lambda.retriever;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by jcchn on 2/25/17.
 */
@Data
@NoArgsConstructor
public class RetrieverRequestGeoPoint {
    private static final ObjectMapper mapper = new ObjectMapper();

    @NonNull
    @JsonProperty
    private double latitude;

    @NonNull
    @JsonProperty
    private double longitude;

    public static RetrieverRequestGeoPointBuilder builder(){
        return new RetrieverRequestGeoPointBuilder();
    }

    public static class RetrieverRequestGeoPointBuilder{
        private String encodedGeoPoint;

        public RetrieverRequestGeoPointBuilder encodedGeoPoint(String encodedGeoPoint){
            this.encodedGeoPoint = encodedGeoPoint;
            return this;
        }

        public RetrieverRequestGeoPoint build() throws IOException {
            String geoPointInString = URLDecoder.decode(encodedGeoPoint, "UTF-8");
            return mapper.readValue(geoPointInString, RetrieverRequestGeoPoint.class);
        }
    }
}
