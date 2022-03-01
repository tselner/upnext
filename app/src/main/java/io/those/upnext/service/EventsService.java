package io.those.upnext.service;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.those.upnext.model.UpNextCalendar;
import io.those.upnext.model.UpNextDayLabel;
import io.those.upnext.model.UpNextEvent;
import io.those.upnext.model.UpNextListElement;
import io.those.upnext.remoteviews.ListViewElementCreator;
import io.those.upnext.repository.CalendarRepository;
import io.those.upnext.repository.EventRepository;

public class EventsService extends RemoteViewsService {
    public static final String EXTRA_START = "start";
    public static final String EXTRA_END = "end";
    public static final String EXTRA_IS_TODAY_VIEW = "isTodayView";
    public static final String EXTRA_DATE_PATTERN = "yyy-MM-dd";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i(EventsService.class.getSimpleName(), "onGetViewFactory ...");
        Log.i(EventsService.class.getSimpleName(), "onGetViewFactory finished!");
        return new EventsRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    public static class EventsRemoteViewsFactory extends BroadcastReceiver implements RemoteViewsService.RemoteViewsFactory {
        private final Context context;
        private final LocalDate start;
        private final LocalDate end;
        private final boolean isTodayView;
        private final List<UpNextListElement> elements = new ArrayList<>();
        private final CalendarRepository calendarRepository;
        private final EventRepository eventRepository;
        
        @NonNull
        @Override
        public String toString() {
            return String.format("%s [%s - %s]",
                    EventsRemoteViewsFactory.class.getSimpleName(),
                    start != null ? start.format(BASIC_ISO_DATE) : "?",
                    end   != null ? end  .format(BASIC_ISO_DATE) : "?");
        }

        public EventsRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;

            this.calendarRepository = new CalendarRepository(context.getContentResolver());
            this.eventRepository    = new EventRepository(context.getContentResolver());

            this.start = LocalDate.parse(intent.getStringExtra(EXTRA_START), DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN));
            this.end   = LocalDate.parse(intent.getStringExtra(EXTRA_END)  , DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN));

            this.isTodayView = intent.getBooleanExtra(EXTRA_IS_TODAY_VIEW, true);
        }

        @Override
        public void onCreate() {
            Log.i(toString(), "onCreate ...");
            // populateElements(); // not necessary, onDataSetChanged gets called autom. after onCreate
            Log.i(toString(), "onCreate finished!");
        }

        @Override
        public void onDataSetChanged() {
            Log.i(toString(), "onDataSetChanged ...");
            populateElements();
            Log.i(toString(), "onDataSetChanged finished!");
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(toString(), String.format("onReceive with Action %s ...", intent.getAction()));
            populateElements();
            Log.i(toString(), String.format("onReceive with Action %s finished!", intent.getAction()));
        }

        @Override
        public void onDestroy() {
            elements.clear();
        }

        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews view = null;

            if (position < elements.size()) {
                Log.i(toString(), String.format("getViewAt with position %d ...", position));
                UpNextListElement currElement = elements.get(position);
                view = ListViewElementCreator.createListElementView(context, currElement, isTodayView);
                Log.i(toString(), String.format("getViewAt with position %d finished with element [%s]", position, currElement));
            }

            return view;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            int viewTypeCount = 0;

            if (dayLabelsPresent(elements)) {
                viewTypeCount++;
            }

            if (allDayEventsPresent(elements)) {
                viewTypeCount++;
            }

            if (subDayEventsPresent(elements)) {
                viewTypeCount++;
            }

            return viewTypeCount;
        }

        private boolean dayLabelsPresent(List<UpNextListElement> elements) {
            return elements.stream().anyMatch(element -> element instanceof UpNextDayLabel);
        }

        private boolean allDayEventsPresent(List<UpNextListElement> elements) {
            return elements.stream().anyMatch(element -> element instanceof UpNextEvent && ((UpNextEvent) element).isAllDay());
        }

        private boolean subDayEventsPresent(List<UpNextListElement> elements) {
            return elements.stream().anyMatch(element -> element instanceof UpNextEvent && !((UpNextEvent) element).isAllDay());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        private void populateElements() {
            Log.i(toString(), "populateElements ...");

            List<UpNextCalendar> cals = calendarRepository.getCalendars();

            long numberOfDaysBetween = ChronoUnit.DAYS.between(start, end) + 1;
            List<LocalDate> days = IntStream.iterate(0, i -> i + 1)
                    .limit(numberOfDaysBetween)
                    .mapToObj(start::plusDays)
                    .collect(Collectors.toList());

            elements.clear();
            days.forEach(day -> {
                List<UpNextEvent> eventsForThatDay = eventRepository.getEventsByDay(cals, day, ZoneId.systemDefault());
                if (!isTodayView && !eventsForThatDay.isEmpty()) {
                    elements.add(new UpNextDayLabel(day));
                }
                elements.addAll(eventsForThatDay);
            });
            Log.i(toString(), String.format("populateElements with %d elements finished!", elements.size()));
        }
    }
}
