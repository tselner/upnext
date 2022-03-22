package io.those.upnext.model;

import static io.those.upnext.util.TimeUtil.toDateTime;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UpNextEvent extends UpNextListElement implements Comparable<UpNextEvent> {
    private final DateTimeFormatter formatter_time = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter formatter_datetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public long id;
    public long instance_id;
    public int color;
    public String timezone;
    public CharSequence title;
    public CharSequence location;
    public boolean allDay;
    public String organizer;
    public boolean guestsCanModify;

    public int startDay;       // start Julian day
    public int endDay;         // end Julian day
    public int startTime;      // Start and end time are in minutes since midnight
    public int endTime;

    public long startMillis;   // UTC milliseconds since the epoch
    public long endMillis;     // UTC milliseconds since the epoch

    public boolean hasAlarm;
    public boolean isRepeating;

    public int selfAttendeeStatus;

    @Override
    public boolean equals(Object otherEventObject) {
        if (otherEventObject instanceof UpNextEvent) {
            UpNextEvent otherEvent = (UpNextEvent) otherEventObject;
            return (id==otherEvent.id);
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(UpNextEvent e) {
        return CompareToBuilder.reflectionCompare(this, e);
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
}
