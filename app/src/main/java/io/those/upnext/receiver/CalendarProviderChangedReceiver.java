package io.those.upnext.receiver;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.those.upnext.intent.IntentUtil;

public class CalendarProviderChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(CalendarProviderChangedReceiver.class.getSimpleName(), String.format("onReceive with action %s ...", intent.getAction()));
        if (Intent.ACTION_PROVIDER_CHANGED.equals(intent.getAction())) {
            ComponentName widgetProvider = new ComponentName(context, UpNextWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widgetProvider);

            Log.i(CalendarProviderChangedReceiver.class.getSimpleName(), String.format("sendBroadcast to %d widgets ...", appWidgetIds.length));
            context.sendBroadcast(IntentUtil.createUpdateIntent(context, appWidgetIds));
            Log.i(CalendarProviderChangedReceiver.class.getSimpleName(), String.format("sendBroadcast to %d widgets finished!", appWidgetIds.length));
        }
        Log.i(CalendarProviderChangedReceiver.class.getSimpleName(), String.format("onReceive with action %s finished!", intent.getAction()));
    }
}
