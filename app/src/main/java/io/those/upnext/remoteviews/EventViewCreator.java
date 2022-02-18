package io.those.upnext.remoteviews;

import static android.view.View.GONE;

import static java.time.format.DateTimeFormatter.ofPattern;

import android.widget.RemoteViews;

import io.those.upnext.R;
import io.those.upnext.model.UpNextEvent;

public class EventViewCreator {
    private static final String DATE_PATTERN = "EEEE, d. LLL";
    private static final String BACKGROUND_COLOR_METHOD = "setBackgroundColor";

    public static RemoteViews createEventView(String packageName, UpNextEvent event, boolean addDayLabel) {
        RemoteViews eventView = new RemoteViews(packageName, R.layout.layout_event);

        if (addDayLabel) {
            eventView.setTextViewText(R.id.day_label, event.getDay().format(ofPattern(DATE_PATTERN)));
        } else {
            eventView.setViewVisibility(R.id.day_label, GONE);
        }

        // Background
        if (event.isAllDay()) {
            eventView.setInt(R.id.event_text, BACKGROUND_COLOR_METHOD, event.getAlphaColor());
        }

        // Color
        eventView.setInt(R.id.event_color, BACKGROUND_COLOR_METHOD, event.getColor());

        // Title
        eventView.setTextViewText(R.id.event_title, event.getTitle());

        if (event.isAllDay()) {
            eventView.setViewVisibility(R.id.event_duration, GONE);
        } else {
            eventView.setTextViewText(R.id.event_duration, event.getDuration());
            // eventView.setTextColor(R.id.event_duration, event.getColor());
        }

        return eventView;
    }
}
