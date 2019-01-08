package com.example.derga.droshed;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by gwenael on 04/06/17.
 */

public class PutData extends AsyncTask<PutData.Entry, Integer, Boolean> {
    private Entry entry;

    class Entry{
        final String sheetname;
        final int IDSheet;
        final User utilisateur;
        final String address;
        final String XML;
        final int idVersionSheet;
        final Context cnt;

        public Entry(int IDSheet, String sheetname, User utilisateur,String address, String XML, int idVersionSheet, Context cnt){
            this.IDSheet = IDSheet;
            this.sheetname = sheetname;
            this.utilisateur = utilisateur;
            this.address = address;
            this.XML = XML;
            this.idVersionSheet = idVersionSheet;
            this.cnt = cnt;
        }
    }

    // envoie les données sur le serveur
    protected Boolean doInBackground(final Entry... entries) {
        HttpURLConnection connection;
        entry = entries[0];
        try {
            Log.d("test",Integer.toString(entry.idVersionSheet));
            URL url = new URL("http://"+entry.address+ "/" + entry.sheetname + "/data/" + (entry.idVersionSheet));
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "text/plain; ");
            connection.setRequestProperty("Authorization", "Basic " + entry.utilisateur.encode64());
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(entry.XML);
            writer.close();
            return true;
        }
        catch (MalformedURLException e) {}
        catch (ProtocolException e1) {}
        catch (IOException e1) {}
        return false;
    }

    // met a jour la bdd
    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        SQLiteDatabase base = new DB(entry.cnt).getWritableDatabase();
        SheetDAO.setLastVersionGet(base, entry.IDSheet, (entry.idVersionSheet));
        base.close();
    }
}
