package io.those.upnext.model;

import static io.those.upnext.util.TimeUtil.toDateTime;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UpNextEvent extends UpNextListElement implements Comparable<UpNextEvent> {
    private final DateTimeFormatter formatter_time = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter formatter_datetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String id;
    private final String title;
    private final String description;
    private final boolean allDay;
    private final Long startInMillis;
    private final Long endInMillis;
    private final String timezone;
    private final UpNextCalendar calendar;

    public LocalDateTime getStart() {
        return millisToLocalDateTime(getStartInMillis());
    }

    public LocalDateTime getEnd() {
        return millisToLocalDateTime(getEndInMillis());
    }

    private LocalDateTime millisToLocalDateTime(Long millis) {
        return millis != null ? toDateTime(millis, ZoneId.systemDefault()) : null;
    }

    public static UpNextEvent of(String id, String title, String description, Boolean allDay, Long startInMillis, Long endInMillis, String timezone, UpNextCalendar calendar) {
        return new UpNextEvent(id, title, description, allDay, startInMillis, endInMillis, timezone, calendar);
    }

    private UpNextEvent(String id, String title, String description, Boolean allDay, Long startInMillis, Long endInMillis, String timezone, UpNextCalendar calendar) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.allDay = allDay;
        this.startInMillis = startInMillis;
        this.endInMillis = endInMillis;
        this.timezone = timezone;
        this.calendar = calendar;
    }

    @Override
    public boolean equals(Object otherEventObject) {
        if (otherEventObject instanceof UpNextEvent) {
            UpNextEvent otherEvent = (UpNextEvent) otherEventObject;
            return (getId().equals(otherEvent.getId()) && getCalendar().equals(otherEvent.getCalendar()));
        } else {
            return false;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("[allDay=%s, start=%s, end=%s] %s", Boolean.TRUE.equals(isAllDay()) ? "YES" : "NO ", getStartWithDateAsString(), getEndWithDateAsString(), getTitle());
    }

    public String getStartAsString() {
        if (getStart() != null) {
            return getStart().format(formatter_time);
        } else {
            return "";
        }
    }

    public String getStartWithDateAsString() {
        if (getStart() != null) {
            return getStart().format(formatter_datetime);
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

    public String getEndWithDateAsString() {
        if (getEnd() != null) {
            return getEnd().format(formatter_datetime);
        } else {
            return "";
        }
    }

    public String getDuration() {
        return String.format("%s - %s", getStartAsString(), getEndAsString());
    }

    @Override
    public int compareTo(UpNextEvent e) {
        return new CompareToBuilder()
                .append(this.isSubDay(), e.isSubDay())
                .append(this.getCalendar(), e.getCalendar())
                .append(this.getStartInMillis(), e.getStartInMillis())
                .append(this.getEndInMillis(), e.getEndInMillis())
                .append(this.getTitle(), e.getTitle())
                .toComparison();
    }

    public int getColor() {
        return getCalendar().getColor();
    }

    public String getId() {
        return id;
    }

    public UpNextCalendar getCalendar() {
        return calendar;
    }

    public String getTitle() {
        return title;
    }

    public Long getStartInMillis() {
        return startInMillis;
    }

    public Long getEndInMillis() {
        return endInMillis;
    }

    public String getDescription() {
        return description;
    }

    public String getTimezone() {
        return timezone;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public boolean isSubDay() {
        return !isAllDay();
    }
}
