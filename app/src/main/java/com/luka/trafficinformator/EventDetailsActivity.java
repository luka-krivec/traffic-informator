package com.luka.trafficinformator;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import utils.IOUtils;


public class EventDetailsActivity extends ActionBarActivity {

    private Event event;
    public TextView textViewRoute;
    public TextView textViewReason;
    public TextView textViewDescription;
    public ImageView imageViewIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        textViewRoute = (TextView) findViewById(R.id.txtRoute);
        textViewReason = (TextView) findViewById(R.id.txtReason);
        textViewDescription = (TextView) findViewById(R.id.txtDescription);
        imageViewIcon = (ImageView) findViewById(R.id.imgIcon);

        event = getIntent().getExtras().getParcelable("event");

        textViewRoute.setText(event.getRoute());
        textViewReason.setText(event.getReason());
        textViewDescription.setText(event.getDescription());
        imageViewIcon.setImageBitmap(IOUtils.getImageBitmap(event.getIconUrl(), this));
    }
}
