package io.those.upnext.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.those.upnext.util.WidgetUtil;

public class CalendarProviderChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(CalendarProviderChangedReceiver.class.getSimpleName(), String.format("onReceive with action %s ...", intent.getAction()));
        if (Intent.ACTION_PROVIDER_CHANGED.equals(intent.getAction())) {
            WidgetUtil.updateAllWidgets(context);
        }
        Log.i(CalendarProviderChangedReceiver.class.getSimpleName(), String.format("onReceive with action %s finished!", intent.getAction()));
    }
}
