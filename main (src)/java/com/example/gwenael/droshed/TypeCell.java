package com.example.derga.droshed;

/**
 * Created by Gwenael on 09/05/2017.
 */

public class TypeCell{
    final String idType;
    final boolean editable;
    final Float min;
    final Float max;
    final int idCol;
    final int idSheet;

    public TypeCell(String idType, boolean editable, Float min, Float max, int idCol, int IDSheet){
        this.idType = idType; this.editable = editable; this.min = min; this.max = max; this.idCol = idCol; this.idSheet = IDSheet;
    }

    @Override
    public String toString() {
        if(idType.equals("integer"))
            return "Il est attendu un " + idType + " >= à " + min + " et <= à " + max;
        if(idType.equals("float"))
            return "Il est attendu un " + idType + " >= à " + min + " et <= à " + max;
        return "Il est attendu un " + idType;
    }
}
