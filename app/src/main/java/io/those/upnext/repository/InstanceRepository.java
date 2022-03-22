package io.those.upnext.repository;

import static android.provider.CalendarContract.Instances;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.those.upnext.model.UpNextEvent;
import io.those.upnext.util.TimeUtil;

public class InstanceRepository {
    private final ContentResolver contentResolver;

    public InstanceRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public List<UpNextEvent> getInstances(LocalDate day) {
        Log.i(InstanceRepository.class.getSimpleName(), String.format("getInstances on day %s ...", day.format(DateTimeFormatter.BASIC_ISO_DATE)));
        List<UpNextEvent> events = getInstances(Time.getJulianDay(TimeUtil.toMillis(day.atTime(LocalTime.MIN), ZoneId.of("UTC")), 0L),1);
        Log.i(EventRepository.class.getSimpleName(), String.format("getInstances on day %s finished with %d instances!", day.format(DateTimeFormatter.BASIC_ISO_DATE), events.size()));
        return events;
    }

    private List<UpNextEvent> getInstances(int startDay, int days) {
        List<UpNextEvent> events = new ArrayList<>();
        Cursor cInstances = null;

        try {
            int endDay = startDay + days - 1;

            Uri.Builder builder = Instances.CONTENT_BY_DAY_URI.buildUpon();
            ContentUris.appendId(builder, startDay);
            ContentUris.appendId(builder, endDay);
            cInstances = contentResolver.query(builder.build(), UpNextEvent.Projections.getProjection(), null, null, null);

            while (cInstances.moveToNext()) {
                events.add(UpNextEvent.toEvent(cInstances));
            }

        } finally {
            if (cInstances != null) {
                cInstances.close();
            }
        }

        Collections.sort(events);
        return events;
    }
}
