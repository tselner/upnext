package io.those.upnext.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import io.those.upnext.remoteviews.WidgetViewCreator;
import io.those.upnext.util.PermissionUtil;

public class UpNextWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_WIDGET_REFRESH = "ACTION_WIDGET_REFRESH";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_WIDGET_REFRESH)) {
            updateAppWidget(context, AppWidgetManager.getInstance(context), intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID));
        } else {
            super.onReceive(context, intent);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        if (PermissionUtil.checkReadCalendarPermission(context)) {
            appWidgetManager.updateAppWidget(appWidgetId, WidgetViewCreator.createWidgetView(context, appWidgetId));
            Toast.makeText(context, String.format("Widget with ID %d has been updated!", appWidgetId), Toast.LENGTH_LONG).show();
        }
    }
}
