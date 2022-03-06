package io.those.upnext.repository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Repository<T extends Comparable<T>> {

    private final ContentResolver contentResolver;

    protected abstract String[] getProjection();
    protected abstract String getSelection();
    protected abstract Uri getProviderUri();
    protected abstract T toItem(Cursor cur);
    protected abstract String getSortOrder();

    protected Repository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    protected List<T> getItems(String[] selectionArgs) {
        Log.i(getClass().getSimpleName(), String.format("getItems with arguments %s ...", selectionArgs != null ? Arrays.toString(selectionArgs) : ""));
        List<T> items = new ArrayList<>();

        Cursor cur = getContentResolver().query(
                getProviderUri(),
                getProjection(),
                getSelection(),
                selectionArgs,
                getSortOrder()
        );

        while (cur.moveToNext()) {
            items.add(toItem(cur));
        }

        cur.close();
        Collections.sort(items);

        items.forEach(item -> Log.i(getClass().getSimpleName(), String.format("getItems found item: %s", item.toString())));

        Log.i(getClass().getSimpleName(), String.format("getItems finished with %d elements!", items.size()));
        return items;
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }
}