package asynctasks;


import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.IOUtils;

public class DirectionsAPI extends AsyncTask<String, String, String> {

    private static final int TIMEOUT = 5000;  // Timeout for waiting API call
    private static final String API_KEY = "AIzaSyDKAUuawMOLkQ0wFkkFTU-OuTyfmNK6w_M";

    @Override
    protected String doInBackground(String... params) {
        String res = "";
        String origin = params[0];
        String destination = params[1];

        HttpURLConnection c = null;
        try  {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("maps.googleapis.com")
                    .appendPath("maps").appendPath("api")
                    .appendPath("directions").appendPath("json")
                    .appendQueryParameter("origin", origin)
                    .appendQueryParameter("destination", destination)
                    .appendQueryParameter("key", API_KEY);

            URL u = new URL(builder.build().toString());
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(TIMEOUT);
            c.setReadTimeout(TIMEOUT);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                    JSONObject json = new JSONObject(IOUtils.getString(c.getInputStream()));
                    res = json.toString();
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }

}
