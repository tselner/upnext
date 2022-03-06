package io.those.upnext.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.those.upnext.model.UpNextCalendar;
import io.those.upnext.model.UpNextEvent;
import io.those.upnext.util.TimeUtil;

public class EventRepository {
    private final ContentResolver contentResolver;

    public EventRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    private String[] getProjection() {
        return new String[]{
                /* 0 */ CalendarContract.Instances.EVENT_ID,             // String
                /* 1 */ CalendarContract.Instances.TITLE,                // String
                /* 2 */ CalendarContract.Instances.DESCRIPTION,          // String
                /* 3 */ CalendarContract.Instances.ALL_DAY,              // Int (boolean)
                /* 4 */ CalendarContract.Instances.BEGIN,                // Long
                /* 5 */ CalendarContract.Instances.END,                  // Long
                /* 6 */ CalendarContract.Instances.EVENT_TIMEZONE,       // String
                /* 7 */ CalendarContract.Instances.CALENDAR_ID,          // Long
                /* 8 */ CalendarContract.Instances.CALENDAR_DISPLAY_NAME,// String
                /* 9 */ CalendarContract.Instances.CALENDAR_COLOR        // Int
        };
    }

    private UpNextEvent toEvent(Cursor cur) {
        return UpNextEvent.of(
                cur.getString(0),
                cur.getString(1),
                cur.getString(2),
                cur.getInt(3) == 1,
                cur.getLong(4),
                cur.getLong(5),
                cur.getString(6),
                UpNextCalendar.of(cur.getLong(7), cur.getString(8), cur.getInt(9))
        );
    }

    public List<UpNextEvent> getEvents(LocalDate day) {
        if (day == null) {
            return Collections.emptyList();
        }

        Log.i(getClass().getSimpleName(), String.format("getEvents for day %s ...", day.toString()));
        List<UpNextEvent> events = new ArrayList<>();

        long start = Time.getJulianDay(TimeUtil.toMillis(day, TimeUtil.UTC), 0);
        long end = Time.getJulianDay(TimeUtil.toMillis(day, TimeUtil.UTC), 0);

        Uri.Builder instancesUriBuilder = CalendarContract.Instances.CONTENT_BY_DAY_URI.buildUpon();
        ContentUris.appendId(instancesUriBuilder, start);
        ContentUris.appendId(instancesUriBuilder, end);
        Uri instancesUri = instancesUriBuilder.build();

        Cursor cur = contentResolver.query(
                instancesUri,
                getProjection(),
                null,
                null,
                null
        );

        while (cur.moveToNext()) {
            events.add(toEvent(cur));
        }

        cur.close();
        Collections.sort(events);

        events.forEach(event -> Log.i(getClass().getSimpleName(), String.format("getEvents for day %s found event: %s", day, event.toString())));

        Log.i(getClass().getSimpleName(), String.format("getEvents for day %s finished with %d events!", day, events.size()));
        return events;
    }
}
