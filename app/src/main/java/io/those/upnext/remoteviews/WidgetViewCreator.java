package io.those.upnext.remoteviews;

import static java.time.format.DateTimeFormatter.ofPattern;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import io.those.upnext.R;
import io.those.upnext.receiver.UpNextWidgetProvider;
import io.those.upnext.service.EventsService;
import io.those.upnext.util.PermissionUtil;

public class WidgetViewCreator {
    public static RemoteViews createWidgetView(Context context, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_upnext);

        // Refresh Button
        Intent updateIntent = new Intent(context, UpNextWidgetProvider.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {appWidgetId});
        PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(context, appWidgetId, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_refresh, pendingUpdateIntent);

        LocalDate today = LocalDate.now();
        // LocalDate today = LocalDate.of(2022, Month.FEBRUARY, 21);

        views.setTextViewText(R.id.header_text, "up next");
        views.setTextViewText(R.id.last_updated, LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        views.setTextViewText(R.id.left_header_weekday, today.format(ofPattern("EEEE")));
        views.setTextViewText(R.id.left_header_date, today.format(ofPattern("d")));

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
