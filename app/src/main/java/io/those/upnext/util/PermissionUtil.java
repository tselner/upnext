package io.those.upnext.util;

import static android.Manifest.permission.READ_CALENDAR;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.content.Context;

public class PermissionUtil {
    public static final int READ_CALENDAR_CODE = 2258;

    public static boolean checkReadCalendarPermission(Context context) {
        return checkSelfPermission(context, READ_CALENDAR) == PERMISSION_GRANTED;
    }

    public static boolean isReadCalendarGranted(int requestCode, int[] grantResults) {
        return requestCode == PermissionUtil.READ_CALENDAR_CODE && grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED;
    }
}
