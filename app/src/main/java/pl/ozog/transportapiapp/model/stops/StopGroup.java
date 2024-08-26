package pl.ozog.transportapiapp.model.stops;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class StopGroup {
    private String name;
    private String shortId;
    private ArrayList<StopPoint> stopPoints;

    public StopGroup(String name, String shortId) {
        this.name = name;
        this.shortId = shortId;
        this.stopPoints = new ArrayList<>();
    }
    public StopGroup(JSONObject stopPoint) {

        if(stopPoint.has("name") && stopPoint.has("shortName")){
            try {
                this.name = (String) stopPoint.get("name");
                this.shortId = (String) stopPoint.get("shortName");
                this.stopPoints.add(new StopPoint(stopPoint));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        else throw new IllegalArgumentException("Key 'shortName' or 'name' not exist in JSONObject");
    }
    public StopGroup(String name, JSONObject stopPoint, boolean clean) {
        if(stopPoint.has("shortName")){
            this.name = name;
            this.stopPoints = new ArrayList<>();
            try {
                this.shortId = (String) stopPoint.get("shortName");

                if(!clean){
                    this.stopPoints.add(new StopPoint(stopPoint));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        else throw new IllegalArgumentException("Key 'shortName' not exist in JSONObject");

    }

    public StopGroup(String name, String shortId, StopPoint stopPoint) {
        this.name = name;
        this.shortId = shortId;
        this.stopPoints = new ArrayList<>();
        if(stopPoint != null){
            this.stopPoints.add(stopPoint);
        }
    }

    public void addStopPoint(JSONObject stopPoint){
        this.stopPoints.add(new StopPoint(stopPoint));

    }
    public void addStopPoint(StopPoint stopPoint){
        this.stopPoints.add(stopPoint);
    }

    public StopPoint getStopPointByShortId(String shortId){
        for(StopPoint stopPoint: stopPoints){
            if(stopPoint.getShortId().equals(shortId)) return stopPoint;
        }
        return null;
    }
    public StopPoint getStopPointByName(String name){
        for(StopPoint stopPoint: stopPoints){
            if(stopPoint.getName().equals(name)) return stopPoint;
        }
        return null;
    }

    public boolean hasShortId(String shortId){
        return this.shortId.equals(shortId);
    }




}
