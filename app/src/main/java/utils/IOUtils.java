package utils;


import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import asynctasks.GetImageBitmap;

public class IOUtils {

    public static String getString(InputStream is) {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total.toString();
    }

    public static Bitmap getImageBitmap(String url) {
        android.graphics.Bitmap bitmap = null;
        try {
            bitmap =  new GetImageBitmap().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.getLogger("IOUtils getImageBitmap").log(Level.SEVERE, null, e);
        }
        return  bitmap;
    }
}
