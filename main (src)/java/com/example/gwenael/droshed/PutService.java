package com.example.derga.droshed;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwenael on 04/06/17.
 */

public class PutService extends Service {
    private SQLiteDatabase base;
    private Thread updateThread = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        base = new DB(getApplicationContext()).getWritableDatabase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test", "testService");
        final int IDSheet = intent.getIntExtra("IDSheet", 0);
        final String nameSheet = intent.getStringExtra("nameSheet");
        final User utilisateur = intent.getParcelableExtra("utilisateur");
        final String addressServer = intent.getStringExtra("addressS");
        updateThread = new Thread(new Runnable()
        {
            @Override public void run()
            {
                while (!Thread.interrupted())
                {
                    try {
                        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                        Log.d("test ID Sheet", Integer.toString(IDSheet));
                        if (networkInfo != null && networkInfo.isConnected()) {
                            int lastGetVersionSheet = SheetDAO.getLastGetVersionSheet(base, IDSheet);
                            Log.d("test lastGetVersionShee", Integer.toString(lastGetVersionSheet));
                            String newCells = SheetDAO.getXLMNewCells(base, IDSheet, lastGetVersionSheet);
                            if(newCells!=null) {
                                Log.d("test3", newCells);
                                PutData asyncTaskData = new PutData();
                                PutData.Entry entryD = asyncTaskData.new Entry(IDSheet, nameSheet, utilisateur, addressServer, newCells, (lastGetVersionSheet + 1), getApplicationContext());
                                asyncTaskData.execute(entryD);
                            }
                        }
                        Thread.sleep(10000);
                    }
                    catch (InterruptedException e) { return ; }
                }
            }
        });
        updateThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (updateThread != null) updateThread.interrupt();
        base.close();
        super.onDestroy();
    }
}
