package com.example.derga.droshed;

/**
 * Created by gwenael on 01/06/17.
 */

public class Cell{
    final int idRow;
    final int idColumn;
    final TypeCell typeInput;
    private String text;

    private Cell(int idRow, int idColumn, TypeCell typeInput, String text){
        this.idRow = idRow; this.idColumn = idColumn; this.typeInput = typeInput; this.text = text;
    }

    public static Cell create(int idRow, int idColumn, TypeCell typeInput, String text){
        return new Cell(idRow, idColumn, typeInput, text);
    }

    // vérifie que le type de text saisie dans la cellule correspond bien au type de la cellule
    public boolean newTextIsCorrect(String text){
        if(!typeInput.editable)
            return false;
        if(typeInput.idType.equals("integer")){
            int i = 0;
            try{
                i = Integer.valueOf(text);
            } catch (NumberFormatException e){
                return false;
            }
            if(i >= typeInput.min && i <= typeInput.max)
                return true;
            return false;
        }

        if(typeInput.idType.equals("float")){
            float i = 0;
            try{
                i = Float.valueOf(text);
            } catch (NumberFormatException e){
                return false;
            }
            if(i >= typeInput.min && i <= typeInput.max)
                return true;
            return false;
        }
        return true;
    }

    // recupere le texte de la cellule
    public String getText(){
        return text;
    }

    // met le texte de la cellule a jour
    public void setText(String text){
        this.text = text;
    }

    @Override
    public String toString() {
        return "idRow = " + idRow + ", idColumn = " + idColumn + ", typeInput = " + typeInput + ", text = " + text;
    }
}
