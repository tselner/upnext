package io.those.upnext.repository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.those.upnext.model.UpNextCalendar;
import io.those.upnext.model.UpNextEvent;
import io.those.upnext.util.TimeUtil;

public class EventRepository extends Repository<UpNextEvent> {

    public EventRepository(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                /* 0 */ CalendarContract.Events._ID,           // String
                /* 1 */ CalendarContract.Events.TITLE,         // String
                /* 2 */ CalendarContract.Events.DESCRIPTION,   // String
                /* 3 */ CalendarContract.Events.ALL_DAY,       // Integer (boolean)
                /* 4 */ CalendarContract.Events.DTSTART,       // Long
                /* 5 */ CalendarContract.Events.DTEND,         // Long
                /* 5 */ CalendarContract.Events.EVENT_TIMEZONE // String
        };
    }

    @Override
    protected String getSelection() {
        return "(" +
                     "(" + CalendarContract.Events.CALENDAR_ID + " = ?)" +
                " AND (" + CalendarContract.Events.DTSTART     + " < ?)" + // must be less than END of range
                " AND (" + CalendarContract.Events.DTEND       + " > ?)" + // must be greater than START of range
               ")";
    }

    @Override
    protected Uri getProviderUri() {
        return CalendarContract.Events.CONTENT_URI;
    }

    @Override
    protected UpNextEvent toItem(Cursor cur) {
        return UpNextEvent.of(
                cur.getString(0),
                cur.getString(1),
                cur.getString(2),
                cur.getInt(3) == 1 ? Boolean.TRUE : Boolean.FALSE,
                cur.getLong(4),
                cur.getLong(5),
                cur.getString(6)
        );
    }

    public List<UpNextEvent> getEvents(UpNextCalendar calendar, LocalDate day) {
        Log.i(EventRepository.class.getSimpleName(), String.format("getEvents for calendar %s on day %s ...", calendar, day.format(DateTimeFormatter.BASIC_ISO_DATE)));

        List<UpNextEvent> events = getItems(new String[]{
                calendar.getId().toString(),
                String.valueOf((TimeUtil.toMillis(day.atTime(LocalTime.MAX), TimeUtil.UTC))), // start <= END of range
                String.valueOf(TimeUtil.toMillis(day.atTime(LocalTime.MIN), TimeUtil.UTC))    // end >= START of range
        });

        events.forEach(event -> {
            event.setCalendar(calendar);
            event.setDay(day);
        });

        Log.i(EventRepository.class.getSimpleName(), String.format("getEvents for calendar %s on day %s finished with %d events!", calendar, day.format(DateTimeFormatter.BASIC_ISO_DATE), events.size()));
        return events;
    }

    public List<UpNextEvent> getEventsByDay(List<UpNextCalendar> cals, LocalDate day) {
        List<UpNextEvent> rangeEvents = new ArrayList<>();
        cals.forEach(cal -> rangeEvents.addAll(getEvents(cal, day)));
        Collections.sort(rangeEvents);
        return rangeEvents;
    }
}
