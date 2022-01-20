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
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_upnext);

        LocalDate today = LocalDate.now();
        // LocalDate today = LocalDate.of(2021, Month.APRIL, 14);

        views.setTextViewText(R.id.left_header_month, today.format(ofPattern("LLLL")));
        views.setTextViewText(R.id.left_header_weekday, today.format(ofPattern("EEEE")));
        views.setTextViewText(R.id.left_header_date, today.format(ofPattern("d")));

        if (PermissionUtil.checkReadCalendarPermission(context)) {
            views.setRemoteAdapter(R.id.left_events,
                    createServiceIntent(context, appWidgetId, today, today, false));

            views.setRemoteAdapter(R.id.right_events,
                    createServiceIntent(context, appWidgetId, today.plusDays(1), today.plusDays(2),true));
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private Intent createServiceIntent(Context context, int appWidgetId, LocalDate start, LocalDate end, boolean withDayLabels) {
        Intent intent = new Intent(context, EventsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(EXTRA_START, start.format(DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN)));
        intent.putExtra(EXTRA_END, end.format(DateTimeFormatter.ofPattern(EXTRA_DATE_PATTERN)));
        intent.putExtra(EXTRA_WITH_DAY_LABELS, withDayLabels);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        return intent;
    }
}
