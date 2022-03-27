package io.those.upnext.adapter;

import static io.those.upnext.util.ColorUtil.getTextColor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.those.upnext.R;
import io.those.upnext.model.UpNextCalendar;

public class CalendarListViewAdapter extends BaseAdapter {
    private final Context context;
    private final List<UpNextCalendar> calendars;

    public CalendarListViewAdapter(Context context, List<UpNextCalendar> calendars) {
        super();

        this.context = context;

        if (calendars != null) {
            this.calendars = calendars;
        } else {
            this.calendars = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return calendars.size();
    }

    @Override
    public Object getItem(int position) {
        return calendars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return calendars.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_calendar, parent, false);
        }

        UpNextCalendar cal = (UpNextCalendar) getItem(position);

        // background
        ImageView backgroundView = convertView.findViewById(R.id.calendar_background);
        backgroundView.setColorFilter(cal.getColor());

        // name
        TextView calendarTitle = ((TextView) convertView.findViewById(R.id.calendar_title));
        calendarTitle.setTextColor(getTextColor(context, cal.getColor()));
        calendarTitle.setText(cal.getDisplayName());

        return convertView;
    }
}
