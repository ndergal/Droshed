package com.example.derga.droshed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AskTextCell extends AppCompatActivity {

    // lancement de l'activité qui permet de modifier une cellule
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_text_cell);
        Intent intent = getIntent();
        String textAdd = intent.getStringExtra("TextAdd");
        final int idRow = intent.getIntExtra("idRow", 0);
        final int idCol = intent.getIntExtra("idCol", 0);
        Log.d("test1", "idRow = " + idRow + ", idCol = " + idCol);
        TextView tv = (TextView) findViewById(R.id.typecelltv);
        tv.setText(textAdd);
        Button bb = (Button) findViewById(R.id.updatecellCancelButton);
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
            }
        });
        Button b = (Button) findViewById(R.id.updatecellButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = ((EditText) findViewById(R.id.updatecellet)).getText().toString();
                Intent intent = new Intent();
                intent.putExtra("text", text);
                intent.putExtra("idRow", idRow);
                intent.putExtra("idCol", idCol);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
