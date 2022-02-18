package io.those.upnext.receiver;

import static java.time.format.DateTimeFormatter.ofPattern;
import static io.those.upnext.service.EventsService.EXTRA_DATE_PATTERN;
import static io.those.upnext.service.EventsService.EXTRA_END;
import static io.those.upnext.service.EventsService.EXTRA_START;
import static io.those.upnext.service.EventsService.EXTRA_WITH_DAY_LABELS;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.those.upnext.R;
import io.those.upnext.remoteviews.WidgetViewCreator;
import io.those.upnext.service.EventsService;
import io.those.upnext.util.PermissionUtil;

public class UpNextWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        appWidgetManager.updateAppWidget(appWidgetId, WidgetViewCreator.createWidgetView(context, appWidgetId));
    }
}
