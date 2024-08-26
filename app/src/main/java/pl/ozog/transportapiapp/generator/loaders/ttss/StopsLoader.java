package pl.ozog.transportapiapp.generator.loaders.ttss;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import pl.ozog.transportapiapp.generator.Generator;
import pl.ozog.transportapiapp.model.stops.StopGroup;
import pl.ozog.transportapiapp.model.stops.StopPoint;

@Getter
@Setter
public class StopsLoader {
    private ArrayList<StopGroup> stops;
    private Map<String, JSONObject> jsons;
//    private Generator.Callback callback;
    public StopsLoader(){
        this.stops = new ArrayList<>();
    }

    public StopsLoader(Map<String, JSONObject> jsons){
        this.jsons = jsons;
        this.stops = new ArrayList<>();
    }
    public StopsLoader(Map<String, JSONObject> jsons, boolean preGenerate){
        this(jsons);
        if(preGenerate)
            generator();
    }

    private void generator(){
        generateStopGroupList(true);
    }
    public void generateStopGroupList(boolean clean){
        generateStopGroupList(this.jsons, clean);
    }
    private void generateStopGroupList(Map<String, JSONObject> jsons, boolean clean){
        for(Map.Entry<String, JSONObject> jsonEntry: jsons.entrySet()){
            try{
                JSONArray jsonArray = (JSONArray) jsonEntry.getValue().get("stopPoints");
                generateStopGroupList(jsonArray, clean);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }
    }

    private void generateStopGroupList(JSONArray stopPoints, boolean clean){
        for(int i=0; i<stopPoints.length(); i++){
            addToStopGroup(stopPoints.optJSONObject(i), clean);
        }
    }
    private void addToStopGroup(JSONObject stopPoint, boolean clean){
        try{
            for(StopGroup stopGroup: stops){
                if(stopGroup.hasShortId((String) stopPoint.get("shortName"))){
                    if(!clean){
                        stopGroup.addStopPoint(stopPoint);
                        Generator.numberOfStopPoints++;
                    }
                    return;
                }
            }
            String name = cleanName((String) stopPoint.get("name"), (String) stopPoint.get("stopPoint"));
            if(!clean) Generator.numberOfStopPoints++;
            stops.add(new StopGroup(name, stopPoint, clean));
            Generator.numberOfStopGroups++;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    private String cleanName(String name, String shortId){
        name = name.replace("("+shortId+")","");
        name = TTSSApiLoader.removeOnDemandFromName(name);
        name = name.stripTrailing();
        return name;
    }
    public StopPoint getStopPointByShortId(String shortId){
        return getStopPointByShortId(stops, shortId);
    }
    public static StopPoint getStopPointByShortId(ArrayList<StopGroup> stopGroups, String shortId){
        for(StopGroup stopGroup: stopGroups){
            StopPoint stopPoint = stopGroup.getStopPointByShortId(shortId);
            if(stopPoint!=null) return stopPoint;
        }
        return null;
    }
    public StopPoint getStopPointByShortIdFast(String shortId){
        return getStopPointByShortIdFast(stops, shortId);
    }
    public static StopPoint getStopPointByShortIdFast(ArrayList<StopGroup> stopGroups, String shortId){
        for(StopGroup stopGroup: stopGroups){
            if(stopGroup.getShortId().equals(shortId.substring(0,shortId.length()-2))){
                return stopGroup.getStopPointByShortId(shortId);
            }
        }
        return null;
    }
    public StopGroup getStopGroupByShortId(String shortId){
        for(StopGroup stopGroup: stops){
            if(stopGroup.getShortId().equals(shortId)) return stopGroup;
        }
        return null;
    }
    public static StopGroup getStopGroupByName(ArrayList<StopGroup> stopGroups, String name){
        for(StopGroup stopGroup: stopGroups){
            if(stopGroup.getName().equals(name)) return stopGroup;
        }
        return null;
    }
    public StopGroup getStopGroupByName(String name){
        return getStopGroupByName(stops, name);
    }
    public StopPoint getStopPointByNameFast(String name){
        return getStopPointByNameFast(stops, name);
    }
    public static StopPoint getStopPointByNameFast(ArrayList<StopGroup> stopGroups, String name){
        StopPoint result = null;
        for(StopGroup stopGroup: stopGroups){
            if(stopGroup.getName().equals(name.substring(0,name.length()-3))){
                result = stopGroup.getStopPointByName(name);
            }
        }
        return result;
    }
    public StopPoint getStopPointByName(String name){
        return getStopPointByName(stops, name);
    }
    public static StopPoint getStopPointByName(ArrayList<StopGroup> stopGroups, String name){
        for(StopGroup stopGroup: stopGroups){
            StopPoint stopPoint = stopGroup.getStopPointByName(name);
            if(stopPoint!=null) return stopPoint;
        }
        return null;
    }


}
