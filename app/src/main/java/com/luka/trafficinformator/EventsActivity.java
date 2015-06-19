package com.luka.trafficinformator;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import adapters.EventsAdapter;


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
        Event[] events = eventsList.toArray(new Event[eventsList.size()]);
        EventsAdapter eventsAdapter = new EventsAdapter(this, R.layout.row_event, events);
        recyclerViewListEvents.setAdapter(eventsAdapter);
    }
}
