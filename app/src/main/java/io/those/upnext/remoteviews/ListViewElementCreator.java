package io.those.upnext.remoteviews;

import static android.view.View.GONE;

import android.content.Context;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import io.those.upnext.R;
import io.those.upnext.model.UpNextDayLabel;
import io.those.upnext.model.UpNextEvent;
import io.those.upnext.model.UpNextListElement;

public class ListViewElementCreator {

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

        int backgroundColor = getEventBackgroundColor(context, event);
        int textColor = getTextColor(context, backgroundColor);

        if (isTodayEvent) {
            if (event.allDay) {
                eventView = new RemoteViews(context.getPackageName(), R.layout.layout_event_today_allday);
            } else {
                eventView = new RemoteViews(context.getPackageName(), R.layout.layout_event_today_subday);
            }
        } else {
            if (event.allDay) {
                eventView = new RemoteViews(context.getPackageName(), R.layout.layout_event_upnext_allday);
            } else {
                eventView = new RemoteViews(context.getPackageName(), R.layout.layout_event_upnext_subday);
            }
        }

        // Background
        eventView.setInt(R.id.event_background, "setColorFilter", backgroundColor);
        // eventView.setInt(R.id.event_background, "setImageAlpha", UpNextCalendar.BACKGROUND_ALPHA);

        // Color (element is only displayed on subDay events)
        if (!event.allDay) {
            eventView.setInt(R.id.event_color, "setColorFilter", event.color);
        }

        // Title
        eventView.setTextViewText(R.id.event_title, event.title);
        eventView.setTextColor(R.id.event_title, textColor);

        // Duration
        if (event.allDay) {
            eventView.setViewVisibility(R.id.event_duration, GONE);
        } else {
            eventView.setTextViewText(R.id.event_duration, isTodayEvent ? event.getDuration() : event.getStartAsString());
            eventView.setTextColor(R.id.event_duration, textColor);
        }

        return eventView;
    }

    private static int getEventBackgroundColor(Context context, UpNextEvent event) {
        // Background & Alpha
        if (event.allDay) {
            return event.color;
        } else {
            return ContextCompat.getColor(context, R.color.background_event);
        }
    }

    private static int getTextColor(Context context, int backgroundColor) {
        // Text color
        int colorFont      = ContextCompat.getColor(context, R.color.font_event);
        int colorFontALt   = ContextCompat.getColor(context, R.color.font_event_alt);
        int chosenFontColor = colorFont;

        if (ColorUtils.calculateContrast(colorFont, backgroundColor) < 4.5) {
            // need different font (https://miromatech.com/android/contrast-ratio/)
            chosenFontColor = colorFontALt;
        }

        return chosenFontColor;
    }
}
