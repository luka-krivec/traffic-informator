package adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luka.trafficinformator.Event;
import com.luka.trafficinformator.EventDetailsActivity;
import com.luka.trafficinformator.R;

import utils.IOUtils;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context context;
    private int rowLayout;
    Event[] objects;

    public EventsAdapter(Context context, int rowLayout, Event[] objects) {
        this.context = context;
        this.rowLayout = rowLayout;
        this.objects = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = objects[position];
        holder.textViewRoute.setText(event.getRoute());
        holder.textViewReason.setText(event.getReason());
        holder.imageViewIcon.setImageBitmap(IOUtils.getImageBitmap(event.getIconUrl(), context));
        holder.event = event;
    }

    @Override
    public int getItemCount() {
        return objects == null ? 0 : objects.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textViewRoute;
        public TextView textViewReason;
        public ImageView imageViewIcon;
        public Event event;

        public ViewHolder(View v) {
            super(v);
            textViewRoute = (TextView) v.findViewById(R.id.txtRoute);
            textViewReason = (TextView) v.findViewById(R.id.txtReason);
            imageViewIcon = (ImageView) v.findViewById(R.id.imgIcon);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intentEventDetails = new Intent(context, EventDetailsActivity.class);
            intentEventDetails.putExtra("event", event);
            context.startActivity(intentEventDetails);
        }
    }
}
