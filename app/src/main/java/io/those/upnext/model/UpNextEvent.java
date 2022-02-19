package io.those.upnext.model;

import static io.those.upnext.util.TimeUtil.toDateTime;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UpNextEvent implements Comparable<UpNextEvent> {
    private static final int ALPHA_FOR_EVENT_BACKGROUND = 0x40000000;
    private final DateTimeFormatter formatter_time = DateTimeFormatter.ofPattern("HH:mm");

    private final String id;
    private final UpNextCalendar calendar;
    private final String title;
    private final String description;
    private final Boolean allDay;
    private final Long startInMillis;
    private final Long endInMillis;
    private final String timezone;
    private final LocalDate day; // the day, for which the event has been read and will be displayed

    public LocalDateTime getStart() {
        return millisToLocalDateTime(getStartInMillis());
    }

    public LocalDateTime getEnd() {
        return millisToLocalDateTime(getEndInMillis());
    }

    private LocalDateTime millisToLocalDateTime(Long millis) {
        return millis != null ? toDateTime(millis, getTimezone() != null ? ZoneId.of(getTimezone()) : ZoneId.systemDefault()) : null;
    }

    public static UpNextEvent of(String id, UpNextCalendar calendar, String title, String description, Boolean allDay, Long startInMillis, Long endInMillis, String timezone, LocalDate day) {
        return new UpNextEvent(id, calendar, title, description, allDay, startInMillis, endInMillis, timezone, day);
    }

    private UpNextEvent(String id, UpNextCalendar calendar, String title, String description, Boolean allDay, Long startInMillis, Long endInMillis, String timezone, LocalDate day) {
        this.id = id;
        this.calendar = calendar;
        this.title = title;
        this.description = description;
        this.allDay = allDay;
        this.startInMillis = startInMillis;
        this.endInMillis = endInMillis;
        this.timezone = timezone;
        this.day = day;
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

    public boolean isAllDay() {
        return Boolean.TRUE.equals(getAllDay());
    }

    @NonNull
    @Override
    public String toString() {
        String toString = getTitle();

        if (getDuration().length() > 0) {
            toString += String.format(" (%s)", getDuration());
        }

        return toString;
    }

    public String getDuration() {
        if (getStart() != null && getEnd() != null) {
            String start = getStart().format(formatter_time);
            String end = getEnd().format(formatter_time);
            return String.format("%s - %s", start, end);
        } else {
            return "";
        }
    }

    @Override
    public int compareTo(UpNextEvent e) {
        return new CompareToBuilder()
                .append(this.getStartInMillis(), e.getStartInMillis())
                .append(this.getEndInMillis(), e.getEndInMillis())
                .append(this.getTimezone(), e.getTimezone())
                .append(this.getTitle(), e.getTitle())
                .append(this.getCalendar(), e.getCalendar())
                .toComparison();
    }

    public int getColor() {
        return getCalendar().getColor();
    }

    public int getAlpha() {
        return ALPHA_FOR_EVENT_BACKGROUND;
    }

    public int getAlphaColor() {
        return (getColor() & 0x00FFFFFF) | ALPHA_FOR_EVENT_BACKGROUND;
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

    public String getDescription() {
        return description;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public Long getStartInMillis() {
        return startInMillis;
    }

    public Long getEndInMillis() {
        return endInMillis;
    }

    public String getTimezone() {
        return timezone;
    }

    public LocalDate getDay() {
        return day;
    }
}
