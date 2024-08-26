package pl.ozog.transportapiapp.adapters;

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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

import pl.ozog.transportapiapp.R;
import pl.ozog.transportapiapp.model.stops.StopGroup;
import pl.ozog.transportapiapp.model.stops.StopPoint;

public class StopGroupAdapter extends RecyclerView.Adapter<StopGroupAdapter.ViewItemHolder> {

    private ArrayList<StopGroup> stopGroups;
    private LayoutInflater layoutInflater;
    public StopGroupAdapter(ArrayList<StopGroup> stopGroups, LayoutInflater layoutInflater) {
        this.stopGroups = stopGroups;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public ViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.stop_group_element_view, parent, false);
        return new ViewItemHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewItemHolder holder, int position) {
        StopGroup currentStopGroup = stopGroups.get(position);
        holder.setData(currentStopGroup, position);
    }


    @Override
    public int getItemCount() {
        return stopGroups.size();
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder {

        TextView title;
        RecyclerView recyclerView;
        ConstraintLayout titleLayout;
        ImageView arrowImage;
        StopGroup stopGroup;
        int position;
        public ViewItemHolder(@NonNull View itemView) {
            super(itemView);
            title =  itemView.findViewById(R.id.stopGroupNameTextView);

            titleLayout = itemView.findViewById(R.id.stopGroupTitleLayout);
            titleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expand(recyclerView.getVisibility() != View.VISIBLE);
                }
            });

            arrowImage = itemView.findViewById(R.id.stopGroupArrowImage);

            recyclerView = itemView.findViewById(R.id.stopPointsRV);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(layoutInflater.getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setVisibility(View.GONE);

        }
        public void setData(StopGroup stopGroup, int position){
            title.setText(stopGroup.getName());
            this.position = position;
            this.stopGroup = stopGroup;

            StopPointAdapter spa = new StopPointAdapter(stopGroup.getStopPoints().stream()
                    .sorted((o1, o2) -> Collator.getInstance(new Locale("pl", "PL"))
                            .compare(o1.getName(), o2.getName()))
                    .collect(Collectors.toCollection(ArrayList::new))
                    ,layoutInflater);
            recyclerView.setAdapter(spa);

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
    }
}
