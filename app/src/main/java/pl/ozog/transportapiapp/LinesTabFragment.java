package pl.ozog.transportapiapp;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

import pl.ozog.transportapiapp.adapters.LineAdapter;
import pl.ozog.transportapiapp.adapters.RouteAdapter;
import pl.ozog.transportapiapp.adapters.RouteStopAdapter;
import pl.ozog.transportapiapp.generator.Generator;
import pl.ozog.transportapiapp.model.Transport;
import pl.ozog.transportapiapp.model.lines.Line;
import pl.ozog.transportapiapp.model.lines.Route;
import pl.ozog.transportapiapp.model.stops.StopGroup;


public class LinesTabFragment extends Fragment implements RouteAdapter.ItemListener {

    ArrayList<Line> lines;
    ArrayList<StopGroup> stopGroups;
    RecyclerView lineRecyclerView, routeStopRecyclerView;
    ConstraintLayout routeDetailsLayout;

    File internalStorageDir;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_lines_tab, container, false);


        internalStorageDir = getActivity().getDataDir();

        Transport transport = Generator.getTransportFromJson(internalStorageDir);
        lines = transport.getLines();
        stopGroups = transport.getStops();

        LineAdapter lineAdapter = new LineAdapter(lines, stopGroups, this.getLayoutInflater(), this);
        lineRecyclerView = rootView.findViewById(R.id.linesRV);
        lineRecyclerView.setHasFixedSize(true);
        lineRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        lineRecyclerView.setAdapter(lineAdapter);

        routeStopRecyclerView = rootView.findViewById(R.id.routeStopsRV);
        routeStopRecyclerView.setHasFixedSize(true);
        routeStopRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        routeDetailsLayout = rootView.findViewById(R.id.lineTabRouteDetailsLayout);
        routeDetailsLayout.setVisibility(View.GONE);
//        routeDetailsLayout.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onRouteChoose(Route route) {
        routeDetailsLayout.setVisibility(View.VISIBLE);
        RouteStopAdapter routeStopAdapter = new RouteStopAdapter(route, stopGroups, this.getLayoutInflater());
        routeStopRecyclerView.setAdapter(routeStopAdapter);
    }
}