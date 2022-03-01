package io.those.upnext.repository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Repository<T extends Comparable> {

    private final ContentResolver contentResolver;

    protected abstract String[] getProjection();
    protected abstract String getSelection();
    protected abstract Uri getProviderUri();
    protected abstract T toItem(Cursor cur);

    protected Repository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    protected List<T> getItems(String[] selectionArgs) {
        Log.i(Repository.class.getSimpleName(), String.format("getItems with %d selection arguments...", selectionArgs != null ? selectionArgs.length : 0));
        List<T> items = new ArrayList<>();

        Cursor cur = getContentResolver().query(
                getProviderUri(),
                getProjection(),
                getSelection(),
                selectionArgs,
                null
        );

        while (cur.moveToNext()) {
            items.add(toItem(cur));
        }

        cur.close();
        Collections.sort(items);

        Log.i(Repository.class.getSimpleName(), String.format("getItems finished with %d elements!", items.size()));
        return items;
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }
}