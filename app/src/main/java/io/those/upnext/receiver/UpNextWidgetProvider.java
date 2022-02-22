package io.those.upnext.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.Toast;

import io.those.upnext.remoteviews.WidgetViewCreator;
import io.those.upnext.util.PermissionUtil;

public class UpNextWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        if (PermissionUtil.checkReadCalendarPermission(context)) {
            appWidgetManager.updateAppWidget(appWidgetId, WidgetViewCreator.createWidgetView(context, appWidgetId));
            Toast.makeText(context, String.format("up next updated (ID=%d).", appWidgetId), Toast.LENGTH_SHORT).show();
        }
    }
}
