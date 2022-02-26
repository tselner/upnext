package io.those.upnext.receiver;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.content.Intent.ACTION_DATE_CHANGED;
import static android.content.Intent.ACTION_PROVIDER_CHANGED;
import static android.content.Intent.ACTION_TIMEZONE_CHANGED;
import static android.content.Intent.ACTION_TIME_CHANGED;
import static java.time.format.DateTimeFormatter.ofPattern;
import static io.those.upnext.service.EventsService.EXTRA_DATE_PATTERN;
import static io.those.upnext.service.EventsService.EXTRA_END;
import static io.those.upnext.service.EventsService.EXTRA_IS_TODAY_VIEW;
import static io.those.upnext.service.EventsService.EXTRA_START;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.time.LocalDate;

import io.those.upnext.R;
import io.those.upnext.remoteviews.WidgetViewCreator;
import io.those.upnext.service.EventsService;

public class UpNextWidgetProvider extends AppWidgetProvider {
/*
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(UpNextWidgetProvider.class.getSimpleName(), "onReceive ...");

        if (isUpdateIntent(intent)) {
            // Calendar provider changed notification
            ComponentName thisWidget = new ComponentName(context, AppWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            // update all the widgets
            performUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisWidget));
        } else if (isServiceIntent(intent)) {
            Intent service = new Intent(context, EventsService.class);
            context.startService(service);
        } else {
            super.onReceive(context, intent);
        }

        Log.i(UpNextWidgetProvider.class.getSimpleName(), "onReceive finished!");
    }
 */

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(UpNextWidgetProvider.class.getSimpleName(), "onUpdate ...");
        performUpdate(context, appWidgetManager, appWidgetIds);
        Log.i(UpNextWidgetProvider.class.getSimpleName(), "onUpdate finished!");
    }

    private void performUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            LocalDate today = LocalDate.now();

            Intent todayUpdateIntent   = createServiceIntent(context, appWidgetId, today            , today            , true);
            Intent upnextUpdateIntent  = createServiceIntent(context, appWidgetId, today.plusDays(1), today.plusDays(2), false);
            Intent refreshButtonIntent = createUpdateIntent (context, appWidgetId);

            RemoteViews views = WidgetViewCreator.createWidgetView(context, today, appWidgetId);

            views.setRemoteAdapter(R.id.today_events, todayUpdateIntent);
            views.setRemoteAdapter(R.id.upnext_events, upnextUpdateIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.today_events);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.upnext_events);

            PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(context, appWidgetId, refreshButtonIntent, FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.btn_refresh, pendingUpdateIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private Intent createServiceIntent(Context context, int appWidgetId, LocalDate start, LocalDate end, boolean isTodayView) {
        Intent intent = new Intent(context, EventsService.class);
        intent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(EXTRA_START, start.format(ofPattern(EXTRA_DATE_PATTERN)));
        intent.putExtra(EXTRA_END, end.format(ofPattern(EXTRA_DATE_PATTERN)));
        intent.putExtra(EXTRA_IS_TODAY_VIEW, isTodayView);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        return intent;
    }

    private Intent createUpdateIntent(Context context, int appWidgetId) {
        Intent updateIntent = new Intent(context, UpNextWidgetProvider.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {appWidgetId});
        return updateIntent;
    }

    private boolean isUpdateIntent(Intent intent) {
        return intent != null && intent.getAction() != null &&
                (
                    intent.getAction().contains("APPWIDGET_UPDATE")
                );
    }

    private boolean isServiceIntent(Intent intent) {
        return intent != null && intent.getAction() != null &&
                (
                    ACTION_PROVIDER_CHANGED.equals(intent.getAction()) ||
                    ACTION_DATE_CHANGED.equals(intent.getAction()) ||
                    ACTION_TIME_CHANGED.equals(intent.getAction()) ||
                    ACTION_TIMEZONE_CHANGED.equals(intent.getAction()) ||
                    intent.getAction().contains("APPWIDGET_SCHEDULED_UPDATE")
                );
    }
}
