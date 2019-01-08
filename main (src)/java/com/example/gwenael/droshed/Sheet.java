package com.example.derga.droshed;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gwenael on 04/05/2017.
 */

public class Sheet {
    final int id;
    final String login;
    final String name;
    private ArrayList<ArrayList<Cell>> cells;
    private HashMap<Integer, TypeCell> typeByColumn;
    private SQLiteDatabase base;

    private Sheet(SQLiteDatabase base, String name, String login){
        id = SheetDAO.getIDSheet(base, name, login);
        this.base = base; this.name = name; this.login = login;
    }

    public static Sheet getInstance(SQLiteDatabase base, String name, String login){
        Sheet s = new Sheet(base, name, login);
        s.insertAllsTypeCells();
        s.insertAllsCells();
        return s;
    }

    public ArrayList<ArrayList<Cell>> getCells(){
        return cells;
    }

    public void insertNewRow(String text){
        SheetDAO.insertNewRow(base, id, text);
        Log.d("test", "test");
        insertAllsCells();
    }

    // met a jour une cellule
    public void updateCell(int idRow, int idColumn, String text){
        Cell cell = getCell(idRow, idColumn);
        if(!(cell.newTextIsCorrect(text)))
            return;
        SheetDAO.updateCell(base, id, idRow, idColumn, text);
        cell.setText(text);
    }

    // renvoie une cellule
    public Cell getCell(int idRow, int idCol){
        return cells.get(idRow).get(idCol);
    }

    private void insertAllsCells(){
        cells = SheetDAO.getAllCells(base, id);
    }

    private void insertAllsTypeCells(){
        typeByColumn = SheetDAO.getAllTypeCells(base, id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(ArrayList<Cell> l : cells) {
            for (Cell c : l)
                sb.append(c.getText() + "|");
            sb.append("\n");
        }
        return sb.toString();
    }
}