package com.example.housekeepingbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper sInstance;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "A house keep in book.db";
    private static final String SQL_CREATE_ENTRIES =
            String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    DBStructure.TABLE_NAME,
                    DBStructure.COLUMN_NAME_DAY,
                    DBStructure.COLUMN_NAME_INCOME,
                    DBStructure.COLUMN_NAME_INCOMEMEMO,
                    DBStructure.COLUMN_NAME_SPEND,
                    DBStructure.COLUMN_NAME_SPENDMEMO);

    private  static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+DBStructure.TABLE_NAME;

    public static DBHelper getInstance(Context context){
        if (sInstance == null){
            sInstance = new DBHelper(context);
        }
        return  sInstance;
    }

    private DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
