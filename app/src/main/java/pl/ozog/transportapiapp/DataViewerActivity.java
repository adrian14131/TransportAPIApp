package pl.ozog.transportapiapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.io.File;

import pl.ozog.transportapiapp.adapters.DataViewerTabsPagerAdapter;
import pl.ozog.transportapiapp.generator.Generator;
import pl.ozog.transportapiapp.model.Transport;

public class DataViewerActivity extends AppCompatActivity {

    TabLayout tabLayout;

    ViewPager2 viewPager;
    DataViewerTabsPagerAdapter viewPagerAdapter;

    File internalStorageDir;


    Transport transport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        internalStorageDir = getDataDir();

        transport = Generator.getTransportFromJson(internalStorageDir);

        tabLayout = findViewById(R.id.dataTabLayout);
        viewPager = findViewById(R.id.viewPager);

        viewPagerAdapter = new DataViewerTabsPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

    }

//    public static Transport getTransportFromJson(File internalStorageDir, String filePath){
//        JSONObject transportJson = FileTools.loadJsonFromFile(internalStorageDir, filePath);
//        Transport result = new Transport();
//        if(transportJson!=null){
//            result = Generator.deserializeFromJson(transportJson);
//        }
//        return result;
//    }
//
//    public static Transport getTransportFromJson(File internalStorageDir){
//        return getTransportFromJson(internalStorageDir, transportJsonPath);
//    }
}