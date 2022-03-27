package io.those.upnext.util;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import io.those.upnext.R;

public class ColorUtil {
    public static int getTextColor(Context context, int baseColor) {
        // Text color
        int colorFont      = ContextCompat.getColor(context, R.color.font_text);
        int colorFontALt   = ContextCompat.getColor(context, R.color.font_text_alt);
        int chosenFontColor = colorFont;

        if (ColorUtils.calculateContrast(colorFont, baseColor) < 4.5) {
            // need different font (https://miromatech.com/android/contrast-ratio/)
            chosenFontColor = colorFontALt;
        }

        return chosenFontColor;
    }
}
