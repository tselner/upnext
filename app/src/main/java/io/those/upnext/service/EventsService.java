package io.those.upnext.service;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.those.upnext.model.UpNextCalendar;
import io.those.upnext.model.UpNextEvent;
import io.those.upnext.remoteviews.EventViewCreator;
import io.those.upnext.repository.CalendarRepository;
import io.those.upnext.repository.EventRepository;

public class EventsService extends RemoteViewsService {
    public static final String EXTRA_START = "start";
    public static final String EXTRA_END = "end";
    public static final String EXTRA_WITH_DAY_LABELS = "withDayLabels";
    public static final String EXTRA_DATE_PATTERN = "yyy-MM-dd";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new EventsRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    static class EventsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private final Context context;
        private final LocalDate start;
        private final LocalDate end;
        private final boolean withDayLabels;
        private final List<UpNextEvent> events = new ArrayList<>();

        public EventsRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;

            this.start = LocalDate.parse(intent.getStringExtra(EXTRA_START), DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN));
            this.end   = LocalDate.parse(intent.getStringExtra(EXTRA_END)  , DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN));

            this.withDayLabels = intent.getBooleanExtra(EXTRA_WITH_DAY_LABELS, false);
        }

        CalendarRepository getCalendarRepositoryInstance(Context context) {
            return new CalendarRepository(context.getContentResolver());
        }

        EventRepository getEventRepositoryInstance(Context context) {
            return new EventRepository(context.getContentResolver());
        }

        @Override
        public void onCreate() {
            CalendarRepository calRep = getCalendarRepositoryInstance(context);
            EventRepository evtRep    = getEventRepositoryInstance(context);

            List<UpNextCalendar> cals = calRep.getCalendars();

            long numberOfDaysBetween = ChronoUnit.DAYS.between(start, end) + 1;
            List<LocalDate> days = IntStream.iterate(0, i -> i + 1)
                    .limit(numberOfDaysBetween)
                    .mapToObj(start::plusDays)
                    .collect(Collectors.toList());

            events.clear();
            days.forEach(day -> {
                List<UpNextEvent> eventsForThatDay = evtRep.getEventsByDay(cals, day, ZoneId.systemDefault());
                events.addAll(eventsForThatDay);
            });
        }

        @Override
        public void onDataSetChanged() {
            // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
            // on the collection view corresponding to this factory. You can do heaving lifting in
            // here, synchronously. For example, if you need to process an image, fetch something
            // from the network, etc., it is ok to do it here, synchronously. The widget will remain
            // in its current state while work is being done here, so you don't need to worry about
            // locking up the widget.
        }

        @Override
        public void onDestroy() {
            events.clear();
        }

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position < events.size()) {
                UpNextEvent lastEvent = position > 0 ? events.get(position - 1) : null;
                UpNextEvent currEvent = events.get(position);

                LocalDate lastDay = lastEvent != null ? lastEvent.getDay() : null;
                LocalDate currDay = currEvent.getDay();

                return EventViewCreator.createEventView(
                        context,
                        currEvent,
                        withDayLabels && (lastDay == null || currDay.isAfter(lastDay))
                );
            } else {
                return null;
            }
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
