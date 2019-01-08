package com.example.derga.droshed;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by glerou02 on 24/04/17.
 */

public class DownloadModel extends AsyncTask<DownloadModel.Entry, Integer, Boolean> {
    private Entry entry;

    class Entry{
        final String sheetname;
        final User utilisateur;
        final String address;
        final Activity activity;

        public Entry(String sheetname, User utilisateur,String address, Activity activity){
            this.sheetname = sheetname;
            this.utilisateur = utilisateur;
            this.activity = activity;
            this.address = address;
        }
    }

    // telecharge le modele
    protected Boolean doInBackground(final Entry... entries) {
        HttpURLConnection connection;
        entry = entries[0];
        try {
            SQLiteDatabase base = new DB(entry.activity).getWritableDatabase();
            if(SheetDAO.getVersionSheet(base, entry.sheetname, entry.utilisateur.getLogin()) != -2) {
                base.close();
                return true;
            }
            URL url = new URL("http://"+entry.address+ "/" + entry.sheetname + "/model");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty  ("Authorization", "Basic " + entry.utilisateur.encode64());
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode == 401)
                return false;
            InputStream stream = connection.getInputStream();
            InputStreamReader sr = new InputStreamReader(stream);
            SheetDAO.insertSheet(base, entry.sheetname, entry.utilisateur.getLogin());
            int idSheet = SheetDAO.getIDSheet(base, entry.sheetname, entry.utilisateur.getLogin());
            SheetDAO.insertTypeCell(base, idSheet, new TypeCell("String", true, null, null, 0, idSheet));
            SheetDAO.insertTypeCell(base, idSheet, new TypeCell("String", false, null, null, -1, idSheet));
            SheetDAO.insertCell(base, idSheet, 0, 0, "/", -1, 0);
            boolean wellParsing = XMLParcer.parseModelXML(base, idSheet, sr, entry);
            SheetDAO.insertVirginSheet(base, idSheet);
            base.close();
            if(!wellParsing) {
                Log.d("", "error in parsing " + entry.sheetname + "");
                return false;
            }
            return true;
        }
        catch (MalformedURLException e) {}
        catch (ProtocolException e1) {}
        catch (IOException e1) {}
        return false;
    }

    // lance le telechargement des données correspondant au modele
    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if(!success) {
            Handler handler =  new Handler(entry.activity.getMainLooper());
            handler.post(new Runnable(){
                public void run(){
                    Toast.makeText(entry.activity, entry.sheetname + " : un problème est survenu", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        DownloadData asyncTaskData = new DownloadData();
        DownloadData.Entry entryD = asyncTaskData.new Entry(entry.sheetname, entry.utilisateur,entry.address, entry.activity);
        asyncTaskData.execute(entryD);
    }
}