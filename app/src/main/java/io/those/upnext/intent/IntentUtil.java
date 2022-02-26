package io.those.upnext.intent;

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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.time.LocalDate;

import io.those.upnext.receiver.UpNextWidgetProvider;
import io.those.upnext.service.EventsService;

public class IntentUtil {
    public static boolean isUpdateIntent(Intent intent) {
        return intent != null && intent.getAction() != null &&
                (
                        intent.getAction().contains("APPWIDGET_UPDATE") ||
                        intent.getAction().contains("APPWIDGET_SCHEDULED_UPDATE")
                );
    }

    public static boolean isServiceIntent(Intent intent) {
        return intent != null && intent.getAction() != null &&
                (
                        ACTION_PROVIDER_CHANGED.equals(intent.getAction()) ||
                        ACTION_DATE_CHANGED    .equals(intent.getAction()) ||
                        ACTION_TIME_CHANGED    .equals(intent.getAction()) ||
                        ACTION_TIMEZONE_CHANGED.equals(intent.getAction())
                );
    }

    public static Intent createServiceIntent(Context context, int appWidgetId, LocalDate start, LocalDate end, boolean isTodayView) {
        Intent intent = new Intent(context, EventsService.class);
        intent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(EXTRA_START, start.format(ofPattern(EXTRA_DATE_PATTERN)));
        intent.putExtra(EXTRA_END, end.format(ofPattern(EXTRA_DATE_PATTERN)));
        intent.putExtra(EXTRA_IS_TODAY_VIEW, isTodayView);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        return intent;
    }

    public static Intent createUpdateIntent(Context context, int[] appWidgetIds) {
        Intent updateIntent = new Intent(context, UpNextWidgetProvider.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        return updateIntent;
    }
}
