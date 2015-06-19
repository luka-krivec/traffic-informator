package asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.luka.trafficinformator.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.IOUtils;

public class ApiCall extends AsyncTask<Void, String, String> {

    public static final String NOT_FOUND = "not found";
    public static final String SERVER_DOWN = "server down";

    private String url;
    private String method;
    private Context context;
    private int timeout;

    public ApiCall(String url, String method, int timeout, Context context) {
        this.url = url;
        this.method = method;
        this.context = context;
        this.timeout = timeout;
    }

    @Override
    protected String doInBackground(Void... params) {
        String res = "";
        String error = context.getResources().getString(R.string.error_retreiving_data);
        HttpURLConnection c = null;

        try  {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod(method);
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    return IOUtils.getString(c.getInputStream());
                case 404:
                    return NOT_FOUND;
                case 500:
                    return SERVER_DOWN;
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            res = error;
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    res = error;
                }
            }
        }
        return res;
    }

    public static boolean isCallSuccessfull(String response) {
        return !(response.equals(NOT_FOUND) || response.equals(SERVER_DOWN)) && response.length() > 0;
    }
}
