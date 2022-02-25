package io.those.upnext.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

import io.those.upnext.remoteviews.WidgetViewCreator;
import io.those.upnext.util.PermissionUtil;

public class UpNextWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(UpNextWidgetProvider.class.getName(), "onUpdate ...");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.i(UpNextWidgetProvider.class.getName(), "onUpdate finished!");
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.i(UpNextWidgetProvider.class.getName(), String.format("updateAppWidget with id %d ...", appWidgetId));
        if (PermissionUtil.checkReadCalendarPermission(context)) {
            appWidgetManager.updateAppWidget(appWidgetId, null); // Necessary, because otherwise the widget does not get updated (caching?)
            appWidgetManager.updateAppWidget(appWidgetId, WidgetViewCreator.createWidgetView(context, appWidgetId));
        }
        Log.i(UpNextWidgetProvider.class.getName(), String.format("updateAppWidget with id %d finished!", appWidgetId));
    }
}
