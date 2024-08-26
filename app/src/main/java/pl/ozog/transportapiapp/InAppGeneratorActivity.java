package pl.ozog.transportapiapp;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import org.json.JSONObject;

import java.io.File;
import java.time.LocalDateTime;

import pl.ozog.transportapiapp.generator.Generator;
import pl.ozog.transportapiapp.generator.measure.Measure;

public class InAppGeneratorActivity extends AppCompatActivity implements View.OnClickListener{

    TextView infoTextView, finalResultTextView;
    ProgressBar progressBar;

    Button startButton, nextButton;
    File internalStorageDir;
    String transportJsonStr;
    Long delay = 1000L;
    boolean started = false;
    String transportJsonPath = "transport.json";
    String measureJsonPath = "measures.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_in_app_generator);
        Bundle extras = getIntent().getExtras();
        internalStorageDir = getDataDir();
        Generator.internalDir = internalStorageDir;
//        Generator.callback = this;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        finalResultTextView = findViewById(R.id.inAppFinalResultTextView);

        infoTextView = findViewById(R.id.inAppInfoTextView);
        startButton = findViewById(R.id.inAppStartButton);
        startButton.setOnClickListener(this);

        nextButton = findViewById(R.id.inAppNextButton);
        nextButton.setOnClickListener(this);

        progressBar = findViewById(R.id.inAppProgressBar);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.inAppStartButton:
                infoTextView.setText("Start");
                startButton.setEnabled(false);
                waitProcess();
                break;
            case R.id.inAppNextButton:
                Intent apiIntent = new Intent(InAppGeneratorActivity.this, DataViewerActivity.class);
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
        double downloadSize = ((double) apiMeasure.getDownloadSize()) / (1024.0*1024.0);
        sb.append("Pobranych danych (B): ").append(apiMeasure.getDownloadSize()).append("B\n");
        sb.append("Pobranych danych (MB): ").append(downloadSize).append("MB\n");
        sb.append("Zapytań API: ").append(apiMeasure.getNumberOfRequests()).append("\n");
        finalResultTextView.setText(sb.toString());
    }

    private Measure getLastMeasure(){
        return Generator.lastMeasure != null? Generator.lastMeasure: new Measure(LocalDateTime.MIN, -1L, -1,-1,-1,-1,-1,-1,-1);
    }
    private void dataReady(long startTime, long stopTime){
        Generator.percent = 100;
        JSONObject jsonObject = Generator.serializeToJson(Generator.transport);
        transportJsonStr = jsonObject.toString();
        Generator.saveJsonToFile(transportJsonPath, jsonObject);
        infoTextView.setText("Dane są gotowe");
        started = false;
        new Handler().postDelayed(() -> {
            startButton.setEnabled(true);
            progressBar.setProgress(100);
            nextButton.setEnabled(true);

            showFinalInfo(stopTime-startTime, getLastMeasure());
        },500);
    }
    private void waitProcess(){
        if(!started){
            started = true;
            long startTime = System.nanoTime();
            progressBar.setProgress(0);
            Generator.percent = 0;
            Runnable rStartProcess = () -> {
                if(Generator.isFileExists(transportJsonPath)){
                    if(!Generator.loaded){
                        Generator.transport = Generator.deserializeFromJson(Generator.readJsonFromFile(transportJsonPath));
                        if(Generator.transport != null){
                            if(!Generator.transport.getLines().isEmpty() && !Generator.transport.getStops().isEmpty()){
                                Generator.isReady = true;
                            }
                            Generator.loaded = true;
                        }
                    }
                }
                if(Generator.canGenerate()){
                    Generator.percent = 0;
                    Generator.startProcess();
                }

            };
            new Thread(rStartProcess).start();
            Handler statusHandler = new Handler(Looper.getMainLooper());
            Runnable rStatusUpdater = new Runnable() {
                @Override
                public void run() {
                    if(started){

                        infoTextView.setText("Generowanie danych na serwerze: "+Generator.percent+"%");
                        progressBar.setProgress((int) Generator.percent);
                        statusHandler.postDelayed(this, delay);
                        if(Generator.loaded){
                            dataReady(startTime, System.nanoTime());
                        }
                    }
                }
            };
            statusHandler.postDelayed(rStatusUpdater, delay);
        }
    }

//    @Override
//    public void onStatusChange() {
////        Log.e("TTT", "onStatusChange: ");
////        Handler statusHandler = new Handler();
////        statusHandler.post(()->{
////            Log.e("TTT", "onStatusChange: ");
////            infoTextView.setText("Generowanie danych na serwerze: "+Generator.percent+"%");
////            progressBar.setProgress((int) Generator.percent);
////        });
//    }
}