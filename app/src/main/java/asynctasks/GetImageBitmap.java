package asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GetImageBitmap extends AsyncTask<String, String, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            InputStream is = (InputStream) new URL(params[0]).getContent();
            Bitmap d = BitmapFactory.decodeStream(is);
            is.close();
            return d;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

}
