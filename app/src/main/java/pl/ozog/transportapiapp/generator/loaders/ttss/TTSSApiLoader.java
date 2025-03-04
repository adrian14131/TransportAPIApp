package pl.ozog.transportapiapp.generator.loaders.ttss;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import pl.ozog.transportapiapp.generator.Generator;
import pl.ozog.transportapiapp.generator.connections.Connections;
import pl.ozog.transportapiapp.generator.exceptions.EmptyResponseException;

@Getter
@Setter
public class TTSSApiLoader {

    private Map<String, String> urls;
    private String endPoint;
    private Map<String, JSONObject> jsons;
    public TTSSApiLoader(String endPoint) {
        this.urls = new HashMap<>();
        this.endPoint = endPoint;
        this.jsons = new HashMap<>();
        configuration();
    }
    private void configuration() {
        urls.put("tram", "http://www.ttss.krakow.pl/internetservice");
        urls.put("bus", "http://91.223.13.70/internetservice");

    }

    public void loadJson(String category){
        if(urls.keySet().contains(category)){
            String url = urls.get(category)+endPoint;
            String jsonContent = Connections.downloadString(url, "UTF-8");

            try {
                JSONObject jsonObject = !jsonContent.isEmpty()? new JSONObject(jsonContent): null;
                if(jsonObject!=null){
                    jsons.put(category, jsonObject);
                }
                else throw new EmptyResponseException("Response for url: "+url+" is empty");
            } catch (EmptyResponseException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        else throw new IllegalArgumentException("Unexpected argument value: "+category);
    }
    public void translateJsons(){
        for(Map.Entry<String, JSONObject> json: jsons.entrySet()){
            jsons.replace(json.getKey(), translateJson(json));
        }
    }
    private JSONObject translateJson(Map.Entry<String, JSONObject> jsonEntry){
        JSONObject json = jsonEntry.getValue();
        if(json.has("stopPoints")){
            try{
                JSONArray stopPoints = (JSONArray) json.get("stopPoints");
                for (int i = 0; i < stopPoints.length(); i++) {
                    JSONObject stopPoint = stopPoints.optJSONObject(i);
                    if(stopPoint.has("stopPoint") && stopPoint.has("name")){
                        String shortId = (String) stopPoint.get("stopPoint");
                        String name = (String) stopPoint.get("name");
                        stopPoint.put("onDemand", onDemand(name));

                        name = removeOnDemandFromName(name);
                        name = changeName(name, shortId, jsonEntry.getKey());
                        stopPoint.put("name", name);
                    }

                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        return json;
    }
    private static boolean onDemand(String name){
        return name.contains("(nż)");
    }
    public static String removeOnDemandFromName(String name){
        return onDemand(name)? name.replace("(nż)",""): name;
    }
    private String changeName(String name, String shortId, String category){
        String result = removeOnDemandFromName(name);
        shortId = shortId.length()<2? "0".repeat(2-shortId.length())+shortId: shortId;

        String stopPlatform = category.equals("bus")? shortId.substring(shortId.length()-2): (category.equals("tram")? "0"+shortId.charAt(shortId.length()-2): "");
        result = result.replace("("+shortId+")","");
        result = result.stripTrailing();
        result += " "+stopPlatform;
        return result;
    }

}
