package io.those.upnext.remoteviews;

import static android.view.View.GONE;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import io.those.upnext.R;
import io.those.upnext.model.UpNextDayLabel;
import io.those.upnext.model.UpNextEvent;
import io.those.upnext.model.UpNextListElement;

public class ListViewElementCreator {
    private static final int ALPHA = 130;

    public static RemoteViews createListElementView(Context context, UpNextListElement currElement, boolean isTodayEvent) {
        if (currElement instanceof UpNextEvent) {
            return createEventView(context, (UpNextEvent) currElement, isTodayEvent);
        }

        if (currElement instanceof UpNextDayLabel) {
            return createDayLabelView(context, (UpNextDayLabel) currElement);
        }

        return null;
    }

    private static RemoteViews createDayLabelView(Context context, UpNextDayLabel dayLabel) {
        RemoteViews dayLabelView = new RemoteViews(context.getPackageName(), R.layout.layout_day_label);
        dayLabelView.setTextViewText(R.id.event_day, dayLabel.toString());
        return dayLabelView;
    }

    private static RemoteViews createEventView(Context context, UpNextEvent event, boolean isTodayEvent) {
        RemoteViews eventView;

        if (isTodayEvent) {
            if (event.isAllDay()) {
                eventView = new RemoteViews(context.getPackageName(), R.layout.layout_event_today_allday);
            } else {
                eventView = new RemoteViews(context.getPackageName(), R.layout.layout_event_today_subday);
            }
        } else {
            if (event.isAllDay()) {
                eventView = new RemoteViews(context.getPackageName(), R.layout.layout_event_upnext_allday);
            } else {
                eventView = new RemoteViews(context.getPackageName(), R.layout.layout_event_upnext_subday);
            }
        }

        // Background
        setEventBackgroundColor(context, eventView, event);

        // Color
        eventView.setInt(R.id.event_color, "setColorFilter", event.getColor());

        // Title
        eventView.setTextViewText(R.id.event_title, event.getTitle());
        if (isNightMode(context) && event.isAllDay()) {
            eventView.setTextColor(R.id.event_title, event.getColor());
        }

        // Duration
        if (event.isAllDay()) {
            eventView.setViewVisibility(R.id.event_duration, GONE);
        } else {
            eventView.setTextViewText(R.id.event_duration, isTodayEvent ? event.getDuration() : event.getStartAsString());
        }

        return eventView;
    }

    private static void setEventBackgroundColor(Context context, RemoteViews eventView, UpNextEvent event) {
        // Background & Alpha
        if (isDayMode(context) && event.isAllDay()) {
            eventView.setInt(R.id.event_background, "setColorFilter", event.getColor());
            eventView.setInt(R.id.event_background, "setImageAlpha", ALPHA);
        } else {
            eventView.setInt(R.id.event_background, "setColorFilter", ContextCompat.getColor(context, R.color.background_event));
        }
    }

    private static boolean isDayMode(Context context) {
        return !isNightMode(context);
    }

    private static boolean isNightMode(Context context) {
        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;

            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                return false;
        }

        return false;
    }
/*
    private static int getTextColor(Context context, int elementColor) {
        // Text color
        int colorFont      = ContextCompat.getColor(context, R.color.font_event);
        int colorFontALt   = ContextCompat.getColor(context, R.color.font_event_alt);
        int chosenFontColor = colorFont;

        if (ColorUtils.calculateContrast(colorFont, elementColor) < 4.5) {
            // need different font (https://miromatech.com/android/contrast-ratio/)
            chosenFontColor = colorFontALt;
        }

        return chosenFontColor;
    }
 */
}
