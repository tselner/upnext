package io.those.upnext.remoteviews;

import static android.view.View.GONE;
import static java.time.format.DateTimeFormatter.ofPattern;

import android.content.Context;
import android.widget.RemoteViews;

import io.those.upnext.R;
import io.those.upnext.model.UpNextEvent;

public class EventViewCreator {
    private static final String DATE_PATTERN = "EEEE, d. LLL";

    public static RemoteViews createEventView(Context context, UpNextEvent event, boolean addDayLabel) {
        RemoteViews eventView = new RemoteViews(context.getPackageName(), R.layout.layout_event);

        if (addDayLabel) {
            eventView.setTextViewText(R.id.day_label, event.getDay().format(ofPattern(DATE_PATTERN)));
        } else {
            eventView.setViewVisibility(R.id.day_label, GONE);
        }

        // Color
        eventView.setInt(R.id.event_color, "setBackgroundColor", event.getColor());

        // Title
        eventView.setTextViewText(R.id.event_title, event.getTitle());

        // Duration
        if (!event.isAllDay()) {
            eventView.setTextViewText(R.id.event_duration, event.getDuration());
        } else {
            eventView.setViewVisibility(R.id.event_duration, GONE);
        }

        return eventView;
    }
/*
    private static int getTextColor(Context context, UpNextEvent event) {
        // Text color
        int colorFont      = ContextCompat.getColor(context, R.color.font_event);
        int colorFontDark  = ContextCompat.getColor(context, R.color.font_event_dark);
        int colorFontLight = ContextCompat.getColor(context, R.color.font_event_light);
        int chosenFontColor = colorFont;

        if (event.isAllDay()) {
            // need different font (https://miromatech.com/android/contrast-ratio/)
            if (ColorUtils.calculateContrast(colorFontDark, event.getColor()) < 4.5) {
                chosenFontColor = colorFontLight;
            } else {
                chosenFontColor = colorFontDark;
            }
        }

        return chosenFontColor;
    }
 */
}
