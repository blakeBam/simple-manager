package com.bambi.android.to_do_list.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SupportAdmin on 1/21/2018.
 */

public class TodoDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TodoContract.ListEntry.TABLE_NAME
            + " (" + TodoContract.ListEntry._ID +  " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TodoContract.ListEntry.COLUMN_TODO + " TEXT NOT NULL);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TodoContract.ListEntry.TABLE_NAME;

    public TodoDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
