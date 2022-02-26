package io.those.upnext.model;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDate;

public class UpNextDayLabel extends UpNextListElement {
    private static final String DATE_PATTERN = "EEEE, d. LLL";
    private final LocalDate day;

    public UpNextDayLabel(LocalDate day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return day != null ? day.format(ofPattern(DATE_PATTERN)) : "?";
    }
}
