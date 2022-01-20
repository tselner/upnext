package io.those.upnext.repository;

import android.content.ContentResolver;

public abstract class Repository {
    private final ContentResolver contentResolver;

    protected Repository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }
}