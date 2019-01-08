package com.example.derga.droshed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    // demarrage de l'activite de login
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button b = (Button) findViewById(R.id.validateButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = ((EditText) findViewById(R.id.loginEditText)).getText().toString();
                String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
                String address = ((EditText) findViewById(R.id.addressedittext)).getText().toString();
                User util = User.createUser(login, password);
                DownloadModels asyncTask = new DownloadModels();
                DownloadModels.Entry entry = asyncTask.new Entry(util,address, LoginActivity.this);
                asyncTask.execute(entry);
            }
        });
    }

    // valide le login password et serveur
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}