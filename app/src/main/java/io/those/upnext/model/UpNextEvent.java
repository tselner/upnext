package io.those.upnext.model;

import static android.provider.CalendarContract.Instances;
import static io.those.upnext.util.TimeUtil.toDateTime;

import android.database.Cursor;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class UpNextEvent extends UpNextListElement implements Comparable<UpNextEvent> {
    private final DateTimeFormatter formatter_time = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter formatter_datetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public long event_id;
    public long instance_id;
    public int color;
    public String timezone;
    public String title;
    public boolean allDay;
    public long startMillis;   // UTC milliseconds since the epoch
    public long endMillis;     // UTC milliseconds since the epoch
    public String description;

    @Override
    public boolean equals(Object otherEventObject) {
        if (otherEventObject instanceof UpNextEvent) {
            UpNextEvent otherEvent = (UpNextEvent) otherEventObject;
            return (event_id==otherEvent.event_id) && (instance_id==otherEvent.instance_id);
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(UpNextEvent e) {
        return new CompareToBuilder()
                .append(this.isSubDay(), e.isSubDay())
                .append(this.startMillis, e.startMillis)
                .append(this.endMillis, e.endMillis)
                .append(this.event_id, e.event_id)
                .append(this.instance_id, e.instance_id)
                .toComparison();
    }

    public boolean isSubDay() {
        return !allDay;
    }

    public String getDuration() {
        return String.format("%s - %s", getStartAsString(), getEndAsString());
    }

    public String getStartAsString() {
        if (getStart() != null) {
            return getStart().format(formatter_time);
        } else {
            return "";
        }
    }

    public String getEndAsString() {
        if (getEnd() != null) {
            return getEnd().format(formatter_time);
        } else {
            return "";
        }
    }

    public LocalDateTime getStart() {
        return millisToLocalDateTime(startMillis);
    }

    public LocalDateTime getEnd() {
        return millisToLocalDateTime(endMillis);
    }

    private LocalDateTime millisToLocalDateTime(Long millis) {
        return millis != null ? toDateTime(millis, ZoneId.systemDefault()) : null;
    }

    public enum Projections {
        EVENT_ID      (0, Instances.EVENT_ID),
        INSTANCE_ID   (1, Instances._ID),
        CALENDAR_COLOR(2, Instances.CALENDAR_COLOR),
        TIMEZONE      (3, Instances.EVENT_TIMEZONE),
        TITLE         (4, Instances.TITLE),
        ALL_DAY       (5, Instances.ALL_DAY),
        BEGIN         (6, Instances.BEGIN),
        END           (7, Instances.END),
        DESCRIPTION   (8, Instances.DESCRIPTION);

        private final int position;
        private final String field;

        Projections(int position, String field) {
            this.position = position;
            this.field = field;
        }

        public static String[] getProjection() {
            return Arrays.stream(values()).map(Projections::getField).toArray(String[]::new);
        }

        public int getPosition() {
            return position;
        }

        public String getField() {
            return field;
        }
    }

    public static UpNextEvent toEvent(Cursor cEvent) {
        UpNextEvent event = new UpNextEvent();

        if (cEvent != null) {
            event.event_id    = cEvent.getLong  (Projections.EVENT_ID.getPosition());
            event.instance_id = cEvent.getLong  (Projections.INSTANCE_ID.getPosition());
            event.color       = cEvent.getInt   (Projections.CALENDAR_COLOR.getPosition());
            event.timezone    = cEvent.getString(Projections.TIMEZONE.getPosition());
            event.title       = cEvent.getString(Projections.TITLE.getPosition());
            event.allDay      = cEvent.getInt   (Projections.ALL_DAY.getPosition()) != 0;
            event.startMillis = cEvent.getLong  (Projections.BEGIN.getPosition());
            event.endMillis   = cEvent.getLong  (Projections.END.getPosition());
            event.description = cEvent.getString(Projections.DESCRIPTION.getPosition());
        }

        return event;
    }
}
