package io.those.upnext.repository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.util.Log;

import java.util.List;

import io.those.upnext.model.UpNextCalendar;

public class CalendarRepository extends Repository<UpNextCalendar> {

    public CalendarRepository(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                Calendars._ID,                   // 0
                Calendars.NAME,                  // 1
                Calendars.ACCOUNT_NAME,          // 2
                Calendars.CALENDAR_DISPLAY_NAME, // 3
                Calendars.CALENDAR_COLOR,        // 4
                Calendars.VISIBLE                // 5
        };
    }

    @Override
    protected String getSelection() {
        return null;
    }

    @Override
    protected Uri getProviderUri() {
        return Calendars.CONTENT_URI;
    }

    @Override
    protected UpNextCalendar toItem(Cursor cur) {
        return UpNextCalendar.of(
                cur.getLong(0),
                cur.getString(1),
                cur.getString(2),
                cur.getString(3),
                cur.getInt(4),
                cur.getInt(5) == 1 ? Boolean.TRUE : Boolean.FALSE
        );
    }

    public List<UpNextCalendar> getCalendars() {
        Log.i(CalendarRepository.class.getSimpleName(), "getCalendars ...");
        List<UpNextCalendar> calendars = getItems(null);
        Log.i(CalendarRepository.class.getSimpleName(), String.format("getCalendars finished with %d calendars!", calendars.size()));
        return calendars;
    }
}