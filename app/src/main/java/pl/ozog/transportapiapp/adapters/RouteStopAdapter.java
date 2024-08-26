package pl.ozog.transportapiapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pl.ozog.transportapiapp.R;
import pl.ozog.transportapiapp.generator.loaders.ttss.StopsLoader;
import pl.ozog.transportapiapp.model.lines.Route;
import pl.ozog.transportapiapp.model.stops.StopGroup;
import pl.ozog.transportapiapp.model.stops.StopPoint;

public class RouteStopAdapter extends RecyclerView.Adapter<RouteStopAdapter.ViewItemHolder>{

    private Route route;
    private LayoutInflater layoutInflater;
    private ArrayList<StopGroup> stopGroups;

    public RouteStopAdapter(Route route, ArrayList<StopGroup> stopGroups, LayoutInflater layoutInflater) {
        this.route = route;
        this.layoutInflater = layoutInflater;
        this.stopGroups = stopGroups;
    }

    @NonNull
    @Override
    public ViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.route_stop_element_view, parent, false);
        return new ViewItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewItemHolder holder, int position) {
        for(StopGroup stopGroup: stopGroups){
            if(stopGroup.hasShortId(route.getStopsShortIds().get(position))){
                holder.setStopGroup(stopGroup, position);
                return;
            }

        }
        holder.setStopPoint(StopsLoader.getStopPointByShortId(stopGroups, route.getStopsShortIds().get(position)), position);
    }

    @Override
    public int getItemCount() {
        return route.getStopsShortIds().size();
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder{

        TextView title;
        StopGroup stopGroup;
        StopPoint stopPoint;
        boolean isStopPoint;
        int position;
        public ViewItemHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.routeStopNameTextView);

        }

        public void setStopPoint(StopPoint stopPoint, int position){
            setData(stopPoint, null, true, position);
        }

        public void setStopGroup(StopGroup stopGroup, int position) {
            setData(null, stopGroup, false, position);
        }

        private void setData(StopPoint stopPoint, StopGroup stopGroup, boolean isStopPoint, int position){
            this.stopGroup = stopGroup;
            this.stopPoint = stopPoint;
            this.isStopPoint = isStopPoint;
            this.position = position;

            if(isStopPoint)
                title.setText(stopPoint!=null?stopPoint.getName():route.getStopsShortIds().get(position));
            else
                title.setText(stopGroup.getName());
        }
    }

}
