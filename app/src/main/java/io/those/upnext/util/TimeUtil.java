package io.those.upnext.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtil {
    public static long toMillis(LocalDate date, ZoneId zoneId) {
        return toMillis(date.atStartOfDay(), zoneId);
    }

    public static long toMillis(LocalDateTime dateTime, ZoneId zoneId) {
        ZonedDateTime zdt = ZonedDateTime.of(dateTime, zoneId);
        return zdt.toInstant().toEpochMilli();
    }

    public static LocalDate toDate(long millis, ZoneId zoneId) {
        return toDateTime(millis, zoneId).toLocalDate();
    }

    public static LocalDateTime toDateTime(long millis, ZoneId zoneId) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), zoneId);
    }
}
