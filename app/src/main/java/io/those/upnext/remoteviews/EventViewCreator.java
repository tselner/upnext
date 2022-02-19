package io.those.upnext.remoteviews;

import static android.view.View.GONE;
import static java.time.format.DateTimeFormatter.ofPattern;

import android.content.Context;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

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
        eventView.setInt(R.id.event_color, "setColorFilter", event.getColor());

        // Title
        eventView.setTextViewText(R.id.event_title, event.getTitle());

        if (event.isAllDay()) {
            eventView.setViewVisibility(R.id.event_duration, GONE);
        } else {
            eventView.setTextViewText(R.id.event_duration, event.getDuration());
        }

        // Text color
        int colorFontDark  = ContextCompat.getColor(context, R.color.font_event_dark);
        int colorFontLight = ContextCompat.getColor(context, R.color.font_event_light);
        int chosenFontColor = colorFontDark;

        if (ColorUtils.calculateContrast(colorFontDark, event.getColor()) < 4.5) {
            // need different font (https://miromatech.com/android/contrast-ratio/)
            chosenFontColor = colorFontLight;
        }

        eventView.setTextColor(R.id.event_title, chosenFontColor);
        eventView.setTextColor(R.id.event_duration, chosenFontColor);

        return eventView;
    }
}
