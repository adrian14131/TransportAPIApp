package pl.ozog.transportapiapp.connectiontasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import lombok.Getter;
import pl.ozog.transportapiapp.generator.Generator;

public class ApiTask extends AsyncTask<String, Void, String> {


    @Getter
    private int responseCode = -1;
    @Getter
    private long downloadSize = 0;

    @Override
    protected String doInBackground(String... strings) {

        String result = "";
        responseCode = -1;
        if(strings.length>0){
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                responseCode = connection.getResponseCode();
                InputStream is = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                result = readString(reader);
                downloadSize = result.getBytes().length;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    private String readString(Reader reader) throws  IOException{
        StringBuilder sb = new StringBuilder();
        int ch;
        while((ch = reader.read()) != -1){
            sb.append((char) ch);

        }
        return sb.toString();
    }
}
