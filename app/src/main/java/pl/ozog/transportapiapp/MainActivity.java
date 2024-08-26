package pl.ozog.transportapiapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import pl.ozog.transportapiapp.generator.Generator;
import pl.ozog.transportapiapp.model.Transport;
import pl.ozog.transportapiapp.tools.FileTools;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button apiWaitButton, generatorWaitButton, apiBackgroundButton, skipButton;
    String transportJsonPath = "transport.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiWaitButton = findViewById(R.id.apiWaitingButton);
        apiWaitButton.setOnClickListener(this);

        apiBackgroundButton = findViewById(R.id.apiBackgroundButton);
        apiBackgroundButton.setOnClickListener(this);
        
        generatorWaitButton = findViewById(R.id.inAppGenWaitingButton);
        generatorWaitButton.setOnClickListener(this);

        skipButton = findViewById(R.id.skipGeneratingButton);
        skipButton.setOnClickListener(this);

        boolean canSkip = false;
        if(FileTools.isFileExist(getDataDir(), transportJsonPath)){
            Transport transport = Generator.getTransportFromJson(getDataDir(), transportJsonPath);
            canSkip = transport != null;
        }
        skipButton.setEnabled(canSkip);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.apiWaitingButton:
                Intent apiIntent = new Intent(MainActivity.this, ApiGeneratorActivity.class);
                apiIntent.putExtra("background", false);
                startActivity(apiIntent);
                break;

            case R.id.inAppGenWaitingButton:
                Intent appIntent = new Intent(MainActivity.this, InAppGeneratorActivity.class);
                appIntent.putExtra("background", false);
                startActivity(appIntent);
                break;

            case R.id.skipGeneratingButton:
                Intent dataIntent = new Intent(MainActivity.this, DataViewerActivity.class);
                startActivity(dataIntent);
                break;


        }
    }
}