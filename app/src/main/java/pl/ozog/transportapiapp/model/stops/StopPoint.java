package pl.ozog.transportapiapp.model.stops;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;


@AllArgsConstructor
@Getter
@Setter
public class StopPoint {

    private String id;
    private String name;
    private String label;
    private long latitude;
    private long longitude;
    private String shortId;
    private String category;
    private boolean onDemand;

    public StopPoint(JSONObject stopPoint){

        List<String> list = Arrays.asList("id","label","name","stopPoint","latitude","longitude","onDemand","category");
        if(list.stream().allMatch(stopPoint::has)){
            try {
                this.id = (String) stopPoint.get("id");
                this.label = (String) stopPoint.get("label");
                this.name = (String) stopPoint.get("name");
                this.shortId = (String) stopPoint.get("stopPoint");
                this.latitude = ((Number) stopPoint.get("latitude")).longValue();
                this.longitude = ((Number)  stopPoint.get("longitude")).longValue();
                this.onDemand = (boolean) stopPoint.get("onDemand");
                this.category = (String) stopPoint.get("category");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        else throw new IllegalArgumentException("Required keys not exists in JSONObject");
    }

}
