package com.bambi.android.to_do_list;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bambi.android.to_do_list.data.TodoContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SupportAdmin on 1/21/2018.
 */

public class TodoCursorAdapter extends CursorAdapter {
    private SparseBooleanArray checked;
    public TodoCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
        checked = new SparseBooleanArray();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView detailView = view.findViewById(R.id.todo_item);
        String detail = cursor.getString(cursor.getColumnIndex(TodoContract.ListEntry.COLUMN_TODO));
        detailView.setText(detail);
        final int id = cursor.getInt(cursor.getColumnIndex(TodoContract.ListEntry._ID));
        final CheckBox checkBox = view.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checked.put(id, checkBox.isChecked());
            }
        });
        boolean checkedValue = checked.get(id);
        checkBox.setChecked(checkedValue);
    }

    public List<Integer> getItemsToDelete(){
        ArrayList<Integer> ids = new ArrayList<>();
        for(int i = 0; i < checked.size(); i++){
            int key = checked.keyAt(i);
            if(checked.get(key))
                ids.add(key);
        }
        return ids;
    }
}
