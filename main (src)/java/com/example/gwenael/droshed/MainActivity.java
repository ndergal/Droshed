package com.example.derga.droshed;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private User utilisateur;
    private String address;
    private ArrayList<String> models = new ArrayList<>();
    private boolean sheetActivityLaunched = false;

    //Fonction appelée au démarrage de l'application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            initiateLogin();
        }
        ListView lv = (ListView) findViewById(R.id.modelsListView);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {
            @Override
            public int getCount() {
                return models.size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.modelofmodel, null);
                TextView tv = (TextView) convertView.findViewById(R.id.textViewModelOfModel);
                SQLiteDatabase base = new DB(MainActivity.this).getWritableDatabase();
                int IDSheet = SheetDAO.getIDSheet(base, models.get(position), utilisateur.getLogin());
                if(IDSheet == -1)
                    tv.setText(models.get(position));
                else
                    tv.setText(models.get(position) + " (Ouvert)");
                base.close();
                return convertView;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(sheetActivityLaunched == true)
                    return;
                sheetActivityLaunched = true;
                DownloadModel asyncTask = new DownloadModel();
                DownloadModel.Entry entry = asyncTask.new Entry(models.get(position), utilisateur,address, MainActivity.this);
                asyncTask.execute(entry);
            }
        });
    }

    //Lancement de l'authentification
    private void initiateLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 4);
    }

    // Fonction au retour de l'authentification
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 4) {
            if (resultCode != RESULT_OK) {
                initiateLogin();
                return;
            }
            utilisateur = data.getParcelableExtra("user");
            models = data.getStringArrayListExtra("models");
            address = data.getStringExtra("address");
            onSaveInstanceState(new Bundle());
            ListView lv = (ListView) findViewById(R.id.modelsListView);
            lv.setAdapter(lv.getAdapter());
        }
    }

    // restaure les données sauvegardées dans le bundle
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        utilisateur = savedInstanceState.getParcelable("utilisateur");
        models = savedInstanceState.getStringArrayList("models");
        address = savedInstanceState.getString("address");
    }

    // sauvegarde les données dans un bundle
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (utilisateur != null){
            outState.putParcelable("utilisateur", utilisateur);
            outState.putString("address",address);
            outState.putStringArrayList("models", models);
        }
    }
}
