package io.those.upnext.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import io.those.upnext.intent.IntentUtil;
import io.those.upnext.receiver.CalendarProviderChangedReceiver;
import io.those.upnext.receiver.UpNextWidgetProvider;

public class WidgetUtil {
    public static void updateAllWidgets(Context context) {
        ComponentName widgetProvider = new ComponentName(context, UpNextWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widgetProvider);

        Log.i(CalendarProviderChangedReceiver.class.getSimpleName(), String.format("sendBroadcast to %d widgets ...", appWidgetIds.length));
        context.sendBroadcast(IntentUtil.createUpdateIntent(context, appWidgetIds));
        Log.i(CalendarProviderChangedReceiver.class.getSimpleName(), String.format("sendBroadcast to %d widgets finished!", appWidgetIds.length));
    }
}
