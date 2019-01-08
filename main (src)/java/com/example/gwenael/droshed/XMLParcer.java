package com.example.derga.droshed;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XMLParcer
{

    public static boolean parseDataXML(SQLiteDatabase base, int idSheet, int versionSheet, Reader r, DownloadData.Entry entry) throws IOException
    {
        Integer idRow = null;
        Integer idColumn = null;
        String text = null;
        Integer versionCell = null;
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(r);
            int eventType = 0;
            eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("Ajout-Cell")||tagname.equalsIgnoreCase("Update-Cell")) {
                            idRow = Integer.valueOf(parser.getAttributeValue(null, "idRow"));
                            idColumn = Integer.valueOf(parser.getAttributeValue(null, "idCol"));
                            text = parser.getAttributeValue(null, "text");
                            versionCell = Integer.valueOf(parser.getAttributeValue(null, "numVersion"));
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("Update-Cell")) {
                            int versionSheetOfCell = SheetDAO.getVersionSheetOfCell(base, idSheet, idRow, idColumn);
                            if (versionSheetOfCell <= versionSheet) {
                                if (idColumn == 0) {
                                    SheetDAO.updateRow(base, idSheet, text, idRow, versionSheet, versionCell);
                                } else {
                                    SheetDAO.updateCell(base, idSheet, idRow, idColumn, text, versionSheet, versionCell);
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

	public static boolean parseModelXML(SQLiteDatabase base, int idSheet, Reader r, DownloadModel.Entry entry) throws IOException
	{
        Integer id = null;
        String name = null;
        String TypeCell = null;
        Boolean editable = null;
        Float minConstraint = null;
        Float maxConstraint = null;
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(r);
            int eventType = 0;
            eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("colonne")) {
                            id = Integer.valueOf(parser.getAttributeValue(null, "id"));
                            name = parser.getAttributeValue(null, "name");
                            TypeCell = parser.getAttributeValue(null, "type");
                            editable = Boolean.valueOf(parser.getAttributeValue(null, "editable"));
                            if (TypeCell.equals("integer") || TypeCell.equals("float")) {
                                minConstraint = Float.valueOf(parser.getAttributeValue(null, "min"));
                                maxConstraint = Float.valueOf(parser.getAttributeValue(null, "max"));
                            }
                            else{
                                minConstraint = null; maxConstraint = null;
                            }
                        }
                        else if (tagname.equalsIgnoreCase("ligne")){
                            id = Integer.valueOf(parser.getAttributeValue(null, "id"));
                            name = parser.getAttributeValue(null, "name");
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("colonne")) {
                            SheetDAO.insertColumn(base, idSheet, id, name, new TypeCell(TypeCell, editable, minConstraint, maxConstraint, id, idSheet));
                        }
                        if (tagname.equalsIgnoreCase("ligne"))
                            SheetDAO.insertCell(base, idSheet, id, 0, name, -1, 0);
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
		return true;
	}

    public static boolean parseModelsXML(List<String> models, Reader r) throws IOException
    {
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(r);
            int eventType = 0;
            eventType = parser.getEventType();
            String text = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("sheet"))
                            text = parser.getAttributeValue(null,"name");
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("sheet"))
                            models.add(text);
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
