package asynctasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GetImageBitmap extends AsyncTask<String, String, Bitmap> {

    private Context context;

    public GetImageBitmap(Context context) {
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return Picasso.with(context).load(params[0]).get();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

}
