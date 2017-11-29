package imt.logofinder.http;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 41000440 on 29/11/2017.
 */

public class HttpRequest extends AsyncTask<String, Integer, String> {

    private Integer tag = null;

    public HttpRequest(Integer tag) {
        this.tag = tag;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection http = null;
        StringBuilder retour = null;
        try {
            URL url = new URL(strings[0]);
            http = (HttpURLConnection) url.openConnection();

            InputStream inputStream = http.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            retour = new StringBuilder();

            int data = reader.read();
            while(data != -1) {
                retour.append((char) data);
                data = reader.read();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(http != null) http.disconnect();
        }

        return retour.toString();
    }
}
