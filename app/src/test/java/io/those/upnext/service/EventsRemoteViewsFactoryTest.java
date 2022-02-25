package io.those.upnext.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static io.those.upnext.service.EventsService.EXTRA_DATE_PATTERN;
import static io.those.upnext.service.EventsService.EXTRA_END;
import static io.those.upnext.service.EventsService.EXTRA_IS_TODAY_EVENT;
import static io.those.upnext.service.EventsService.EXTRA_START;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.those.upnext.model.UpNextCalendar;
import io.those.upnext.model.UpNextEvent;
import io.those.upnext.repository.CalendarRepository;
import io.those.upnext.repository.EventRepository;

public class EventsRemoteViewsFactoryTest {
    private static final LocalDate DAY1 = LocalDate.of(2022, Month.JANUARY, 1);
    private static final LocalDate DAY2 = LocalDate.of(2022, Month.JANUARY, 2);
    private static final LocalDate DAY3 = LocalDate.of(2022, Month.JANUARY, 3);

    private static final UpNextCalendar CAL1 = UpNextCalendar.of(4711L, "Cal1", "", "Calendar 1", Color.BLUE, true);

    private static final UpNextEvent EVENT1 = UpNextEvent.of("1", CAL1, "Event1", "", true, null, null, "UTC", DAY1);
    private static final UpNextEvent EVENT2 = UpNextEvent.of("2", CAL1, "Event2", "", true, null, null, "UTC", DAY1);
    private static final UpNextEvent EVENT3 = UpNextEvent.of("3", CAL1, "Event3", "", true, null, null, "UTC", DAY2);
    private static final UpNextEvent EVENT4 = UpNextEvent.of("4", CAL1, "Event4", "", true, null, null, "UTC", DAY2);
    private static final UpNextEvent EVENT5 = UpNextEvent.of("5", CAL1, "Event5", "", true, null, null, "UTC", DAY2);
    private static final UpNextEvent EVENT6 = UpNextEvent.of("6", CAL1, "Event6", "", true, null, null, "UTC", DAY3);

    private static final List<LocalDate> DAYS = Arrays.asList(
            DAY1,
            DAY2,
            DAY3
    );

    private static final Map<LocalDate, List<UpNextEvent>> EVENTS_PER_DAY = new HashMap<>();

    static {
        EVENTS_PER_DAY.put(DAY1, Arrays.asList(EVENT1, EVENT2, EVENT3));
        EVENTS_PER_DAY.put(DAY2, Arrays.asList(EVENT3, EVENT4, EVENT5));
        EVENTS_PER_DAY.put(DAY3, Arrays.asList(EVENT4, EVENT5, EVENT6));
    }

    private EventsService.EventsRemoteViewsFactory newInstance() {

        ContentResolver contentResolver = mock(ContentResolver.class);

        Context context = mock(Context.class);
        doReturn(contentResolver).when(context).getContentResolver();

        Intent intent = mock(Intent.class);
        doReturn(DAY1.format(DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN))).when(intent).getStringExtra(eq(EXTRA_START));
        doReturn(DAY3.format(DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN))).when(intent).getStringExtra(eq(EXTRA_END));
        doReturn(true).when(intent).getBooleanExtra(eq(EXTRA_IS_TODAY_EVENT), eq(false));

        EventsService.EventsRemoteViewsFactory factory = spy(new EventsService.EventsRemoteViewsFactory(context, intent));

        CalendarRepository calRep = mock(CalendarRepository.class);
        doReturn(Collections.singletonList(CAL1)).when(calRep).getCalendars();
        doReturn(calRep).when(factory).getCalendarRepositoryInstance(nullable(Context.class));

        EventRepository eveRep = mock(EventRepository.class);
        DAYS.forEach(day -> doReturn(EVENTS_PER_DAY.get(day))
                .when(eveRep).getEventsByDay(any(), eq(day), nullable(ZoneId.class)));
        doReturn(eveRep).when(factory).getEventRepositoryInstance(nullable(Context.class));

        factory.onCreate();
        return factory;
    }

    @Test
    public void newInstance_works() {
        assertNotNull(newInstance());
    }
}
