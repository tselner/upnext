package io.those.upnext.activity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.those.upnext.remoteviews.WidgetViewCreator;
import io.those.upnext.util.PermissionUtil;

public class UpNextWidgetConfigurationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED); // This causes the widget host to cancel the widget placement if the user presses the back button

        if (PermissionUtil.checkReadCalendarPermission(this)) {
            Intent intent = getIntent();
            int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

            Bundle extras = intent.getExtras();
            if (extras != null) {
                appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }

            RemoteViews widgetView = WidgetViewCreator.createWidgetView(this, appWidgetId);
            AppWidgetManager.getInstance(this).updateAppWidget(appWidgetId, widgetView);

            Intent resultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
        } else {
            Toast.makeText(this, "Can not create widget: permission required!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
