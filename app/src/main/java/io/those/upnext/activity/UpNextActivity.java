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
import io.those.upnext.repository.CalendarRepository;
import io.those.upnext.model.UpNextCalendar;
import io.those.upnext.adapter.CalendarListViewAdapter;
import io.those.upnext.util.PermissionUtil;

public class UpNextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}