package io.those.upnext.activity;

import static android.Manifest.permission.READ_CALENDAR;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import io.those.upnext.R;
import io.those.upnext.adapter.CalendarListViewAdapter;
import io.those.upnext.repository.CalendarRepository;
import io.those.upnext.util.PermissionUtil;

public class UpNextActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        refreshUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionUtil.isReadCalendarGranted(requestCode, grantResults)) {
            Toast.makeText(this, "Thank you!", Toast.LENGTH_SHORT).show();
            refreshUI();
        } else {
            Toast.makeText(this, "This app requires permission to read your calendar!", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickRefresh(View view) {
        checkPermission();
        refreshUI();
    }

    private void refreshUI() {
        setContentView(R.layout.activity_upnext);

        // ListView
        ListView listViewAvailableCalendars = findViewById(R.id.list_available_calendars);
        listViewAvailableCalendars.setEmptyView(findViewById(R.id.list_available_calendars_empty));

        if (PermissionUtil.checkReadCalendarPermission(this)) {
            listViewAvailableCalendars.setAdapter(new CalendarListViewAdapter(
                    this,
                    new CalendarRepository(getContentResolver()).getCalendars()
                    )
            );
        }
    }

    private void checkPermission() {
        if (!PermissionUtil.checkReadCalendarPermission(this)) {
            requestPermissions(new String[] {READ_CALENDAR}, PermissionUtil.READ_CALENDAR_CODE);
        }
    }
}