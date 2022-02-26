package io.those.upnext.remoteviews;

import static java.time.format.DateTimeFormatter.ofPattern;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import java.time.LocalDate;

import io.those.upnext.R;

public class WidgetViewCreator {
    public static RemoteViews createWidgetView(Context context, LocalDate today, int appWidgetId) {
        Log.i(WidgetViewCreator.class.getSimpleName(), String.format("createWidgetView with id %d ...", appWidgetId));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_upnext);
        views.setTextViewText(R.id.header_text, "up next");
        views.setTextViewText(R.id.left_header_weekday, today.format(ofPattern("EEEE")));
        views.setTextViewText(R.id.left_header_date, today.format(ofPattern("d")));

        Log.i(WidgetViewCreator.class.getSimpleName(), String.format("createWidgetView with id %d finished!", appWidgetId));
        return views;
    }
}
