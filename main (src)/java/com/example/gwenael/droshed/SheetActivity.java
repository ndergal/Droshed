package com.example.derga.droshed;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SheetActivity extends AppCompatActivity {
    SheetView sv;
    Sheet s;
    Intent putService;
    SQLiteDatabase base;

    // demerrage de l'ativity sheet
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        Intent intent = getIntent();
        String nameSheet = intent.getStringExtra("nameSheet");
        User utilisateur = intent.getParcelableExtra("utilisateur");
        String addressServer = intent.getStringExtra("addressS");
        base = new DB(getApplicationContext()).getWritableDatabase();
        s = Sheet.getInstance(base, nameSheet, utilisateur.getLogin());
        sv = (SheetView) findViewById(R.id.sheetView);
        sv.setSheet(s);
        sv.invalidate();
        putService = new Intent(SheetActivity.this, PutService.class);
        putService.putExtra("addressS", addressServer);
        putService.putExtra("IDSheet", s.id);
        putService.putExtra("utilisateur", utilisateur);
        putService.putExtra("nameSheet", nameSheet);
        startService(putService);
    }

    // lance le menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.sheetactivitymenu, menu);
        return true;
    }

    // gere les options du menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insertNewRow:
                Intent newIntent = new Intent(this, AskTextCell.class);
                newIntent.putExtra("TextAdd", "Write the name of the new row");
                startActivityForResult(newIntent, 6);
                break;
            case R.id.InvertSheet :
                sv.invert = !sv.invert;
                sv.invalidate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // permet d'ajouter une ligne dans la sheet
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 5) {
            if (resultCode != RESULT_OK)
                return;
            int idRow = data.getIntExtra("idRow", 0);
            int idCol = data.getIntExtra("idCol", 0);
            String text = data.getStringExtra("text");
            s.updateCell(idRow, idCol, text);
            sv.invalidate();
        }
        if(requestCode == 6) {
            if (resultCode != RESULT_OK)
                return;
            String text = data.getStringExtra("text");
            Log.d("test" , text);
            s.insertNewRow(text);
            sv.invalidate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(putService);
        base.close();
    }
}
