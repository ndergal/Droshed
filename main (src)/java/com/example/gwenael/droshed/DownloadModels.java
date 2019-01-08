package com.example.derga.droshed;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by glerou02 on 24/04/17.
 */

public class DownloadModels extends AsyncTask<DownloadModels.Entry, Integer, ArrayList<String>> {
    private Entry entry;

    class Entry{
        final User utilisateur;
        final Activity activity;
        final String address;
        public Entry(User utilisateur, String address, Activity activity){
            this.utilisateur = utilisateur;
            this.activity = activity;
            this.address = address;
        }
    }

    // telecharge les modeles disponibles
    protected ArrayList<String> doInBackground(Entry... entries) {
        HttpURLConnection connection;
        entry = entries[0];
        try {
            URL url = new URL("http://"+entry.address+"/models/model"); //10.0.2.2
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty  ("Authorization", "Basic " + entry.utilisateur.encode64());
            connection.connect();
            if(connection.getResponseCode() == 401)
                return null;
            InputStream stream = connection.getInputStream();
            InputStreamReader sr = new InputStreamReader(stream);
            ArrayList<String> list = new ArrayList<>();
            if(!XMLParcer.parseModelsXML(list, sr)) {
                Log.d("", "error in parsing models.xml");
                return null;
            }
            return list;
        }
        catch (MalformedURLException e) {}
        catch (ProtocolException e1) {}
        catch (IOException e1) {}
        return null;
    }

    // retour a l'activité principal avec le user et les modeles
    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        Intent intent = new Intent();
        if(strings == null){
            entry.activity.setResult(RESULT_CANCELED, intent);
            entry.activity.finish();
        }
        else {
            intent.putExtra("user", entry.utilisateur);
            intent.putExtra("models", strings);
            intent.putExtra("address",entry.address);
            entry.activity.setResult(RESULT_OK, intent);
            entry.activity.finish();
        }
    }
}