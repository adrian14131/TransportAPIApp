package pl.ozog.transportapiapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pl.ozog.transportapiapp.R;
import pl.ozog.transportapiapp.model.stops.StopPoint;

public class StopPointAdapter extends RecyclerView.Adapter<StopPointAdapter.ViewItemHolder> {


    private ArrayList<StopPoint> stopPoints;
    private LayoutInflater layoutInflater;


    public StopPointAdapter(ArrayList<StopPoint> stopPoints, LayoutInflater layoutInflater) {
        this.stopPoints = stopPoints;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public ViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.stop_point_element_view, parent, false);
        return new ViewItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewItemHolder holder, int position) {
        StopPoint currentStopPoint = stopPoints.get(position);
        holder.setData(currentStopPoint, position);
    }

    @Override
    public int getItemCount() {
        return stopPoints.size();
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder{

        TextView category, title;

        StopPoint stopPoint;
        int position;
        public ViewItemHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.stopPointCategoryTextView);
            title = itemView.findViewById(R.id.stopPointTitleTextView);


        }

        public void setData(StopPoint stopPoint, int position){
            title.setText(stopPoint.getName());
            category.setText(stopPoint.getCategory().equals("tram")?"(T)":stopPoint.getCategory().equals("bus")?"(A)":"");
            this.position = position;
            this.stopPoint = stopPoint;

        }

    }
}
