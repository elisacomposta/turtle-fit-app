package com.example.turtlefit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBhelper extends SQLiteOpenHelper {

    private String TABLE = "activity";
    private String COLUMN_SPORT = "sport";
    private String COLUMN_DATE = "date";
    private String COLUMN_DIFFICULTY = "difficulty";
    private String COLUMN_NOTES = "notes";
    private String COLUMN_SAVED = "saved";

    public DBhelper(@Nullable Context context) {
        super(context, "turtleDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String q = "CREATE TABLE " + TABLE + "(" + COLUMN_SPORT + " INTEGER NOT NULL, " +
                COLUMN_DATE + " STRING NOT NULL, " +
                COLUMN_DIFFICULTY + " INTEGER NOT NULL, " +
                COLUMN_NOTES + " STRING, " +
                COLUMN_SAVED + " INTEGER NOT NULL, PRIMARY KEY(" + COLUMN_SPORT + ", " + COLUMN_DATE + "));";
        db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Sport> getEveryone(){
        List<Sport> l = new ArrayList<>();
        String q = "SELECT * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(q, null);
        if(c.moveToFirst()){
            do{
                String s = c.getString(0);
                String dat = c.getString(1);
                int diff = c.getInt(2);
                String n = c.getString(3);
                boolean svd = intToBool(c.getInt(4));
                Sport act = new Sport(s, dat, diff, n, svd);
                l.add(act);
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return l;
    }

    public void addOne(Sport s){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SPORT, s.getActivity());
        cv.put(COLUMN_DATE, s.getDate());
        cv.put(COLUMN_DIFFICULTY, s.getDifficulty());
        cv.put(COLUMN_NOTES, s.getNotes());

        if(s.isSaved()){
            cv.put(COLUMN_SAVED, 1);
        } else {
            cv.put(COLUMN_SAVED, 0);
        }

        db.insert(TABLE, null, cv);
        db.close();
    }

    public void addAll(List<Sport> l){
        for(Sport s: l){
            addOne(s);
        }
    }

    public boolean deleteOne(Sport s){
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "DELETE FROM " + TABLE + " WHERE " + COLUMN_SPORT + " = '" + s.getActivity() + "' AND " + COLUMN_DATE + " = '" + s.getDate() + "'";
        Cursor c = db.rawQuery(q, null);
        if(c.moveToFirst()){
            return true;
        }
        c.close();
        db.close();
        return false;
    }

    public List<Sport> getAll(String date){     //retrieve all activities on a specific day
        List<Sport> toRet = new ArrayList<>();

        String q = "SELECT * FROM " + TABLE + " WHERE date = '" + date + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(q, null);
        if(c.moveToFirst()){
            do {
                Sport s = new Sport(c.getString(0), c.getString(1),
                        c.getInt(2), c.getString(3), intToBool(c.getInt(4)));
                toRet.add(s);
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return toRet;
    }

    public boolean intToBool(int x){
        switch(x){
            case 1: return true;
            default: return false;
        }
    }

    public void updateOne(Sport s){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SPORT, s.getActivity());
        cv.put(COLUMN_DATE, s.getDate());
        cv.put(COLUMN_DIFFICULTY, s.getDifficulty());
        cv.put(COLUMN_NOTES, s.getNotes());
        cv.put(COLUMN_SAVED, 1);

        db.update(TABLE, cv, COLUMN_SPORT + " = ? AND " + COLUMN_DATE + " = ?" , new String[]{s.getActivity(), s.getDate()});
        db.close();
    }

    public void updateAll(List<Sport> l){
        for(Sport s: l){
            updateOne(s);
        }
    }
}
