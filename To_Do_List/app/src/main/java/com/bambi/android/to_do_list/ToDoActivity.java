package com.bambi.android.to_do_list;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bambi.android.to_do_list.data.TodoContract;

import java.util.ArrayList;

public class ToDoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private EditText mNewItemEntry;
    private TodoCursorAdapter mAdapter;
    private ListView mListView;

    private Button mInsertButton;
    private Button mAddButton;
    private Button mDeleteButton;

    private View.OnClickListener insertOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            insert();
        }
    };

    private View.OnClickListener addOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showInsertion();
            mNewItemEntry.requestFocus();
        }
    };

    private View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ArrayList<Integer> ids = (ArrayList<Integer>) mAdapter.getItemsToDelete();
            for(int id : ids) {
                Uri curItemUri = Uri.withAppendedPath(TodoContract.ListEntry.CONTENT_URI, String.valueOf(id));
                getContentResolver().delete(curItemUri, null, null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        mNewItemEntry = findViewById(R.id.editText);
        mInsertButton = findViewById(R.id.insert_item);
        mAddButton = findViewById(R.id.add_item);
        mDeleteButton = findViewById(R.id.delete_item);
        hideInsertion();

        mAdapter = new TodoCursorAdapter(this, null);
        mListView = findViewById(R.id.todo_list);
        mListView.setAdapter(mAdapter);
        mListView.requestFocus();

        getLoaderManager().initLoader(0, null, this);
        mInsertButton.setOnClickListener(insertOnClickListener);
        mAddButton.setOnClickListener(addOnClickListener);
        mDeleteButton.setOnClickListener(deleteClickListener);
    }

    private void insert(){
        String detail = mNewItemEntry.getText().toString();
        ContentValues values = new ContentValues();
        values.put(TodoContract.ListEntry.COLUMN_TODO, detail);
        try {
            getContentResolver().insert(TodoContract.ListEntry.CONTENT_URI, values);
        }catch (IllegalArgumentException e){
            String message = e.getMessage();
            if(message.equals("Item requires a name"))
                Toast.makeText(this, message, Toast.LENGTH_SHORT);
            else
                Log.d("Todo Activity", "The uri is to update/save a pet is malformed");
        }
        mNewItemEntry.setText("");
        mListView.requestFocus();
        hideInsertion();
    }

    private void showInsertion(){
        mNewItemEntry.setVisibility(View.VISIBLE);
        mInsertButton.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mNewItemEntry, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideInsertion(){
        mNewItemEntry.setVisibility(View.GONE);
        mInsertButton.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mNewItemEntry.getWindowToken(), 0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                TodoContract.ListEntry._ID,
                TodoContract.ListEntry.COLUMN_TODO};

        return new CursorLoader(
                this,
                TodoContract.ListEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
