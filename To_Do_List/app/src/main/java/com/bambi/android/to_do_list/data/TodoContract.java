package com.bambi.android.to_do_list.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class TodoContract {
    private TodoContract() {}
    public static final String CONTENT_AUTHORITY = "com.bambi.android.to_do_list";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static abstract class ListEntry implements BaseColumns{
        public static final String TABLE_NAME = "todo";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TODO = "detail";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    }
}
