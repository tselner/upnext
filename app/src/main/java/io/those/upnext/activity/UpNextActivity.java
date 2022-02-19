package io.those.upnext.activity;

import static android.Manifest.permission.READ_CALENDAR;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import io.those.upnext.R;
import io.those.upnext.adapter.CalendarListViewAdapter;
import io.those.upnext.model.UpNextCalendar;
import io.those.upnext.repository.CalendarRepository;
import io.those.upnext.util.PermissionUtil;

public class UpNextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        refreshUI();
    }

    public void onClickRefresh(View view) {
        checkPermission();
        refreshUI();
        Toast.makeText(this, "Refresh has been clicked!", Toast.LENGTH_SHORT).show();
    }

    private void refreshUI() {
        setContentView(R.layout.activity_upnext);

        if (PermissionUtil.checkReadCalendarPermission(this)) {
            CalendarRepository calRep = new CalendarRepository(getContentResolver());
            List<UpNextCalendar> cals = calRep.getCalendars();

            if (cals.isEmpty()) {
                findViewById(R.id.list_available_calendars).setVisibility(View.GONE);
            } else {
                findViewById(R.id.text_no_calendars).setVisibility(View.GONE);

                // ListView
                ListView listViewAvailableCalendars = findViewById(R.id.list_available_calendars);
                listViewAvailableCalendars.setAdapter(new CalendarListViewAdapter(this, cals));
            }
        }
        else {
            findViewById(R.id.list_available_calendars).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionUtil.isReadCalendarGranted(requestCode, grantResults)) {
            Toast.makeText(this, "Thank your for your permission to read your calendar!", Toast.LENGTH_SHORT).show();
            refreshUI();
        } else {
            Toast.makeText(this, "This app requires permission to read your calendar!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission() {
        if (!PermissionUtil.checkReadCalendarPermission(this)) {
            requestPermissions(new String[] {READ_CALENDAR}, PermissionUtil.READ_CALENDAR_CODE);
        }
    }
}