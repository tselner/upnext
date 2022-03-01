package io.those.upnext.repository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.those.upnext.model.UpNextEvent;
import io.those.upnext.util.TimeUtil;

public class InstanceRepository extends Repository<UpNextEvent> {
    public InstanceRepository(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                /* 0 */ CalendarContract.Instances.EVENT_ID,    // String
                /* 4 */ CalendarContract.Instances.BEGIN,       // Long
                /* 5 */ CalendarContract.Instances.END,         // Long
        };
    }

    @Override
    protected String getSelection() {
        return "(" +
                     "(" + CalendarContract.Instances.BEGIN   + " < ?)" + // must be less than END of range
                " AND (" + CalendarContract.Instances.END     + " > ?)" + // must be greater than START of range
                ")";
    }

    @Override
    protected Uri getProviderUri() {
        return CalendarContract.Instances.CONTENT_URI;
    }

    @Override
    protected UpNextEvent toItem(Cursor cur) {
        return UpNextEvent.of(
                cur.getString(0),
                "instance",
                "this is an instance",
                false,
                cur.getLong(4),
                cur.getLong(5),
                null
        );
    }

    public List<UpNextEvent> getInstances(LocalDate day) {
        Log.i(EventRepository.class.getSimpleName(), String.format("getInstances for day %s ...", day.format(DateTimeFormatter.BASIC_ISO_DATE)));

        List<UpNextEvent> instances = getItems(new String[]{
                String.valueOf((TimeUtil.toMillis(day.atTime(LocalTime.MAX), TimeUtil.UTC))), // start <= END of range
                String.valueOf(TimeUtil.toMillis(day.atTime(LocalTime.MIN), TimeUtil.UTC))    // end >= START of range
        });

        instances.forEach(event -> event.setDay(day));

        Log.i(EventRepository.class.getSimpleName(), String.format("getInstances for day %s finished with %d instances!", day.format(DateTimeFormatter.BASIC_ISO_DATE), instances.size()));
        return instances;
    }
}
