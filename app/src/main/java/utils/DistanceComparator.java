package utils;


import android.location.Location;

import com.luka.trafficinformator.Event;
import com.luka.trafficinformator.MainActivity;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Event> {

    @Override
    public int compare(Event e1, Event e2) {
        Location loc1 = new Location("loc1");
        loc1.setLatitude(e1.getLat());
        loc1.setLongitude(e1.getLng());
        Location loc2 = new Location("loc2");
        loc2.setLatitude(e2.getLat());
        loc2.setLongitude(e2.getLng());

        float distance1 = MainActivity.mLastLocation.distanceTo(loc1);
        float distance2 = MainActivity.mLastLocation.distanceTo(loc2);

        if(distance1 > distance2) {
            return 1;
        } else if(distance1 < distance2) {
            return -1;
        } else {
            return 0;
        }
    }

}
