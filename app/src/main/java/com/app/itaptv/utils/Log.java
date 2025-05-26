package com.app.itaptv.utils;

/**
 * Created by Poona on 11/10/2016.
 */
public class Log {
    private static boolean shouldPrintLog() {
        return true;
        //return Url.getEnvironment() != Url.Environment.PRODUCTION;
    }

    public static void i(String tag, String string) {
        if (shouldPrintLog()) android.util.Log.i(tag, string);
    }

    public static void e(String tag, String string) {
        if (shouldPrintLog()) android.util.Log.e(tag, string);
    }

    public static void d(String tag, String string) {
        if (shouldPrintLog()) android.util.Log.d(tag, string);
    }

    public static void v(String tag, String string) {
        if (shouldPrintLog()) android.util.Log.v(tag, string);
    }

    public static void w(String tag, String string) {
        if (shouldPrintLog()) android.util.Log.w(tag, string);
    }
}

