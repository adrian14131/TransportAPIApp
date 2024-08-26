package pl.ozog.transportapiapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

import pl.ozog.transportapiapp.adapters.StopGroupAdapter;
import pl.ozog.transportapiapp.generator.Generator;
import pl.ozog.transportapiapp.model.Transport;
import pl.ozog.transportapiapp.model.stops.StopGroup;


public class StopsTabFragment extends Fragment {

    RecyclerView parentStopRV;

    File internalStorageDir;

    ArrayList<StopGroup> stopGroups;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stops_tab, container, false);

        internalStorageDir = getActivity().getDataDir();

        Transport transport = Generator.getTransportFromJson(internalStorageDir);
        stopGroups = transport.getStops();

        stopGroups = stopGroups.stream().sorted((o1, o2)->Collator.getInstance(new Locale("pl", "PL")).compare(o1.getName(), o2.getName())).collect(Collectors.toCollection(ArrayList::new));
        StopGroupAdapter stopGroupAdapter = new StopGroupAdapter(stopGroups, this.getLayoutInflater());
        parentStopRV = rootView.findViewById(R.id.stopGroupsRV);
        parentStopRV.setHasFixedSize(true);
        parentStopRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        parentStopRV.setAdapter(stopGroupAdapter);
        return rootView;
    }
}