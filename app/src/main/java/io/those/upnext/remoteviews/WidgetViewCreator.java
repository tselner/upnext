package io.those.upnext.remoteviews;

import static java.time.format.DateTimeFormatter.ofPattern;
import static io.those.upnext.receiver.UpNextWidgetProvider.ACTION_WIDGET_REFRESH;
import static io.those.upnext.service.EventsService.EXTRA_DATE_PATTERN;
import static io.those.upnext.service.EventsService.EXTRA_END;
import static io.those.upnext.service.EventsService.EXTRA_IS_TODAY_EVENT;
import static io.those.upnext.service.EventsService.EXTRA_START;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.those.upnext.R;
import io.those.upnext.receiver.UpNextWidgetProvider;
import io.those.upnext.service.EventsService;
import io.those.upnext.util.PermissionUtil;

public class WidgetViewCreator {
    public static RemoteViews createWidgetView(Context context, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_upnext);

        // Refresh Button
        Intent updateIntent = new Intent(context, UpNextWidgetProvider.class);
        updateIntent.setAction(ACTION_WIDGET_REFRESH);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
        views.setOnClickPendingIntent(R.id.btn_refresh, pendingUpdateIntent);

        LocalDate today = LocalDate.now();
        // LocalDate today = LocalDate.of(2021, Month.AUGUST, 2);

        views.setTextViewText(R.id.today_date, today.format(ofPattern("EEEE, d. LLL")));

        if (PermissionUtil.checkReadCalendarPermission(context)) {
            views.setRemoteAdapter(R.id.today_events,
                    createServiceIntent(context, appWidgetId, today, today, true));

            views.setRemoteAdapter(R.id.upnext_events,
                    createServiceIntent(context, appWidgetId, today.plusDays(1), today.plusDays(2),false));
        }

        return views;
    }

    private static Intent createServiceIntent(Context context, int appWidgetId, LocalDate start, LocalDate end, boolean isTodayEvent) {
        Intent intent = new Intent(context, EventsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(EXTRA_START, start.format(DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN)));
        intent.putExtra(EXTRA_END, end.format(DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN)));
        intent.putExtra(EXTRA_IS_TODAY_EVENT, isTodayEvent);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        return intent;
    }
}
