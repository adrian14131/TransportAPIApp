package pl.ozog.transportapiapp.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import lombok.Setter;
import pl.ozog.transportapiapp.R;
import pl.ozog.transportapiapp.generator.loaders.ttss.StopsLoader;
import pl.ozog.transportapiapp.model.lines.Line;
import pl.ozog.transportapiapp.model.lines.Route;
import pl.ozog.transportapiapp.model.stops.StopGroup;
import pl.ozog.transportapiapp.model.stops.StopPoint;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewItemHolder>{

    @Setter
    ItemListener itemListener;
    private ArrayList<Route> routes;
    private LayoutInflater layoutInflater;

    public RouteAdapter(ArrayList<Route> routes, LayoutInflater layoutInflater) {
        this.routes = routes;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public ViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.route_element_view, parent, false);
        return new ViewItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewItemHolder holder, int position) {
        Route route = routes.get(position);
        holder.setData(route, position);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder{

        TextView title;
        int position;
        Route route;

        public ViewItemHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.routeElementTextView);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onRouteChoose(route);
                }
            });
        }

        @SuppressLint("DefaultLocale")
        public void setData(Route route, int position){
            this.route = route;
            this.position = position;
            String firstStopName = getStopName(!route.getStopsShortIds().isEmpty() ?route.getStopsShortIds().get(0):"");
            String lastStopName = getStopName(!route.getDirectionId().isEmpty()?route.getDirectionId():"");
            title.setText(String.format("%d. %s -> %s", route.getRouteNumber(), firstStopName, lastStopName));
        }

        private String getStopName(String shortId){
            for(StopGroup stopGroup: LineAdapter.stopGroups){
                if(stopGroup.hasShortId(shortId))
                    return stopGroup.getName();
            }
            StopPoint stopPoint = StopsLoader.getStopPointByShortId(LineAdapter.stopGroups, shortId);
            return stopPoint!=null?stopPoint.getName():shortId;
        }
    }
    public interface ItemListener{
        void onRouteChoose(Route route);
    }

}
