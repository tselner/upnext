package io.those.upnext.activity;

import static android.Manifest.permission.READ_CALENDAR;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.those.upnext.remoteviews.WidgetViewCreator;
import io.those.upnext.util.PermissionUtil;

public class UpNextWidgetConfigurationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission(); // Required for displaying calendar events
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionUtil.isReadCalendarGranted(requestCode, grantResults)) {
            Intent intent = getIntent();
            int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

            Bundle extras = intent.getExtras();
            if (extras != null) {
                appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            }

            RemoteViews widgetView = WidgetViewCreator.createWidgetView(this, appWidgetId);
            AppWidgetManager.getInstance(this).updateAppWidget(appWidgetId, widgetView);

            Intent resultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
        } else {
            Toast.makeText(this, "This app requires permission to read your calendar!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
        }

        finish();
    }

    private void checkPermission() {
        if (!PermissionUtil.checkReadCalendarPermission(this)) {
            requestPermissions(new String[] {READ_CALENDAR}, PermissionUtil.READ_CALENDAR_CODE);
        }
    }
}
