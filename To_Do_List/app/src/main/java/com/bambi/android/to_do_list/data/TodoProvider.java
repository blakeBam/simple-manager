package com.bambi.android.to_do_list.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by SupportAdmin on 1/21/2018.
 */

public class TodoProvider extends ContentProvider {
    public static final String LOG_TAG = TodoProvider.class.getSimpleName();
    private TodoDbHelper mDbHelper;

    private static final int TODO_TABLE = 100;
    private static final int TODO_ITEM = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(TodoContract.CONTENT_AUTHORITY, TodoContract.ListEntry.TABLE_NAME, TODO_TABLE);
        sUriMatcher.addURI(TodoContract.CONTENT_AUTHORITY, TodoContract.ListEntry.TABLE_NAME + "/#", TODO_ITEM);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new TodoDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match){
            case TODO_ITEM:
                selection = TodoContract.ListEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
            case TODO_TABLE:
                cursor = database.query(TodoContract.ListEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case TODO_TABLE:
                return TodoContract.ListEntry.CONTENT_LIST_TYPE;
            case TODO_ITEM:
                return TodoContract.ListEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        String detail = contentValues.getAsString(TodoContract.ListEntry.COLUMN_TODO);
        if(detail == null || detail.equals(""))
            throw new IllegalArgumentException("Item requires a name");
        final int match = sUriMatcher.match(uri);
        switch(match){
            case TODO_TABLE:
                return insertTodo(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertTodo(Uri uri, ContentValues values){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(TodoContract.ListEntry.TABLE_NAME, null, values);
        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int delete;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case TODO_TABLE:
                delete = database.delete(TodoContract.ListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TODO_ITEM:
                selection = TodoContract.ListEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                delete = database.delete(TodoContract.ListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(delete != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO_TABLE:
                return updateTodo(uri, contentValues, selection, selectionArgs);
            case TODO_ITEM:
                selection = TodoContract.ListEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateTodo(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateTodo(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if(values.containsKey(TodoContract.ListEntry.COLUMN_TODO)){
            String detail = values.getAsString(TodoContract.ListEntry.COLUMN_TODO);
            if(detail == null || detail.equals(""))
                throw new IllegalArgumentException("Pet requires a name");
        }

        if(values.size() == 0)
            return 0;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(TodoContract.ListEntry.TABLE_NAME, values, selection, selectionArgs);

        if(rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
