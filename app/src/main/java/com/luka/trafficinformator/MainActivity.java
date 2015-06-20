package com.luka.trafficinformator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import asynctasks.ApiCall;
import asynctasks.DirectionsAPI;
import asynctasks.TrafficEventsAPI;
import utils.IOUtils;

public class MainActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks {

    private static final float TOLERANCE = 100.0f;  // Tolerance for searching traffic event on route

    private GoogleMap mMap;
    private EditText editFromLocation;
    private EditText editToLocation;
    private Button btnSearchEvents;
    private Button btnDisplayEvents;
    private ArrayList<Event> trafficEvents;
    private LatLng[] routePoints;
    private PolylineOptions optimalRoute;
    private GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editFromLocation = (EditText) findViewById(R.id.editFromLocation);
        editToLocation = (EditText) findViewById(R.id.editToLocation);
        btnSearchEvents = (Button) findViewById(R.id.btnSearchEvents);
        btnSearchEvents.setOnClickListener(this);
        trafficEvents = getTrafficEvents();
        btnDisplayEvents = (Button) findViewById(R.id.btnDisplayEvents);
        btnDisplayEvents.setOnClickListener(this);

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        setUpMapIfNeeded();

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey("optimalRoute")
                    && savedInstanceState.containsKey("routePoints")) {
                optimalRoute = savedInstanceState.getParcelable("optimalRoute");
                routePoints = (LatLng[]) savedInstanceState.getParcelableArray("routePoints");
                addDirectionOnMap(optimalRoute, routePoints);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("optimalRoute", optimalRoute);
        outState.putParcelableArray("routePoints", routePoints);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * Zoom to Slovenia and add traffic events to map
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        moveCamera(new LatLng(46.059231, 14.826602), 7f);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.marker, null);
                TextView info = (TextView) v.findViewById(R.id.info);
                info.setText(marker.getTitle());
                return v;
            }
        });
        addTrafficEvents(trafficEvents);
    }

    private void moveCamera(LatLng point, float zoom) {
        CameraPosition startCameraPosition = new CameraPosition.Builder()
                .target(point)
                .zoom(zoom)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(startCameraPosition));
    }

    /**
     * Parse traffic events from opendata API
     * @return
     */
    private ArrayList<Event> getTrafficEvents() {
        ArrayList<Event> events = new ArrayList<>();
        String response = TrafficEventsAPI.getTraffic(this);

        if(ApiCall.isCallSuccessfull(response)) {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray eventsArray = json.getJSONObject("dogodki").getJSONArray("dogodek");
                for(int i = 0; i < eventsArray.length(); i++) {
                    JSONObject event = eventsArray.getJSONObject(i);
                    events.add(new Event(event.getString("icon"), event.getString("cesta"),
                            event.getString("vzrok"),event.getString("opis"),
                            event.getDouble("y_wgs"), event.getDouble("x_wgs")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_retreiving_data), Toast.LENGTH_LONG).show();
            Log.d("TrafficEvents API error", response);
        }
        return events;
    }

    /**
     * Add markers that shows traffic events
     */
    private void addTrafficEvents(ArrayList<Event> trafficEvents) {
        HashMap<String, Bitmap> cacheIcons = new HashMap<>();

        for(Event event : trafficEvents) {
            String iconUrl = event.getIconUrl();
            Bitmap icon;
            boolean notCached = false;
            if(cacheIcons.containsKey(iconUrl)) {
                icon = cacheIcons.get(iconUrl);
            } else {
                notCached = true;
                icon = IOUtils.getImageBitmap(event.getIconUrl(), this);
            }
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(event.getLat(), event.getLng()))
                    .title(event.getDescription());

            if(icon != null) {
                marker.icon(BitmapDescriptorFactory.fromBitmap(icon));
                if(notCached) {
                    cacheIcons.put(iconUrl, icon);
                }

            }
            mMap.addMarker(marker);
        }
    }

    private void showEventsOnRoute(LatLng[] routePoints) {
        ArrayList<Event> trafficEventsOnRoute = new ArrayList<>();

        for(Event event : trafficEvents) {
            LatLng eventPoint = new LatLng(event.getLat(), event.getLng());
            if(PolyUtil.isLocationOnPath(eventPoint, Arrays.asList(routePoints), false, TOLERANCE)) {
                trafficEventsOnRoute.add(event);
            }
        }
        addTrafficEvents(trafficEventsOnRoute);
    }

    /**
     * Draw route on map between two locations
     * @param from Start location
     * @param to End location
     */
    private void drawDirections(String from, String to) {
        String warning = getResources().getString(R.string.warning_check_input_data);
        String error = getResources().getString(R.string.error_retreiving_data);

        optimalRoute = new PolylineOptions();

        if(from.length() == 0 || to.length() == 0) {
            Toast.makeText(this, R.string.warning_input_from_and_to, Toast.LENGTH_SHORT).show();
        } else {
            try {
                String apiResponse = DirectionsAPI.getDirections(from, to, this);

                if(ApiCall.isCallSuccessfull(apiResponse)) {
                    JSONObject jsonDirections = new JSONObject(apiResponse);
                    String status = jsonDirections.getString("status");
                    Log.d("DirectionsAPI status: ", status);

                    if(status.equals("OK")) {
                        JSONArray routes = jsonDirections.getJSONArray("routes");

                        if(routes.length() == 0) {
                            Toast.makeText(this, warning, Toast.LENGTH_LONG).show();
                        } else {
                            JSONObject route = routes.getJSONObject(0);
                            String polyLine = route.getJSONObject("overview_polyline").getString("points");
                            ArrayList<LatLng> points = decodePoly(polyLine);
                            routePoints = points.toArray(new LatLng[points.size()]);
                            optimalRoute.addAll(points);
                            addDirectionOnMap(optimalRoute, routePoints);
                        }
                    } else if(status.equals("NOT_FOUND")) {
                        Toast.makeText(this, warning, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.error_retreiving_data), Toast.LENGTH_LONG).show();
                    Log.d("Directions API error", apiResponse);
                }
            } catch (JSONException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addDirectionOnMap(PolylineOptions optimalRoute, LatLng[] points) {
        mMap.clear();
        mMap.addPolyline(optimalRoute);
        moveCamera(points[0], 10f);
        showEventsOnRoute(points);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSearchEvents) {
            String from = editFromLocation.getText().toString();
            String to = editToLocation.getText().toString();
            drawDirections(from, to);
        } else if(v.getId()== R.id.btnDisplayEvents) {
            Intent intentEvents = new Intent(this, EventsActivity.class);
            intentEvents.putParcelableArrayListExtra("events", trafficEvents);
            startActivity(intentEvents);
        }
    }

    /**
     * Decode array LatLNG points from API reponse field polyline
     * Algorithm description: https://developers.google.com/maps/documentation/utilities/polylinealgorithm
     * @param encoded
     * @return
     */
    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Google Api Client connection suspended.");
    }
}
