package com.yube.TMP;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.yube.TMP.Contact.PlayListContact;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tolga on 11.10.2017.
 */

public class DataBase {
    private static final String DATABASENAME = "TMPDATABASE";
    private static final String TABLENAME = "TMPTABLE";
    private static final int DATABASE_VERSION = 1;
    private static final String ID = "tmp_id";
    private static final String NAME = "name";
    private static final String PATH = "path";
    private static final String CATEGORY = "category";
    Context context;
    String sonuc;
    ArrayList<PlayListContact> veri=new ArrayList<>();
    private DbHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DataBase(Activity mainActivity) {
        this.context = mainActivity;
    }

    public DataBase open() {
        dbHelper = new DbHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        return this;
    }

    public long create(String name, String path, String category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(PATH, path);
        contentValues.put(CATEGORY, category);
        return sqLiteDatabase.insert(TABLENAME, null, contentValues);
    }

    public void close() {
        sqLiteDatabase.close();
    }

    public String vericek() {
        String[] column = new String[]{ID, NAME, PATH, CATEGORY};
        Cursor c = sqLiteDatabase.query(TABLENAME, column, null, null, null, null, null);
        sonuc = "";
        int idindex = c.getColumnIndex(ID);
        int nameindex = c.getColumnIndex(NAME);
        int pathindex = c.getColumnIndex(PATH);
        int categoryindex = c.getColumnIndex(CATEGORY);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
        PlayListContact pb=new PlayListContact(c.getString(0),c.getString(1),c.getString(2));
        veri.add(pb);
        }
        sonuc = sonuc + c.getString(nameindex);

        return sonuc;
    }


    private static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, DATABASENAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLENAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT NOT NULL," + PATH + "TEXT NOT NULL," + CATEGORY + "TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }


    }
}
