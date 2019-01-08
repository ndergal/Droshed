package com.example.derga.droshed;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gwenael on 27/05/2017.
 */

public class DB extends SQLiteOpenHelper {
    public static final String DB_NAME = "droshedDB";
    public static final int VERSION = 1;

    public DB(Context context) { super(context, DB_NAME, null, VERSION);}

    //lancement de la bdd
    @Override
    public void onCreate(SQLiteDatabase db) {
        String creationDatabase = "create table Sheet"
                + "("
                + "ID integer primary key autoincrement, "
                + "Name text not null, "
                + "Login text not null, "
                + "VersionGetFichier integer"
                + ");";
        db.execSQL(creationDatabase);
        creationDatabase = "create table TypeCell"
                + "("
                + "ID integer primary key autoincrement, "
                + "Type text not null, "
                + "Editable integer not null, "
                + "Min real, "
                + "Max real, "
                + "IDColumn integer not null, "
                + "IDSheet integer not null, "
                + "FOREIGN KEY(IDSheet) REFERENCES Sheet(ID)"
                + ");";
        db.execSQL(creationDatabase);
        creationDatabase = "create table Cell"
                + "("
                + "ID integer primary key autoincrement,"
                + "IDCol integer not null, "
                + "IDRow integer not null, "
                + "Text text, "
                + "VersionSheet integer not null, "
                + "VersionCell integer not null, "
                + "IDSheet integer not null, "
                + "IDTypeCell integer not null, "
                + "FOREIGN KEY(IDSheet) REFERENCES Sheet(ID), "
                + "FOREIGN KEY(IDTypeCell) REFERENCES TypeCell(ID)"
                + ");";
        db.execSQL(creationDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Do nothing
    }
}
