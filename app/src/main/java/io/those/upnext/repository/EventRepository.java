package io.those.upnext.repository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import io.those.upnext.model.UpNextCalendar;
import io.those.upnext.model.UpNextEvent;
import io.those.upnext.util.TimeUtil;

public class EventRepository extends Repository {
    private static final int HOUR_IN_MILLIS = 3600000;
    private static final ZoneId UTC = ZoneId.of("UTC");

    public EventRepository(ContentResolver contentResolver) {
        super(contentResolver);
    }

    private static final String[] REQUESTED_EVENT_FIELDS = new String[]{
            /* 0 */ CalendarContract.Events._ID,           // String
            /* 1 */ CalendarContract.Events.TITLE,         // String
            /* 2 */ CalendarContract.Events.DESCRIPTION,   // String
            /* 3 */ CalendarContract.Events.ALL_DAY,       // Integer (boolean)
            /* 4 */ CalendarContract.Events.DTSTART,       // Long
            /* 5 */ CalendarContract.Events.DTEND,         // Long
            /* 5 */ CalendarContract.Events.EVENT_TIMEZONE // String
    };

    private static final String EVENT_SELECTION =
            "(" +
                     "(" + CalendarContract.Events.CALENDAR_ID + " = ?)" +
                " AND (" + CalendarContract.Events.DTSTART     + " < ?)" + // must be less than END of range
                " AND (" + CalendarContract.Events.DTEND       + " > ?)" + // must be greater than START of range
            ")";

    public List<UpNextEvent> getEvents(UpNextCalendar calendar, LocalDate day) {
        Log.i(EventRepository.class.getSimpleName(), String.format("getEvents for calendar %s on day %s ...", calendar, day.format(DateTimeFormatter.BASIC_ISO_DATE)));
        List<UpNextEvent> events = new ArrayList<>();

        Cursor cur = getContentResolver().query(
                CalendarContract.Events.CONTENT_URI, REQUESTED_EVENT_FIELDS,
                EVENT_SELECTION,
                new String[]{
                        calendar.getId().toString(),
                        String.valueOf((TimeUtil.toMillis(day.atTime(LocalTime.MAX), UTC))), // start <= END of range
                        String.valueOf(TimeUtil.toMillis(day.atTime(LocalTime.MIN), UTC))            // end >= START of range
                },
                null);

        while (cur.moveToNext()) {
            events.add(UpNextEvent.of(
                    cur.getString(0),
                    calendar,
                    cur.getString(1),
                    cur.getString(2),
                    cur.getInt(3) == 1 ? Boolean.TRUE : Boolean.FALSE,
                    cur.getLong(4),
                    cur.getLong(5),
                    cur.getString(6),
                    day
            ));
        }

        cur.close();

        Collections.sort(events);
        Log.i(EventRepository.class.getSimpleName(), String.format("getEvents for calendar %s on day %s finished with %d events!", calendar, day.format(DateTimeFormatter.BASIC_ISO_DATE), events.size()));
        return events;
    }

    public List<UpNextEvent> getEventsByDay(List<UpNextCalendar> cals, LocalDate day, ZoneId zoneId) {
        List<UpNextEvent> rangeEvents = new ArrayList<>();
        cals.forEach(cal -> rangeEvents.addAll(getEvents(cal, day)));
        Collections.sort(rangeEvents);
        return rangeEvents;
    }

    public List<UpNextEvent> getEventsForTesting(LocalDate day, List<UpNextCalendar> cals, int countEvents) {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        ZonedDateTime zonedStartOfDay = ZonedDateTime.of(day.atStartOfDay(), ZoneId.systemDefault());
        long startOfDayMillis = zonedStartOfDay.toInstant().toEpochMilli();

        List<UpNextEvent> events = new ArrayList<>();

        for (int i = 0; i < countEvents; i++) {
            long startInMillis;
            long endInMillis;
            boolean allDay;

            if (i % 2 == 0) {
                // all day
                allDay = true;
                startInMillis = startOfDayMillis;
                endInMillis = startInMillis + (HOUR_IN_MILLIS * 24);
            } else {
                // one hour
                allDay = false;
                startInMillis = startOfDayMillis + ((long) i * HOUR_IN_MILLIS);
                endInMillis = startInMillis + HOUR_IN_MILLIS;
            }

            UpNextCalendar randCal = cals.get(i % cals.size());
            String title = generateTitle(i);
            events.add(UpNextEvent.of(String.valueOf(i), randCal, title, "This is an event.", allDay, startInMillis, endInMillis, tz.getID(), day));
        }

        Collections.sort(events);
        return events;
    }

    private String generateTitle(int i) {
        return "Event " + i;
    }
}
