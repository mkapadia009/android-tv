package com.app.itaptv.font_awesome;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by poonam on 22/8/18.
 */

public class FontAwesome {

    /**
     * FontAwesome name with extension.
     * Required to reference the font from the Assets folder
     */
    public static String FONT_NAME = "fonts/fontawesome.ttf";
    //public static String FONT_NAME = "fonts/Rubik.ttf";
    public static String FA_ICON_FACEBOOK = "\uF09A";
    public static String FA_Exclamation = "\uF071";

    /**
     * Get FontAwesome font/type face.
     *
     * @param context = current context.
     * @return FontAwesome font.
     */
    public static Typeface getFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), FONT_NAME);
    }

    /**
     * Sets the icon with the unicode character supported by FontAwesome
     *
     * @param context     = current context.
     * @param unicode     = unicode for the icon.
     * @param uiComponent = UI component (like TextView, Button, etc) to which the icon is to be set.
     */
    public static void setIcon(Context context, String unicode, Object uiComponent) {
        Typeface fontAwesome = getFont(context);
        if (uiComponent instanceof TextView) {
            TextView textView = (TextView) uiComponent;
            textView.setTypeface(fontAwesome);
            textView.setText(unicode);
        }
    }

}
