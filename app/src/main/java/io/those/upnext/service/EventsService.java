package io.those.upnext.service;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.those.upnext.model.UpNextDayLabel;
import io.those.upnext.model.UpNextEvent;
import io.those.upnext.model.UpNextListElement;
import io.those.upnext.remoteviews.ListViewElementCreator;
import io.those.upnext.repository.EventRepository;

public class EventsService extends RemoteViewsService {
    public static final String EXTRA_START = "start";
    public static final String EXTRA_END = "end";
    public static final String EXTRA_IS_TODAY_VIEW = "isTodayView";
    public static final String EXTRA_DATE_PATTERN = "yyy-MM-dd";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new EventsRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    public static class EventsRemoteViewsFactory extends BroadcastReceiver implements RemoteViewsService.RemoteViewsFactory {
        private final Context context;
        private final LocalDate start;
        private final LocalDate end;
        private final boolean isTodayView;
        private final List<UpNextListElement> elements = new ArrayList<>();
        private final EventRepository eventRepository;
        
        @NonNull
        @Override
        public String toString() {
            return String.format("%s [%s - %s]",
                    getClass().getSimpleName(),
                    start != null ? start.format(BASIC_ISO_DATE) : "?",
                    end   != null ? end  .format(BASIC_ISO_DATE) : "?");
        }

        public EventsRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;

            this.eventRepository = new EventRepository(context.getContentResolver());

            this.start = LocalDate.parse(intent.getStringExtra(EXTRA_START), DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN));
            this.end   = LocalDate.parse(intent.getStringExtra(EXTRA_END)  , DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN));

            this.isTodayView = intent.getBooleanExtra(EXTRA_IS_TODAY_VIEW, true);
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            populateElements();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            populateElements();
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
                UpNextListElement currElement = elements.get(position);
                view = ListViewElementCreator.createListElementView(context, currElement, isTodayView);
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
            long numberOfDaysBetween = ChronoUnit.DAYS.between(start, end) + 1;
            List<LocalDate> days = IntStream.iterate(0, i -> i + 1)
                    .limit(numberOfDaysBetween)
                    .mapToObj(start::plusDays)
                    .collect(Collectors.toList());

            elements.clear();
            days.forEach(day -> {
                List<UpNextEvent> eventsForThatDay = eventRepository.getEvents(day);

                if (!isTodayView && !eventsForThatDay.isEmpty()) {
                    elements.add(new UpNextDayLabel(day));
                }

                elements.addAll(eventsForThatDay);
            });
        }
    }
}
