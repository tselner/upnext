package io.those.upnext.repository;

import static android.provider.CalendarContract.Calendars;
import static android.provider.CalendarContract.Events;
import static android.provider.CalendarContract.Instances;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.those.upnext.model.UpNextEvent;
import io.those.upnext.util.TimeUtil;

public class EventRepository {
    private final ContentResolver contentResolver;

    public EventRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }
    private static final String SORT_EVENTS_BY =
            "begin ASC, end DESC, title ASC";
    private static final String SORT_ALLDAY_BY =
            "startDay ASC, endDay DESC, title ASC";
    private static final String DISPLAY_AS_ALLDAY = "dispAllday";

    private static final String EVENTS_WHERE = DISPLAY_AS_ALLDAY + "=0";
    private static final String ALLDAY_WHERE = DISPLAY_AS_ALLDAY + "=1";

    // The projection to use when querying instances to build a list of events
    public static final String[] EVENT_PROJECTION = new String[] {
            Instances.TITLE,                 // 0
            Instances.EVENT_LOCATION,        // 1
            Instances.ALL_DAY,               // 2
            Instances.CALENDAR_COLOR,        // 3
            Instances.EVENT_TIMEZONE,        // 4
            Instances.EVENT_ID,              // 5
            Instances.BEGIN,                 // 6
            Instances.END,                   // 7
            Instances._ID,                   // 8
            Instances.START_DAY,             // 9
            Instances.END_DAY,               // 10
            Instances.START_MINUTE,          // 11
            Instances.END_MINUTE,            // 12
            Instances.HAS_ALARM,             // 13
            Instances.RRULE,                 // 14
            Instances.RDATE,                 // 15
            Instances.SELF_ATTENDEE_STATUS,  // 16
            Events.ORGANIZER,                // 17
            Events.GUESTS_CAN_MODIFY,        // 18
            Instances.ALL_DAY + "=1 OR (" + Instances.END + "-" + Instances.BEGIN + ")>="
                    + DateUtils.DAY_IN_MILLIS + " AS " + DISPLAY_AS_ALLDAY, // 19
    };

    // The indices for the projection array above.
    private static final int PROJECTION_TITLE_INDEX = 0;
    private static final int PROJECTION_LOCATION_INDEX = 1;
    private static final int PROJECTION_ALL_DAY_INDEX = 2;
    private static final int PROJECTION_COLOR_INDEX = 3;
    private static final int PROJECTION_TIMEZONE_INDEX = 4;
    private static final int PROJECTION_EVENT_ID_INDEX = 5;
    private static final int PROJECTION_BEGIN_INDEX = 6;
    private static final int PROJECTION_END_INDEX = 7;
    private static final int PROJECTION_INSTANCE_ID_INDEX = 8;
    private static final int PROJECTION_START_DAY_INDEX = 9;
    private static final int PROJECTION_END_DAY_INDEX = 10;
    private static final int PROJECTION_START_MINUTE_INDEX = 11;
    private static final int PROJECTION_END_MINUTE_INDEX = 12;
    private static final int PROJECTION_HAS_ALARM_INDEX = 13;
    private static final int PROJECTION_RRULE_INDEX = 14;
    private static final int PROJECTION_RDATE_INDEX = 15;
    private static final int PROJECTION_SELF_ATTENDEE_STATUS_INDEX = 16;
    private static final int PROJECTION_ORGANIZER_INDEX = 17;
    private static final int PROJECTION_GUESTS_CAN_INVITE_OTHERS_INDEX = 18;

    public List<UpNextEvent> loadEvents(LocalDate day) {
        return loadEvents(Time.getJulianDay(TimeUtil.toMillis(day.atTime(LocalTime.MIN), ZoneId.of("UTC")), 0L),1);
    }

    private List<UpNextEvent> loadEvents(int startDay, int days) {
        Cursor cEvents = null;
        Cursor cAllday = null;
        List<UpNextEvent> events = new ArrayList<>();

        try {
            int endDay = startDay + days - 1;

            cAllday = instancesQuery(contentResolver, startDay, endDay, ALLDAY_WHERE, null, SORT_ALLDAY_BY);
            cEvents = instancesQuery(contentResolver, startDay, endDay, EVENTS_WHERE, null, SORT_EVENTS_BY);

            buildEventsFromCursor(events, cAllday, startDay, endDay);
            buildEventsFromCursor(events, cEvents, startDay, endDay);

        } finally {
            if (cEvents != null) {
                cEvents.close();
            }
            if (cAllday != null) {
                cAllday.close();
            }
        }

        return events;
    }

    private Cursor instancesQuery(ContentResolver cr, int startDay, int endDay, String selection, String[] selectionArgs, String orderBy) {
        String WHERE_CALENDARS_SELECTED = Calendars.VISIBLE + "=?";
        String[] WHERE_CALENDARS_ARGS = {"1"};
        String DEFAULT_SORT_ORDER = "begin ASC";

        Uri.Builder builder = Instances.CONTENT_BY_DAY_URI.buildUpon();
        ContentUris.appendId(builder, startDay);
        ContentUris.appendId(builder, endDay);
        if (TextUtils.isEmpty(selection)) {
            selection = WHERE_CALENDARS_SELECTED;
            selectionArgs = WHERE_CALENDARS_ARGS;
        } else {
            selection = "(" + selection + ") AND " + WHERE_CALENDARS_SELECTED;
            if (selectionArgs != null && selectionArgs.length > 0) {
                selectionArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 1);
                selectionArgs[selectionArgs.length - 1] = WHERE_CALENDARS_ARGS[0];
            } else {
                selectionArgs = WHERE_CALENDARS_ARGS;
            }
        }
        return cr.query(builder.build(), EVENT_PROJECTION, selection, selectionArgs,
                orderBy == null ? DEFAULT_SORT_ORDER : orderBy);
    }

    private void buildEventsFromCursor(
            List<UpNextEvent> events, Cursor cEvents, int startDay, int endDay) {
        if (cEvents == null || events == null) {
            Log.e(EventRepository.class.getSimpleName(), "buildEventsFromCursor: null cursor or null events list!");
            return;
        }

        int count = cEvents.getCount();

        if (count == 0) {
            return;
        }

        // Sort events in two passes so we ensure the allday and standard events
        // get sorted in the correct order
        cEvents.moveToPosition(-1);
        while (cEvents.moveToNext()) {
            UpNextEvent e = generateEventFromCursor(cEvents);
            if (e.startDay > endDay || e.endDay < startDay) {
                continue;
            }
            events.add(e);
        }
    }
    private UpNextEvent generateEventFromCursor(Cursor cEvents) {
        UpNextEvent e = new UpNextEvent();

        e.id = cEvents.getLong(PROJECTION_EVENT_ID_INDEX);
        e.title = cEvents.getString(PROJECTION_TITLE_INDEX);
        e.location = cEvents.getString(PROJECTION_LOCATION_INDEX);
        e.allDay = cEvents.getInt(PROJECTION_ALL_DAY_INDEX) != 0;
        e.organizer = cEvents.getString(PROJECTION_ORGANIZER_INDEX);
        e.guestsCanModify = cEvents.getInt(PROJECTION_GUESTS_CAN_INVITE_OTHERS_INDEX) != 0;

        if (e.title == null || e.title.length() == 0) {
            e.title = "no title";
        }

        if (!cEvents.isNull(PROJECTION_COLOR_INDEX)) {
            e.color = cEvents.getInt(PROJECTION_COLOR_INDEX);
        } else {
            e.color = Color.parseColor("#000000");
        }

        e.timezone = cEvents.getString(PROJECTION_TIMEZONE_INDEX);

        long eStart = cEvents.getLong(PROJECTION_BEGIN_INDEX);
        long eEnd = cEvents.getLong(PROJECTION_END_INDEX);

        e.instance_id = cEvents.getLong(PROJECTION_INSTANCE_ID_INDEX);

        e.startMillis = eStart;
        e.startTime = cEvents.getInt(PROJECTION_START_MINUTE_INDEX);
        e.startDay = cEvents.getInt(PROJECTION_START_DAY_INDEX);

        e.endMillis = eEnd;
        e.endTime = cEvents.getInt(PROJECTION_END_MINUTE_INDEX);
        e.endDay = cEvents.getInt(PROJECTION_END_DAY_INDEX);

        e.hasAlarm = cEvents.getInt(PROJECTION_HAS_ALARM_INDEX) != 0;

        // Check if this is a repeating event
        String rrule = cEvents.getString(PROJECTION_RRULE_INDEX);
        String rdate = cEvents.getString(PROJECTION_RDATE_INDEX);
        e.isRepeating = !TextUtils.isEmpty(rrule) || !TextUtils.isEmpty(rdate);

        e.selfAttendeeStatus = cEvents.getInt(PROJECTION_SELF_ATTENDEE_STATUS_INDEX);
        return e;
    }
}
