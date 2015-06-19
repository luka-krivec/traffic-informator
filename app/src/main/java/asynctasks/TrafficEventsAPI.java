package asynctasks;


import android.content.Context;
import android.net.Uri;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrafficEventsAPI {

    private static final int TIMEOUT = 5000;  // Timeout for waiting API call


    public static String getTraffic(Context context) {
        String res = "";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("opendata.si")
                .appendPath("promet")
                .appendPath("events/");

        try {
            res = new ApiCall(builder.build().toString(), "POST", TIMEOUT, context).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.getLogger("TrafficEventsAPI").log(Level.SEVERE, null, e);
        }

        return res;
    }

}
