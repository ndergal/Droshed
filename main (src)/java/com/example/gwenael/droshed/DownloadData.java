package com.example.derga.droshed;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
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

public class DownloadData extends AsyncTask<DownloadData.Entry, Integer, Boolean> {
    private Entry entry;

    class Entry{
        final Activity activity;
        final String sheetname;
        final User utilisateur;
        final String address;

        public Entry(String sheetname, User utilisateur,String address, Activity activity){
            this.sheetname = sheetname;
            this.utilisateur = utilisateur;
            this.activity = activity;
            this.address = address;
        }
    }

    protected Boolean doInBackground(Entry... entries) {
        HttpURLConnection connection;
        entry = entries[0];
        //Récupération du numéro de version max du serveur
        try {
            SQLiteDatabase base = new DB(entry.activity).getWritableDatabase();
            int versionSheetLocal = SheetDAO.getVersionSheet(base, entry.sheetname, entry.utilisateur.getLogin());
            URL url = new URL("http://"+entry.address+"/" + entry.sheetname + "/data/lastversion");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty  ("Authorization", "Basic " + entry.utilisateur.encode64());
            connection.connect();
            int idSheet = SheetDAO.getIDSheet(base, entry.sheetname, entry.utilisateur.getLogin());
            int responseCode = connection.getResponseCode();
            int maxVersion = -1;
            if(responseCode == 404){
                maxVersion = -1;
            }
            else {
                InputStream stream = connection.getInputStream();
                InputStreamReader sr = new InputStreamReader(stream);
                BufferedReader in = new BufferedReader(sr);
                String line;
                while ((line = in.readLine()) != null) {
                    maxVersion = Integer.valueOf(line);
                }
                SheetDAO.updateVersionSheetOldCells(base, idSheet, (maxVersion + 1));
            }
            Log.d("testMax version", Integer.toString(maxVersion));
            Log.d("testMax version local", Integer.toString(versionSheetLocal));
            for(int i = (versionSheetLocal + 1); i <= maxVersion; i++){
                url = new URL("http://"+entry.address+"/" + entry.sheetname + "/data/" + i);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty  ("Authorization", "Basic " + entry.utilisateur.encode64());
                connection.connect();
                InputStream stream = connection.getInputStream();
                InputStreamReader sr = new InputStreamReader(stream);
                boolean wellParsing = XMLParcer.parseDataXML(base, idSheet, i, sr, entry);
                if(!wellParsing) {
                    Log.d("", "error in parsing " + entry.sheetname + "");
                    return false;
                }
                SheetDAO.setLastVersionGet(base, idSheet, i);
            }
            base.close();
            return true;
        }
        catch (MalformedURLException e) {}
        catch (ProtocolException e1) {}
        catch (IOException e1) {}
        return false;
    }

    //affiche la sheet correspondante au données
    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if(!success) {
            Handler handler =  new Handler(entry.activity.getMainLooper());
            handler.post(new Runnable(){
                public void run(){
                    Toast.makeText(entry.activity, entry.sheetname + " : un problème est survenu", Toast.LENGTH_SHORT);
                }
            });
            return;
        }
        Intent intent = new Intent(entry.activity, SheetActivity.class);
        intent.putExtra("nameSheet", entry.sheetname);
        intent.putExtra("utilisateur", entry.utilisateur);
        intent.putExtra("pathDB", entry.activity.getDatabasePath("DroshedDB").getPath());
        intent.putExtra("addressS", entry.address);
        entry.activity.startActivityForResult(intent, 5);
    }
}