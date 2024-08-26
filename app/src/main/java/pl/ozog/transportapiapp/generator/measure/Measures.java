package pl.ozog.transportapiapp.generator.measure;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Measures {
    ArrayList<Measure> measures = new ArrayList<>();

    public void addMeasures(Measure measure){
        measures.add(measure);
    }
}
