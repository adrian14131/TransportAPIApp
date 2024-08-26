package pl.ozog.transportapiapp.generator;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

import pl.ozog.transportapiapp.generator_adapters.GsonLocalDateDeserializer;
import pl.ozog.transportapiapp.generator_adapters.GsonLocalDateSerializer;
import pl.ozog.transportapiapp.generator_adapters.GsonLocalDateTimeDeserializer;
import pl.ozog.transportapiapp.generator_adapters.GsonLocalDateTimeSerializer;
import pl.ozog.transportapiapp.generator.loaders.mpk.LineLoader;
import pl.ozog.transportapiapp.generator.loaders.ttss.StopsLoader;
import pl.ozog.transportapiapp.generator.loaders.ttss.TTSSApiLoader;
import pl.ozog.transportapiapp.generator.measure.Measure;
import pl.ozog.transportapiapp.generator.measure.Measures;
import pl.ozog.transportapiapp.model.Transport;
import pl.ozog.transportapiapp.tools.FileTools;

public class Generator {

    public static boolean DEBUG = false;
    public static boolean ERROR = true;
    public static File internalDir = null;
    public static long downloadedSize = 0;
    public static double percent;
    public static double maxPercent = 100;
    public static boolean isReady = false;
    public static boolean loaded = false;
    public static boolean processing = false;
    public static boolean generateEveryRequest = false;
    public static TTSSApiLoader apiLoader = new TTSSApiLoader("/geoserviceDispatcher/services/stopinfo/stopPoints?left=-648000000&bottom=-324000000&right=648000000&top=324000000");
    public static StopsLoader stopsLoader = new StopsLoader();
    public static LineLoader lineLoader = new LineLoader("https://rozklady.mpk.krakow.pl");
    private static String measureJsonPath = "measures.json";
    private static String transportJsonPath = "transport.json";
    public static Transport transport = new Transport();
    public static Measure lastMeasure;
//    public static Generator.Callback callback;
    public static long fullProcessStopTime;
    public static long numberOfStopPoints =0, numberOfStopGroups = 0, numberOfLines = 0, numberOfRoutes = 0, numberOfLoadingPages = 0, numberOfRequests = 0;

    public static void dLog(String tag, String message){
        if(DEBUG)
            if(ERROR)
                Log.e(tag, message);
            else
                Log.d(tag, message);
    }

    public static void startProcess(){
        Measure measure = new Measure(LocalDateTime.now(), 0L,0,0,0,0,0,0,0);
        downloadedSize = 0;
        numberOfStopPoints =0;
        numberOfStopGroups = 0;
        numberOfLines = 0;
        numberOfRoutes = 0;
        numberOfRequests = 0;
        numberOfLoadingPages = 0;

        long startTime = System.nanoTime();
;
        loaded = false;
        processing = true;
        apiLoader = new TTSSApiLoader("/geoserviceDispatcher/services/stopinfo/stopPoints?left=-648000000&bottom=-324000000&right=648000000&top=324000000");
//        apiLoader.loadJson("tram");
        apiLoader.loadJson("bus");

        stopsLoader = new StopsLoader(apiLoader.getJsons(), true);

        apiLoader.translateJsons();

        stopsLoader.generateStopGroupList(false);

        lineLoader = new LineLoader("https://rozklady.mpk.krakow.pl", stopsLoader.getStops());

        transport = new Transport(LocalDate.now(), lineLoader.getLines(), stopsLoader.getStops());

        saveJsonToFile(transportJsonPath, serializeToJson(transport));
        processing = false;
        long stopTime = System.nanoTime();

        measure.setExecutionTime(stopTime - startTime);
        measure.setNumberOfLines(numberOfLines);
        measure.setNumberOfRoutes(numberOfRoutes);
        measure.setNumberOfStopGroups(numberOfStopGroups);
        measure.setNumberOfStopPoints(numberOfStopPoints);
        measure.setDownloadSize(downloadedSize);
        measure.setNumberOfRequests(numberOfRequests);
        measure.setNumberOfLoadingPages(numberOfLoadingPages);
        Generator.lastMeasure = measure;
        saveMeasure(measureJsonPath, measure);
        loaded = true;

    }
    public static void saveMeasure(String filePath, Measure measureResult) {


        Measures measures = new Measures();

        if(isFileExists(filePath))
            measures = loadMeasure(filePath);
        measures.addMeasures(measureResult);

        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeSerializer()).serializeNulls().disableHtmlEscaping().create();
        try {
            saveJsonToFile(filePath, new JSONObject(gson.toJson(measures)));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public static Measures loadMeasure(String filePath){
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeDeserializer()).serializeNulls().disableHtmlEscaping().create();
        return gson.fromJson(readJsonFromFile(filePath).toString(), Measures.class);
    }
    public static boolean canGenerate(){
        if(transport == null) return true;
        if(transport.getLastUpdate() == null) return true;
        return transport.getLastUpdate().isBefore(LocalDate.now()) || generateEveryRequest;
    }
    public static void saveJsonToFile(String filePath, JSONObject jsonObject){
        if(internalDir!=null){
            FileTools.saveJsonToFile(internalDir, filePath, jsonObject);
        }
    }
    public static JSONObject readJsonFromFile(String filePath){
        if(internalDir!=null){
            return FileTools.loadJsonFromFile(internalDir, filePath);
        }
        return null;
    }

    public static Transport deserializeFromJson(JSONObject jsonObject){
        Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().registerTypeAdapter(LocalDate.class, new GsonLocalDateDeserializer()).create();
        return gson.fromJson(jsonObject.toString(), Transport.class);
    }
    public static JSONObject serializeToJson(Transport t){
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().registerTypeAdapter(LocalDate.class, new GsonLocalDateSerializer()).create();
            return new JSONObject(gson.toJson(t));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public static double calculatePercentage(double value, double from, double minPercentage, double maxPercentage){
        if(minPercentage > maxPercentage){
            double temp = minPercentage;
            minPercentage = maxPercentage;
            maxPercentage = temp;
        }
        if(from == 0) return -1;
        double result = ((value / from) * (maxPercentage - minPercentage)) + minPercentage;
        result = Double.valueOf(String.format("%.2f", result).replace(",","."));
        return result > 100? 100: result;
    }

    //    public static void initJsons(){
//        Measures measures = new Measures();
//        if(!isFileExists(measureJsonPath)){
//            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeSerializer()).serializeNulls().disableHtmlEscaping().create();
//            try {
//                saveJsonToFile(measureJsonPath, (JSONObject) new JSONParser().parse(gson.toJson(measures)));
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//
//
//    }
    public static boolean isDataReady(){
        return isReady;
    }


    public static boolean isFileExists(String filePath){
        return FileTools.isFileExist(internalDir, filePath);
    }

    public static Transport getTransportFromJson(File internalStorageDir, String filePath){
        JSONObject transportJson = FileTools.loadJsonFromFile(internalStorageDir, filePath);
        Transport result = new Transport();
        if(transportJson!=null){
            result = Generator.deserializeFromJson(transportJson);
        }
        return result;
    }

    public static Transport getTransportFromJson(File internalStorageDir){
        return getTransportFromJson(internalStorageDir, transportJsonPath);
    }
//    public interface Callback{
//        void onStatusChange();
//    }

}
