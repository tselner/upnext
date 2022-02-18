package io.those.upnext.repository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CalendarContract;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    public List<UpNextEvent> getEvents(UpNextCalendar calendar, LocalDate start, LocalDate end) {
        List<UpNextEvent> events = new ArrayList<>();

        Cursor cur = getContentResolver().query(
                CalendarContract.Events.CONTENT_URI, REQUESTED_EVENT_FIELDS,
                EVENT_SELECTION,
                new String[]{
                        calendar.getId().toString(),
                        String.valueOf((TimeUtil.toMillis(end.atTime(LocalTime.MAX), UTC))), // start <= END of range
                        String.valueOf(TimeUtil.toMillis(start.atTime(LocalTime.MIN), UTC))            // end >= START of range
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
                    cur.getString(6)
            ));
        }

        cur.close();

        Collections.sort(events);
        return events;
    }

    public List<UpNextEvent> getEventsByDateRange(List<UpNextCalendar> cals, LocalDate start, LocalDate end, ZoneId zoneId) {
        List<UpNextEvent> rangeEvents = new ArrayList<>();
        cals.forEach(cal -> rangeEvents.addAll(getEvents(cal,start, end)));
        Collections.sort(rangeEvents);
        return rangeEvents;
    }

    public List<UpNextEvent> getEventsForTesting(LocalDate day, List<UpNextCalendar> cals, int countEvents) {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        ZonedDateTime zonedStartOfDay = ZonedDateTime.of(day.atStartOfDay(), ZoneId.systemDefault());
        long startOfDayMillis = zonedStartOfDay.toInstant().toEpochMilli();

        List<UpNextEvent> events = new ArrayList<>();

        for (int i = 0; i < countEvents; i++) {
            long startInMillis = 0L;
            long endInMillis = 0L;
            boolean allDay = false;

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
            events.add(UpNextEvent.of(String.valueOf(i), randCal, title, "This is an event.", allDay, startInMillis, endInMillis, tz.getID()));
        }

        Collections.sort(events);
        return events;
    }

    private String generateTitle(int i) {
        return "Event " + i;
    }
}
