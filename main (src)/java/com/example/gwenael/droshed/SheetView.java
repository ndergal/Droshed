package com.example.derga.droshed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import java.util.ArrayList;

/**
 * Created by Gwenael on 27/05/2017.
 */

public class SheetView extends View implements GestureDetector.OnGestureListener {
    private Sheet sheet;
    boolean invert;
    float scrollX = 0;
    float scrollY = 0;
    public static int TAILLE_X_CELL = 300;
    public static int TAILLE_Y_CELL = 100;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    // gere le scroll sur une sheet
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        ArrayList<ArrayList<Cell>> cells = sheet.getCells();
        int maxY,maxX;
        if(!invert) {
            maxY = (cells.size() - 1) * SheetView.TAILLE_Y_CELL;
            maxX = (cells.get(0).size() - 1) * SheetView.TAILLE_X_CELL;
        }else{
            maxY = (cells.get(0).size() - 1) * SheetView.TAILLE_Y_CELL;
            maxX = (cells.size() - 1) * SheetView.TAILLE_X_CELL;
        }
        scrollX += distanceX * (1/mScaleFactor);
        if(scrollX < 0)
            scrollX = 0;
        if(scrollX > maxX)
            scrollX = maxX;

        scrollY += distanceY * (1/mScaleFactor);
        if(scrollY < 0)
            scrollY = 0;
        if(scrollY > maxY)
            scrollY = maxY;

        invalidate();
        return false;
    }

    // gere l'appuie long sur une cellule pour l'editer
    @Override
    public void onLongPress(MotionEvent e) {
        float x, y;
        x = (e.getX() + (scrollX * mScaleFactor));
        y = (e.getY() + (scrollY * mScaleFactor));

        final int idRow,idCol,idRowMax,idColMax;
        if(!invert) {
            idRow = (int) (y / (100 * mScaleFactor));
            idCol = (int) (x / (300 * mScaleFactor));
        }else{
            idCol = (int) (y / (100 * mScaleFactor));
            idRow = (int) (x / (300 * mScaleFactor));
        }

        idRowMax = sheet.getCells().size() - 1 ;
        idColMax = sheet.getCells().get(0).size() - 1;

        if((idRow > idRowMax)||(idCol > idColMax))
            return;
        if(!(sheet.getCells().get(idRow).get(idCol).typeInput.editable)) {
            return;
        }
        Intent newIntent = new Intent(getContext(), AskTextCell.class);
        newIntent.putExtra("idRow", idRow);
        newIntent.putExtra("idCol", idCol);
        newIntent.putExtra("TextAdd", sheet.getCells().get(idRow).get(idCol).typeInput.toString());
        ((Activity)getContext()).startActivityForResult(newIntent, 5);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public SheetView(Context context) {
        super(context);
        gestureDetector = new GestureDetector(getContext(), this);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public SheetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(getContext(), this);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public SheetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(getContext(), this);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setSheet(Sheet sheet){
        this.sheet = sheet;
    }

    // dessine les sheets
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(sheet == null)
            return;
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(0, 0, 0));
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);

        Paint textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(20);
        textPaint.setColor(Color.rgb(0, 0, 0));

        ArrayList<ArrayList<Cell>> cells = sheet.getCells();

        if(!invert) {
            for (int i = 0; i < cells.size(); i++) {
                ArrayList<Cell> ligne = cells.get(i);
                for (int j = 0; j < ligne.size(); j++) {
                    canvas.drawRect((TAILLE_X_CELL * j - scrollX), (TAILLE_Y_CELL * i - scrollY), (TAILLE_X_CELL + TAILLE_X_CELL * j - scrollX), (TAILLE_Y_CELL + TAILLE_Y_CELL * i - scrollY), paint);
                    canvas.drawText(ligne.get(j).getText(), ((TAILLE_X_CELL / 2) + TAILLE_X_CELL * j - scrollX), ((TAILLE_Y_CELL / 2) + TAILLE_Y_CELL * i - scrollY), textPaint);
                }
            }
        }else{
            for (int i = 0; i < cells.size(); i++) {
                ArrayList<Cell> ligne = cells.get(i);
                for (int j = 0; j < ligne.size(); j++) {
                    canvas.drawRect((TAILLE_X_CELL * i - scrollX), (TAILLE_Y_CELL * j - scrollY), (TAILLE_X_CELL + TAILLE_X_CELL * i - scrollX), (TAILLE_Y_CELL + TAILLE_Y_CELL * j - scrollY), paint);
                    canvas.drawText(ligne.get(j).getText(), ((TAILLE_X_CELL / 2) + TAILLE_X_CELL * i - scrollX), ((TAILLE_Y_CELL / 2) + TAILLE_Y_CELL * j - scrollY), textPaint);
                }
            }
        }
        canvas.restore();
    }
}
