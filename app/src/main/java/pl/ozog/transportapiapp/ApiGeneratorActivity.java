package pl.ozog.transportapiapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import pl.ozog.transportapiapp.generator.Generator;
import pl.ozog.transportapiapp.generator_adapters.GsonLocalDateTimeDeserializer;
import pl.ozog.transportapiapp.connectiontasks.ApiTask;
import pl.ozog.transportapiapp.generator.measure.Measure;
import pl.ozog.transportapiapp.tools.FileTools;


public class ApiGeneratorActivity extends AppCompatActivity implements View.OnClickListener{

    TextView infoTextView, finalResultTextView;
    ProgressBar progressBar;

    Button startButton, nextButton;

    String url;
    String transportJsonStr;
    String transportJsonPath = "transport.json";

    Long delay = 1000L;
    Long downloadSize = 0L;
    int apiRequests = 0;

    boolean started = false;

    Map<String, String> endpoints;
    File internalStorageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_api_generator);
        Bundle extras = getIntent().getExtras();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        internalStorageDir = getDataDir();
        transportJsonStr = "";

        finalResultTextView = findViewById(R.id.apiFinalResultTextView);
        infoTextView = findViewById(R.id.apiInfoTextView);

        url = "http://192.168.0.10:8080";

        endpoints = new HashMap<>();
        endpoints.put("GENERATE", "/transport/generate");
        endpoints.put("GET", "/transport/get");
        endpoints.put("STATUS", "/transport/status");
        endpoints.put("MEASURES", "/transport/measures");

        startButton = findViewById(R.id.apiStartButton);
        startButton.setOnClickListener(this);

        nextButton = findViewById(R.id.apiNextButton);
        nextButton.setOnClickListener(this);

        progressBar = findViewById(R.id.apiProgressBar);


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.apiStartButton:
                infoTextView.setText("Start");
                startButton.setEnabled(false);
                apiWaitProcess();
                break;
            case R.id.apiNextButton:
                Intent apiIntent = new Intent(ApiGeneratorActivity.this, DataViewerActivity.class);
                startActivity(apiIntent);
                break;

        }

    }
    private void showFinalInfo(Long nsTime, Measure apiMeasure){
        double msTime = (double) nsTime / 1000000.0;
        double genMsTime = (double) apiMeasure.getExecutionTime() / 1000000.0;
        StringBuilder sb = new StringBuilder("Całkowity czas: "+msTime+"ms\n");
        sb.append("Data generowania: ").append(apiMeasure.getTime().toString()).append("\n");
        sb.append("Czas generowania: ").append(genMsTime).append("ms\n");
        sb.append("Przystanki: ").append(apiMeasure.getNumberOfStopPoints()).append(" w ").append(apiMeasure.getNumberOfStopGroups()).append(" grupach\n");
        sb.append("Liczba linii: ").append(apiMeasure.getNumberOfLines()).append("\n");
        sb.append("Liczba tras: ").append(apiMeasure.getNumberOfRoutes()).append("\n");
        sb.append("Załadowanych stron: ").append(apiMeasure.getNumberOfLoadingPages()).append("\n");
        double serverDownloadSize = ((double) apiMeasure.getDownloadSize()) / (1024.0*1024.0);
        sb.append("Pobranych danych serwer (B): ").append(apiMeasure.getDownloadSize()).append("B\n");
        sb.append("Pobranych danych serwer (MB): ").append(serverDownloadSize).append("MB\n");
        sb.append("Zapytań API: ").append(apiMeasure.getNumberOfRequests()).append("\n");
        double appDownloadSize = ((double) downloadSize) / (1024.0*1024.0);
        sb.append("Pobranych danych aplikacja (B): ").append(downloadSize).append("B\n");
        sb.append("Pobranych danych aplikacja (MB): ").append(appDownloadSize).append("MB\n");
        sb.append("APIs: ").append(apiRequests).append("\n");

        if(internalStorageDir!=null){
            try {
                FileTools.saveJsonToFile(internalStorageDir, transportJsonPath, new JSONObject(transportJsonStr));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        finalResultTextView.setText(sb.toString());
    }
    private Measure getLastMeasure(){
        ApiTask apiTask = new ApiTask();
        try{
            String result = apiTask.execute(url+endpoints.get("MEASURES")) .get();
            if(result!=null && !result.isEmpty() && apiTask.getResponseCode()==200){
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeDeserializer()).serializeNulls().disableHtmlEscaping().create();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("measures");
                if(jsonArray.length()>0){
                    return gson.fromJson(jsonArray.get(jsonArray.length()-1).toString(), Measure.class);
                }

            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return new Measure(LocalDateTime.MIN, -1L, -1,-1,-1,-1,-1,-1,-1);
    }
    private void apiWaitProcess(){

        ApiTask apiTask = new ApiTask();
        boolean isReady = false;
        if(!started){

            started = true;
            apiRequests = 0;
            downloadSize = 0L;
            long startTime = System.nanoTime();
            progressBar.setProgress(0);
            try {

                String response = apiTask.execute(url+endpoints.get("GENERATE")).get();
                apiRequests++;
                switch (apiTask.getResponseCode()){
                    case 200:
                        infoTextView.setText("Dane są gotowe");
                        transportJsonStr = response;
                        isReady = true;
                        started = false;
                        downloadSize+=apiTask.getDownloadSize();
                        new Handler().postDelayed(() -> {
                            startButton.setEnabled(true);
                            progressBar.setProgress(100);
                            nextButton.setEnabled(true);

                            showFinalInfo(System.nanoTime()-startTime, getLastMeasure());
                        },500);
                        break;

                    case 202:
                    case 204:
                        infoTextView.setText("Dane nie są gotowe");
                        break;

                    default:
                        infoTextView.setText("Wystąpił błąd");
                        return;
                }

                if(!isReady){
                    Handler handler = new Handler(Looper.getMainLooper());

                    Runnable r = new Runnable() {
                        @Override
                        public void run() {

                            try{
                                ApiTask statusApiTask = new ApiTask();
                                String statusJsonStr = statusApiTask.execute(url+endpoints.get("STATUS")).get();
                                apiRequests++;
                                downloadSize+=statusApiTask.getDownloadSize();
                                JSONObject jsonObject = new JSONObject(statusJsonStr);
                                double percentage = jsonObject.optDouble("percentage", 0);
                                if(!isReadyToDownload(jsonObject)){
                                    infoTextView.setText("Generowanie danych na serwerze: "+percentage+"%");
                                    progressBar.setProgress((int) percentage);
                                    handler.postDelayed(this, delay);
                                }
                                else{
                                    ApiTask apiTask1 = new ApiTask();
                                    try {
                                        String res = apiTask1.execute(url+endpoints.get("GET")).get();
                                        apiRequests++;
                                        downloadSize+=apiTask1.getDownloadSize();
                                        if(apiTask1.getResponseCode()==200){
                                            transportJsonStr = res;
                                            infoTextView.setText("Dane są gotowe");
                                            started = false;
                                            startButton.setEnabled(true);
                                            nextButton.setEnabled(true);
                                            progressBar.setProgress(100);
                                            showFinalInfo(System.nanoTime()-startTime, getLastMeasure());

                                        }
                                    } catch (ExecutionException e) {
                                        throw new RuntimeException(e);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    };
                    handler.post(r);
                }
            }
            catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



    }

    private boolean isReadyToDownload(JSONObject jsonObject){
        try {
            if(jsonObject!=null){
                if(jsonObject.has("ready")){
                    return jsonObject.getBoolean("ready");
                }
            }
            return false;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public class ApiTask2 extends AsyncTask<String, Void, String> {


        @Getter
        private int responseCode = -1;
        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            responseCode = -1;
            if(strings.length>0){
                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    responseCode = connection.getResponseCode();
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    result = readString(reader);
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
}