package pl.ozog.transportapiapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pl.ozog.transportapiapp.R;
import pl.ozog.transportapiapp.generator.loaders.ttss.StopsLoader;
import pl.ozog.transportapiapp.model.lines.Line;
import pl.ozog.transportapiapp.model.lines.Route;
import pl.ozog.transportapiapp.model.stops.StopGroup;
import pl.ozog.transportapiapp.model.stops.StopPoint;

public class LineAdapter extends RecyclerView.Adapter<LineAdapter.ViewItemHolder>{

    public static ArrayList<StopGroup> stopGroups;
    private ArrayList<Line> lines;
    private LayoutInflater layoutInflater;
    private RouteAdapter.ItemListener itemListener;
    public LineAdapter(ArrayList<Line> lines, ArrayList<StopGroup> stopGroups, LayoutInflater layoutInflater, RouteAdapter.ItemListener itemListener) {
        LineAdapter.stopGroups = stopGroups;
        this.lines = lines;
        this.layoutInflater = layoutInflater;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.line_element_view, parent, false);
        return new ViewItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewItemHolder holder, int position) {
        Line line = lines.get(position);
        holder.setData(line, position);
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder{

        TextView title, moreInfo;
        RecyclerView recyclerView;
        ConstraintLayout titleLayout;
        ImageView arrowImage;
        Line line;
        int position;
        public ViewItemHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.lineNameTextView);
            moreInfo = itemView.findViewById(R.id.lineMoreInfoTextView);
            titleLayout = itemView.findViewById(R.id.lineElementTitleLayout);
            titleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expand(recyclerView.getVisibility() != View.VISIBLE);
                }
            });

            arrowImage = itemView.findViewById(R.id.lineArrowImage);

            recyclerView = itemView.findViewById(R.id.routeRV);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(layoutInflater.getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setVisibility(View.GONE);
        }

        public void setData(Line line, int position){
            title.setText(line.getLine());
            moreInfo.setText(getFullLineName(line));
            this.line = line;
            this.position = position;

            RouteAdapter ra = new RouteAdapter(line.getRoutes(), layoutInflater);
            ra.setItemListener(itemListener);
            recyclerView.setAdapter(ra);
        }
        public void expand(boolean toExpand){
            if(toExpand)
                recyclerView.setVisibility(View.VISIBLE);
            else
                recyclerView.setVisibility(View.GONE);
            setArrow(!toExpand);

        }
        public void setArrow(boolean up){
            if(up)
                arrowImage.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), android.R.drawable.arrow_up_float, itemView.getContext().getTheme()));
            else
                arrowImage.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), android.R.drawable.arrow_down_float, itemView.getContext().getTheme()));
        }

        public String getFullLineName(Line line){
            StringBuilder result = new StringBuilder("");

            String middle = line.getRoutes().size()>1?" <-> ":" -> ";

            if(!line.getRoutes().isEmpty()){
                String firstStopName = getStopName(!line.getRoutes().get(0).getStopsShortIds().isEmpty() ?line.getRoutes().get(0).getStopsShortIds().get(0):"");
                String lastStopName = getStopName(!line.getRoutes().get(0).getDirectionId().isEmpty() ?line.getRoutes().get(0).getDirectionId():"");
                result.append(firstStopName).append(middle).append(lastStopName);
            }
            return result.toString();
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


}
