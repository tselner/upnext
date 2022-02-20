package io.those.upnext.remoteviews;

import static android.view.View.GONE;
import static java.time.format.DateTimeFormatter.ofPattern;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import java.time.LocalDate;

import io.those.upnext.R;
import io.those.upnext.model.UpNextEvent;

public class EventViewCreator {
    private static final String DATE_PATTERN = "EEEE, d. LLL";

    public static RemoteViews createEventView(Context context, UpNextEvent prevEvent, UpNextEvent event, boolean isTodayEvent) {
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

        if (!isTodayEvent && addEventDay(prevEvent, event)) {
            eventView.setTextViewText(R.id.event_day, event.getDay().format(ofPattern(DATE_PATTERN)));
        } else {
            eventView.setViewVisibility(R.id.event_day, GONE);
        }

        // Background
        setEventBackgroundColor(context, eventView, event);

        // Color
        eventView.setInt(R.id.event_color, "setColorFilter", event.getColor());

        // Title
        eventView.setTextViewText(R.id.event_title, event.getTitle());

        // Duration
        if (event.isAllDay()) {
            eventView.setViewVisibility(R.id.event_duration, GONE);
        } else {
            eventView.setTextViewText(R.id.event_duration, isTodayEvent ? event.getDuration() : event.getStartAsString());
        }

        return eventView;
    }

    private static boolean addEventDay(UpNextEvent prevEvent, UpNextEvent event) {
        LocalDate lastDay = prevEvent != null ? prevEvent.getDay() : null;
        LocalDate currDay = event.getDay();

        return (lastDay == null || currDay.isAfter(lastDay));
    }

    private static void setEventBackgroundColor(Context context, RemoteViews eventView, UpNextEvent event) {
        if (event.isAllDay()) {
            eventView.setInt(R.id.event_background, "setColorFilter", event.getColor());
        } else {
            eventView.setInt(R.id.event_background, "setColorFilter", ContextCompat.getColor(context, R.color.background_event));
        }

        if (!isNightModeActive(context)) {
            eventView.setInt(R.id.event_background, "setImageAlpha", 130);
        }
    }

    private static boolean isNightModeActive(Context context) {
        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;

            case Configuration.UI_MODE_NIGHT_NO:
                return false;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                return false;
        }

        return false;
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
