package imt.logofinder.http;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Nico on 10/04/2018.
 */

public class CustomPostRequest extends AsyncTask<String, Integer, String> {
    /**
     *
     * @param strings 0 : HOST, 1 : PORT, 2 : URL, 3 : Paramètres
     * @return String
     */
    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection http = null;
        String retour = null;
        try{
            URL url = new URL("http", strings[0], Integer.valueOf(strings[1]), strings[2]);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setFixedLengthStreamingMode(strings[3].getBytes().length);
            //http.setUseCaches (false);
            http.setDoInput(true);
            http.setDoOutput(true);


            //Envois de la requete

            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(http.getOutputStream (), "UTF-8"));
            writer.write(strings[3]);
            writer.flush();
            writer.close();
            retour = String.valueOf(http.getResponseCode());
            return retour;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(http != null) http.disconnect();
        }

        return null;
    }
}
