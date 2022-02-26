package io.those.upnext.repository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.CalendarContract.Calendars;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.those.upnext.model.UpNextCalendar;

public class CalendarRepository extends Repository {
    private static final String[] REQUESTED_CALENDAR_FIELDS = new String[]{
            Calendars._ID,                   // 0
            Calendars.NAME,                  // 1
            Calendars.ACCOUNT_NAME,          // 2
            Calendars.CALENDAR_DISPLAY_NAME, // 3
            Calendars.CALENDAR_COLOR,        // 4
            Calendars.VISIBLE                // 5
    };

    public CalendarRepository(ContentResolver contentResolver) {
        super(contentResolver);
    }

    public List<UpNextCalendar> getCalendars() {
        Log.i(CalendarRepository.class.getSimpleName(), "getCalendars ...");
        List<UpNextCalendar> calendars = new ArrayList<>();

        Cursor cur = getContentResolver().query(Calendars.CONTENT_URI, REQUESTED_CALENDAR_FIELDS, null, null, null);

        while (cur.moveToNext()) {
            calendars.add(UpNextCalendar.of(
                    cur.getLong(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getInt(4),
                    cur.getInt(5) == 1 ? Boolean.TRUE : Boolean.FALSE
            ));
        }

        cur.close();

        Collections.sort(calendars);
        Log.i(CalendarRepository.class.getSimpleName(), String.format("getCalendars finished with %d calendars!", calendars.size()));
        return calendars;
    }

    public List<UpNextCalendar> getCalendarsForTesting() {
        List<UpNextCalendar> calendars = new ArrayList<>();

        calendars.add(UpNextCalendar.of(1L, "cal1", "thomas.selner", "Calendar 1", Color.parseColor("#FFE76F51"), true));
        calendars.add(UpNextCalendar.of(2L, "cal2", "thomas.selner", "Calendar 2", Color.parseColor("#FF2A9D8F"), true));
        calendars.add(UpNextCalendar.of(3L, "cal3", "thomas.selner", "Calendar 3", Color.parseColor("#FFE9C46A"), true));

        Collections.sort(calendars);
        return calendars;
    }
}