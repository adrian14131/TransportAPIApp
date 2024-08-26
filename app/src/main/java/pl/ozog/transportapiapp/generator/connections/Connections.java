package pl.ozog.transportapiapp.generator.connections;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Documented;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import pl.ozog.transportapiapp.connectiontasks.ApiTask;
import pl.ozog.transportapiapp.connectiontasks.PageTask;
import pl.ozog.transportapiapp.generator.Generator;

public class Connections {

    public static Map<String, Map<String, String>> cookiesMap;
    public static boolean DEBUG = true;
    public static int MAXSIZE = 1024*1024*20;
    public static Document download(String url, String cookiesName){

        if(cookiesMap == null) cookiesMap = new HashMap<>();

        Connection.Response response = null;
        try {
            response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36")
                    .cookies(cookiesMap.getOrDefault(cookiesName, new HashMap<>()))
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .execute();
            cookiesMap.put(cookiesName, response.cookies());
            Generator.downloadedSize += response.bodyAsBytes().length;
            Generator.numberOfLoadingPages++;

            return response.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String downloadString(String url, String charsetName){
        try {
            InputStream is = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(charsetName)));
            String result = readString(reader);
            Generator.downloadedSize += result.getBytes().length;
            Generator.numberOfRequests++;

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String downloadStringAsync(String url, String charsetName){
        ApiTask apiTask = new ApiTask();

        try {
            return apiTask.execute(url).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static String readString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while((ch = reader.read()) != -1){
            sb.append((char) ch);
        }
        return sb.toString();
    }
}
