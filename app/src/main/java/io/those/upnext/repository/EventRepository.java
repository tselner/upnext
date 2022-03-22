package io.those.upnext.repository;

import static android.provider.CalendarContract.Events;

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
                /* 0 */ Events._ID,            // String
                /* 1 */ Events.TITLE,          // String
                /* 2 */ Events.DESCRIPTION,    // String
                /* 3 */ Events.ALL_DAY,        // Integer (boolean)
                /* 4 */ Events.DTSTART,        // Long
                /* 5 */ Events.DTEND,          // Long
                /* 6 */ Events.EVENT_TIMEZONE, // String
                /* 7 */ Events.CALENDAR_COLOR
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
        UpNextEvent event = new UpNextEvent();
        event.event_id    = Long.parseLong(cur.getString(0));
        event.title       = cur.getString(1);
        event.description = cur.getString(2);
        event.allDay      = cur.getInt(3) == 1 ? Boolean.TRUE : Boolean.FALSE;
        event.startMillis = cur.getLong(4);
        event.endMillis   = cur.getLong(5);
        event.timezone    = cur.getString(6);
        event.color       = cur.getInt(7);
        return event;
    }

    @Override
    protected String getSortOrder() {
        return null;
    }

    public List<UpNextEvent> getEvents(UpNextCalendar calendar, LocalDate day) {
        Log.i(EventRepository.class.getSimpleName(), String.format("getEvents for calendar %s on day %s ...", calendar, day.format(DateTimeFormatter.BASIC_ISO_DATE)));

        List<UpNextEvent> events = getItems(new String[]{
                calendar.getId().toString(),
                String.valueOf((TimeUtil.toMillis(day.atTime(LocalTime.MAX), TimeUtil.UTC))), // start < END of day
                String.valueOf(TimeUtil.toMillis(day.atTime(LocalTime.MIN), TimeUtil.UTC))    // end > START of day
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