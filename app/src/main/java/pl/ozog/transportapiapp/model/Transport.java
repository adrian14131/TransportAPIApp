package pl.ozog.transportapiapp.model;


import android.os.Build;

import java.time.LocalDate;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.ozog.transportapiapp.model.lines.Line;
import pl.ozog.transportapiapp.model.stops.StopGroup;

@AllArgsConstructor
@Getter
@Setter
public class Transport {

    LocalDate lastUpdate;
    ArrayList<Line> lines;
    ArrayList<StopGroup> stops;

    public Transport(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this.lastUpdate = LocalDate.EPOCH;
        }
            lines = new ArrayList<>();
            stops = new ArrayList<>();
    }
}
