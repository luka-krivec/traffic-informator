package com.luka.trafficinformator;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import adapters.EventsAdapter;
import utils.DistanceComparator;


public class EventsActivity extends ActionBarActivity {

    private RecyclerView recyclerViewListEvents;
    private RecyclerView.LayoutManager recyclerViewLinerLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        recyclerViewListEvents = (RecyclerView) findViewById(R.id.recyclerViewListEvents);
        recyclerViewListEvents.setHasFixedSize(true);

        recyclerViewLinerLayoutManager = new LinearLayoutManager(this);
        recyclerViewListEvents.setLayoutManager(recyclerViewLinerLayoutManager);

        ArrayList<Event> eventsList = getIntent().getParcelableArrayListExtra("events");
        Collections.sort(eventsList, new DistanceComparator());
        Event[] events = eventsList.toArray(new Event[eventsList.size()]);
        EventsAdapter eventsAdapter = new EventsAdapter(this, R.layout.row_event, events);
        recyclerViewListEvents.setAdapter(eventsAdapter);
    }
}
