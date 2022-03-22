package io.those.upnext.receiver;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.time.LocalDate;

import io.those.upnext.R;
import io.those.upnext.intent.IntentUtil;
import io.those.upnext.remoteviews.WidgetViewCreator;

public class UpNextWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(UpNextWidgetProvider.class.getSimpleName(), "onUpdate ...");
        performUpdate(context, appWidgetManager, appWidgetIds);
        Toast.makeText(context, "up next widgets updated.", Toast.LENGTH_SHORT).show();
        Log.i(UpNextWidgetProvider.class.getSimpleName(), "onUpdate finished!");
    }

    private void performUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            LocalDate today = LocalDate.now();
            // LocalDate today = LocalDate.of(2022, Month.MARCH, 12);

            Intent todayUpdateIntent   = IntentUtil.createServiceIntent(context,            appWidgetId, today            , today            , true);
            Intent upnextUpdateIntent  = IntentUtil.createServiceIntent(context,            appWidgetId, today.plusDays(1), today.plusDays(2), false);
            Intent refreshButtonIntent = IntentUtil.createUpdateIntent (context, new int[] {appWidgetId});

            RemoteViews views = WidgetViewCreator.createWidgetView(context, today, appWidgetId);

            views.setRemoteAdapter(R.id.today_events, todayUpdateIntent);
            views.setEmptyView    (R.id.today_events, R.id.today_events_empty);

            views.setRemoteAdapter(R.id.upnext_events, upnextUpdateIntent);
            views.setEmptyView    (R.id.upnext_events, R.id.upnext_events_empty);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.today_events);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.upnext_events);

            PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(context, appWidgetId, refreshButtonIntent, FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.btn_refresh, pendingUpdateIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
