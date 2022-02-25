package io.those.upnext.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
    public static final String EXTRA_IS_TODAY_EVENT = "isTodayEvent";
    public static final String EXTRA_DATE_PATTERN = "yyy-MM-dd";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i(EventsService.class.getName(), "onGetViewFactory ...");
        Log.i(EventsService.class.getName(), "onGetViewFactory finished!");
        return new EventsRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    static class EventsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private final Context context;
        private final LocalDate start;
        private final LocalDate end;
        private final boolean isTodayEvent;
        private final List<UpNextEvent> events = new ArrayList<>();

        public EventsRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;

            this.start = LocalDate.parse(intent.getStringExtra(EXTRA_START), DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN));
            this.end   = LocalDate.parse(intent.getStringExtra(EXTRA_END)  , DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN));

            this.isTodayEvent = intent.getBooleanExtra(EXTRA_IS_TODAY_EVENT, true);
        }

        private void populateEvents() {
            Log.i(EventsRemoteViewsFactory.class.getName(), "populateEvents ...");
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
            Log.i(EventsRemoteViewsFactory.class.getName(), String.format("populateEvents with %d events finished!", events.size()));
        }

        CalendarRepository getCalendarRepositoryInstance(Context context) {
            return new CalendarRepository(context.getContentResolver());
        }

        EventRepository getEventRepositoryInstance(Context context) {
            return new EventRepository(context.getContentResolver());
        }

        @Override
        public void onCreate() {
            Log.i(EventsRemoteViewsFactory.class.getName(), "onCreate ...");
            populateEvents();
            Log.i(EventsRemoteViewsFactory.class.getName(), "onCreate finished!");
        }

        @Override
        public void onDataSetChanged() {
            Log.i(EventsRemoteViewsFactory.class.getName(), "onDataSetChanged ...");
            populateEvents();
            Log.i(EventsRemoteViewsFactory.class.getName(), "onDataSetChanged finished!");
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
            RemoteViews view = null;

            Log.i(EventsRemoteViewsFactory.class.getName(), String.format("getViewAt with position %d ...", position));
            if (position < events.size()) {
                view = EventViewCreator.createEventView(context, position > 0 ? events.get(position - 1) : null, events.get(position), isTodayEvent);
            }

            Log.i(EventsRemoteViewsFactory.class.getName(), String.format("getViewAt with position %d finished!", position));
            return view;
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
            return true;
        }
    }
}
