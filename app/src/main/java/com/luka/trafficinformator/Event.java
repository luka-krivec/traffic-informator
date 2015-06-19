package com.luka.trafficinformator;


import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {

    private String iconUrl;
    private String route;
    private String reason;
    private String description;
    private double lat;
    private double lng;

    public Event(String iconUrl, String route, String reason, String description, double lat, double lng) {
        this.iconUrl = iconUrl;
        this.route = route;
        this.reason = reason;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
    }

    public Event(Parcel in) {
        this.iconUrl = in.readString();
        this.route = in.readString();
        this.reason = in.readString();
        this.description = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getRoute() {
        return route;
    }

    public String getReason() {
        return reason;
    }

    public String getDescription() {
        return description;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iconUrl);
        dest.writeString(route);
        dest.writeString(reason);
        dest.writeString(description);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
