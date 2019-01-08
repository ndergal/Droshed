package com.example.derga.droshed;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gwenael on 30/05/17.
 */

public class SheetDAO {

    public static ArrayList<ArrayList<Cell>> getAllCells(SQLiteDatabase base, int IDSheet){
        ArrayList<ArrayList<Cell>> newAL = new ArrayList<>();
        int maxIDRow = getMaxIDRow(base, IDSheet);
        for(int i = 0; i <= maxIDRow; i++)
            newAL.add(new ArrayList<Cell>());
        Cursor c = base.rawQuery("select * from Cell where IDSheet = " + IDSheet, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int idCol  = c.getInt(c.getColumnIndex("IDCol"));
            int idRow  = c.getInt(c.getColumnIndex("IDRow"));
            String text  = c.getString(c.getColumnIndex("Text"));
            TypeCell tc;
            if(idRow == 0)
                tc = getTypeCell(base, IDSheet, -1);
            else
                tc = getTypeCell(base, IDSheet, idCol);
            Cell cell = Cell.create(idCol, idRow, tc, text);
            newAL.get(idRow).add(cell);
        }
        c.close();
        return newAL;
    }

    public static HashMap<Integer, TypeCell> getAllTypeCells(SQLiteDatabase base, int IDSheet){
        HashMap<Integer, TypeCell> newMap = new HashMap<>();
        Cursor c = base.rawQuery("select * from TypeCell where IDSheet = " + IDSheet, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String type = c.getString(c.getColumnIndex("Type"));
            Boolean editable  = (c.getInt(c.getColumnIndex("Editable")) == 1)?true:false;
            Float min  = c.getFloat(c.getColumnIndex("Min"));
            Float max  = c.getFloat(c.getColumnIndex("Max"));
            int idCol  = c.getInt(c.getColumnIndex("IDColumn"));
            int idSheet  = c.getInt(c.getColumnIndex("IDSheet"));
            TypeCell tc = new TypeCell(type, editable, min, max, idCol, idSheet);
            newMap.put(idCol, tc);
        }
        c.close();
        return newMap;
    }

    public static void insertSheet(SQLiteDatabase base, String name, String loginUser){
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Login", loginUser);
        values.put("VersionGetFichier", -1);
        base.insert("Sheet", null, values);
    }

    public static TypeCell getTypeCell(SQLiteDatabase base, int IDSheet, int IDCol){
        Cursor c = base.rawQuery("select * from TypeCell where IDSheet = " + IDSheet + " AND IDColumn = " + IDCol, null);
        c.moveToFirst();
        String type = c.getString(c.getColumnIndex("Type"));
        Boolean editable  = (c.getInt(c.getColumnIndex("Editable")) == 1)?true:false;
        Float min  = c.getFloat(c.getColumnIndex("Min"));
        Float max  = c.getFloat(c.getColumnIndex("Max"));
        int idCol  = c.getInt(c.getColumnIndex("IDColumn"));
        int idSheet  = c.getInt(c.getColumnIndex("IDSheet"));
        TypeCell tc = new TypeCell(type, editable, min, max, idCol, idSheet);
        return tc;
    }

    public static int getIDSheet(SQLiteDatabase base, String name, String loginUser){
        Cursor c = base.rawQuery("select ID from Sheet where Name = '" + name + "' and Login = '" + loginUser + "'", null);
        if(c.getCount() == 0)
            return -1;
        c.moveToFirst();
        int i = c.getInt(c.getColumnIndex("ID"));
        c.close();
        return i;
    }

    public static void insertColumn(SQLiteDatabase base, int idSheet, int IDCol, String name, TypeCell TC){
        insertTypeCell(base, idSheet, TC);
        ContentValues values = new ContentValues();
        values.put("IDCol", IDCol);
        values.put("IDRow", 0);
        values.put("Text", name);
        values.put("VersionSheet", -1);
        values.put("VersionCell", 0);
        values.put("IDSheet", idSheet);
        values.put("IDTypeCell", getIDTypeCell(base, idSheet, 0));
        base.insert("Cell", null, values);
    }

    public static void insertCell(SQLiteDatabase base, int IDSheet, int idRow, int idCol, String text, int VersionSheet, int VersionCell){
        ContentValues values = new ContentValues();
        values.put("IDCol", idCol);
        values.put("IDRow", idRow);
        values.put("Text", text);
        values.put("VersionSheet", VersionSheet);
        values.put("VersionCell", VersionCell);
        values.put("IDSheet", IDSheet);
        values.put("IDTypeCell", getIDTypeCell(base, IDSheet, idCol));
        base.insert("Cell", null, values);
    }

    public static int getLastGetVersionSheet(SQLiteDatabase base, int IDSheet){
        Cursor c = base.rawQuery("select * from Sheet where ID = " + IDSheet, null);
        if(c.getCount() == 0)
            Log.d("test", "Probleme with idsheet = " + IDSheet);
        c.moveToNext();
        int i = c.getInt(c.getColumnIndex("VersionGetFichier"));
        c.close();
        return i;
    }

    public static int getVersionSheet(SQLiteDatabase base, String name, String loginUser){
        Cursor c = base.rawQuery("select VersionGetFichier from Sheet where Name = '" + name + "' and Login = '" + loginUser + "'", null);
        if(c.getCount() == 0)
            return -2;
        c.moveToNext();
        int i = c.getInt(c.getColumnIndex("VersionGetFichier"));
        c.close();
        return i;
    }

    public static int getVersionSheet(SQLiteDatabase base, int idSheet){
        Cursor c = base.rawQuery("select VersionGetFichier from Sheet where ID = " + idSheet, null);
        c.moveToFirst();
        int i = c.getInt(c.getColumnIndex("VersionGetFichier"));
        c.close();
        return i;
    }

    public static void updateCell(SQLiteDatabase base, int IDSheet, int IDRow, int IDCol, String text){
        int newVersionSheet = getLastGetVersionSheet(base, IDSheet) + 1;
        int newVersionCell = getMaxNumVersionCell(base, IDSheet) + 1;
        updateCell(base,IDSheet,IDRow, IDCol, text, newVersionSheet, newVersionCell);
    }

    public static void updateCell(SQLiteDatabase base, int IDSheet, int IDRow, int IDCol, String text, Integer versionSheet, Integer versionCell){
        ContentValues values = new ContentValues();
        values.put("Text", text.toString());
        values.put("VersionSheet", versionSheet);
        values.put("VersionCell", versionCell);
        String[] args = new String[]{Integer.toString(IDSheet), Integer.toString(IDRow), Integer.toString(IDCol)};
        base.update("Cell", values, "IDSheet = ? AND IDRow = ? AND IDCol = ?", args);
    }

    public static void insertTypeCell(SQLiteDatabase base, int IDSheet, TypeCell TC){
        ContentValues values = new ContentValues();
        values.put("Type", TC.idType);
        values.put("Editable", TC.editable);
        values.put("Min", TC.min);
        values.put("Max", TC.max);
        values.put("IDColumn", TC.idCol);
        values.put("IDSheet", IDSheet);
        base.insert("TypeCell", null, values);
    }

    public static int getIDTypeCell(SQLiteDatabase base, int IDSheet, int IDCol){
        Cursor c = base.rawQuery("select ID from TypeCell where IDSheet = " + IDSheet + " and IDColumn = " + IDCol, null);
        c.moveToNext();
        int i = c.getInt(c.getColumnIndex("ID"));
        c.close();
        return i;
    }

    public static int getMaxIDCol(SQLiteDatabase base, int IDSheet){
        Cursor c = base.rawQuery("select Max(IDCol) as IDCol from Cell where IDSheet = " + IDSheet, null);
        c.moveToNext();
        int i = c.getInt(c.getColumnIndex("IDCol"));
        c.close();
        return i;
    }

    public static int getMaxIDRow(SQLiteDatabase base, int IDSheet){
        Cursor c = base.rawQuery("select Max(IDRow) as IDRow from Cell where IDSheet = " + IDSheet, null);
        c.moveToNext();
        int i = c.getInt(c.getColumnIndex("IDRow"));
        c.close();
        return i;
    }

    public static int getMaxNumVersionCell(SQLiteDatabase base, int IDSheet){
        Cursor c = base.rawQuery("select Max(VersionCell) as VersionCell from Cell where IDSheet = " + IDSheet, null);
        c.moveToNext();
        int i = c.getInt(c.getColumnIndex("VersionCell"));
        c.close();
        return i;
    }

    public static void insertVirginSheet(SQLiteDatabase base, int IDSheet){
        int maxCol = getMaxIDCol(base, IDSheet);
        int maxRow = getMaxIDRow(base, IDSheet);
        for(int i = 1; i <= maxCol; i++) {
            for (int j = 1; j <= maxRow; j++) {
                insertCell(base, IDSheet, j, i, "", -1, 0);
            }
        }
    }

    public static void updateRow(SQLiteDatabase base, int IDSheet, String text, int IDRow, int IDVersionSheet, int IDVersionCell){
        int maxRow = getMaxIDRow(base, IDSheet);
        if(IDRow > maxRow)
            insertRow(base, IDSheet, text, IDRow, IDVersionSheet, IDVersionCell);
        else
            updateCell(base, IDSheet, IDRow, 0, text, IDVersionSheet, IDVersionCell);
    }

    public static void insertRow(SQLiteDatabase base, int IDSheet, String text, int IDRow, int IDVersionSheet, int IDVersionCell){
        int maxCol = getMaxIDCol(base, IDSheet);
        insertCell(base, IDSheet, IDRow, 0, text, IDVersionSheet, IDVersionCell);
        for (int j = 1; j <= maxCol; j++) {
            insertCell(base, IDSheet, IDRow, j, "", IDVersionSheet, IDVersionCell);
        }
    }

    public static void insertNewRow(SQLiteDatabase base, int IDSheet, String text){
        int newVersionSheet = getLastGetVersionSheet(base, IDSheet) + 1;
        int newVersionCell = getMaxNumVersionCell(base, IDSheet) + 1;
        int maxCol = getMaxIDCol(base, IDSheet);
        int maxRow = getMaxIDRow(base, IDSheet) + 1;
        insertCell(base, IDSheet, maxRow, 0, text, newVersionSheet, newVersionCell);
        for (int j = 1; j <= maxCol; j++) {
            insertCell(base, IDSheet, maxRow, j, "", newVersionSheet, newVersionCell);
        }
    }

    public static void updateVersionSheetOldCells(SQLiteDatabase base, int idSheet, int newVersionSheet) {
        int versionLocalSheet = SheetDAO.getVersionSheet(base, idSheet);
        ContentValues values = new ContentValues();
        values.put("VersionSheet", newVersionSheet);
        String[] args = new String[]{Integer.toString(idSheet), Integer.toString(versionLocalSheet)};
        base.update("Cell", values, "IDSheet = ? and VersionSheet > ?", args);
    }

    public static int getVersionSheetOfCell(SQLiteDatabase base, int idSheet, int idRow, int idCol){
        Cursor c = base.rawQuery("select VersionSheet from Cell where IDSheet = " + idSheet + " and IDRow = " + idRow + " and IDCol = " + idCol, null);
        if(c.getCount() == 0)
            return -3;
        c.moveToNext();
        int i = c.getInt(c.getColumnIndex("VersionSheet"));
        c.close();
        return i;
    }

    public static void setLastVersionGet(SQLiteDatabase base, int idSheet, int newLastGetVersion) {
        ContentValues values = new ContentValues();
        values.put("VersionGetFichier", newLastGetVersion);
        String[] args = new String[]{Integer.toString(idSheet)};
        base.update("Sheet", values, "ID = ?", args);
    }

    public static String getXLMNewCells(SQLiteDatabase base, int idSheet, int lastGetVersionSheet) {
        Cursor c = base.rawQuery("select * from Cell where IDSheet = " + idSheet + " and VersionSheet > " + lastGetVersionSheet, null);
        if(c.getCount() == 0)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append("<Datas>\n");
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int idCol  = c.getInt(c.getColumnIndex("IDCol"));
            int idRow  = c.getInt(c.getColumnIndex("IDRow"));
            String text  = c.getString(c.getColumnIndex("Text"));
            int versionCell  = c.getInt(c.getColumnIndex("VersionCell"));
            sb.append("<Update-Cell idRow=\"").append(idRow);
            sb.append("\" idCol=\"").append(idCol);
            sb.append("\" text=\"").append(text);
            sb.append("\" numVersion=\"").append(versionCell).append("\" />").append("\n");
        }
        c.close();
        sb.append("</Datas>");
        return sb.toString();
    }
}
