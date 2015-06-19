package asynctasks;


import android.content.Context;
import android.net.Uri;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectionsAPI {

    private static final int TIMEOUT = 5000;  // Timeout for waiting API call
    private static final String API_KEY = "AIzaSyDKAUuawMOLkQ0wFkkFTU-OuTyfmNK6w_M";

    public static String getDirections(String origin, String destination, Context context) {
        String res = "";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps").appendPath("api")
                .appendPath("directions").appendPath("json")
                .appendQueryParameter("origin", origin)
                .appendQueryParameter("destination", destination)
                .appendQueryParameter("key", API_KEY);

        try {
            res = new ApiCall(builder.build().toString(), "GET", TIMEOUT, context).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.getLogger("DirectionsAPI").log(Level.SEVERE, null, e);
        }
        return res;
    }
}
