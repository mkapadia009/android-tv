package com.app.itaptv.utils;

import android.content.Context;
import android.text.format.DateUtils;

import com.app.itaptv.R;
import com.app.itaptv.structure.GameData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by poonam on 19/9/18.
 */

public class GameDateValidation {


    /**
     * If start and end both dates matches with today's date then method returns only time - e.g. Today, <start_time> - <end_time>
     * Else it returns both date and time e.g Sep 17, <start_time> - Sep 20, <end_time>
     *
     * @return Returns label stating start and end date time of the game
     */
    public static String getDateTimeLabel(Context context, String gameStartDate, String gameEndDate) {

        String dateFormat = "";
        String strLabel = "";
        String equalDate = "";

        String startDt = gameStartDate;
        String endDt = gameEndDate;


        if (isCurrentDate(startDt) && isCurrentDate(endDt)) {
            dateFormat = GameData.timeFormat;
            strLabel = context.getString(R.string.text_today_time);

        } else if (equalsDate(startDt, endDt)) {
            dateFormat = GameData.timeFormat;
            strLabel = context.getString(R.string.text_equal_date);
            equalDate = Utility.formatDate(GameData.sourceDateFormat, GameData.dateFormat, gameStartDate);
        } else {
            dateFormat = GameData.destDateFormat;
            strLabel = context.getString(R.string.text_date_time);
        }

        startDt = Utility.formatDate(GameData.sourceDateFormat, dateFormat, gameStartDate);
        endDt = Utility.formatDate(GameData.sourceDateFormat, dateFormat, gameEndDate);
        return !equalDate.equals("") ? String.format(strLabel, equalDate, startDt, endDt) : String.format(strLabel, startDt, endDt);

    }

    /**
     * @return Returns game end time with appropriate label
     */
    public static String getDateTime(Context context, String gameDateTime) {

        String dateFormat = "";

        String dateTime = gameDateTime;

        if (isCurrentDate(dateTime)) {
            String startsIn = getTimeLeft(gameDateTime);
            dateTime = String.format(context.getString(R.string.starts_in), startsIn);
            /*dateFormat = GameData.timeFormat;
            String strTodayDate = context.getString(R.string.text_today_time);
            dateTime = Utility.formatDate(GameData.sourceDateFormat, dateFormat, gameDateTime);
            dateTime = String.format(strTodayDate, dateTime);*/

        } else if (isTomorrowDate(dateTime)) {
            dateFormat = GameData.timeFormat;
            String strTodayDate = context.getString(R.string.text_tomorrow_time);
            dateTime = Utility.formatDate(GameData.sourceDateFormat, dateFormat, gameDateTime);
            dateTime = String.format(strTodayDate, dateTime);

        } else {
            dateFormat = GameData.destDateFormat;
            dateTime = Utility.formatDate(GameData.sourceDateFormat, dateFormat, gameDateTime);
        }
        return dateTime;

    }


    /**
     * checks whether specified date matches with today's date
     *
     * @param date date in string format
     * @return true if specified date matches with today's date else false
     */
    public static boolean isCurrentDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GameData.defaultDateFormat, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        String currentDate = simpleDateFormat.format(calendar.getTime());

        try {
            Date dtCurrentDate = simpleDateFormat.parse(currentDate);
            Date dtDate = simpleDateFormat.parse(Utility.formatDate(GameData.sourceDateFormat, GameData.defaultDateFormat, date));

            switch (dtDate.compareTo(dtCurrentDate)) {
                case 0:
                    return true;
                default:
                    return false;
            }
        } catch (ParseException e) {
            Log.e("Parse Exception", e.getMessage());
        }
        return false;
    }

    public static boolean isTomorrowDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GameData.defaultDateFormat, Locale.ENGLISH);
        try {

            Date dtDate = simpleDateFormat.parse(Utility.formatDate(GameData.sourceDateFormat, GameData.defaultDateFormat, date));
            return isTomorrow(dtDate);
        } catch (ParseException e) {
            Log.e("Parse Exception", e.getMessage());
        }
        return false;
    }

    public static boolean isTomorrow(Date d) {
        return DateUtils.isToday(d.getTime() - DateUtils.DAY_IN_MILLIS);
    }


    /**
     * checks whether start and end dates matches
     *
     * @param startDate start date of the game
     * @param endDate   end date of the game
     * @return true in case both dates are same or false
     */
    public static boolean equalsDate(String startDate, String endDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GameData.defaultDateFormat);

        try {
            Date dtStartDate = simpleDateFormat.parse(Utility.formatDate(GameData.sourceDateFormat, GameData.defaultDateFormat, startDate));
            Date dtEndDate = simpleDateFormat.parse(Utility.formatDate(GameData.sourceDateFormat, GameData.defaultDateFormat, endDate));

            switch (dtStartDate.compareTo(dtEndDate)) {
                case 0:
                    return true;
                default:
                    return false;
            }
        } catch (ParseException e) {
            Log.e("Parse Exception", e.getMessage());
        }
        return false;
    }


    /**
     * checks whether today's date is in between game's start and end dates
     *
     * @param gameStartDate start date
     * @param gameEndDate   end date
     * @return true if today's date is less than or equal to end date of the game else false
     */
    public static String getInvalidDateMsg(Context context, String gameStartDate, String gameEndDate) {
        String message = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GameData.sourceDateFormat1);
        Calendar calendar = Calendar.getInstance();
        String currentDate = simpleDateFormat.format(calendar.getTime());

        try {
            Date dtOrgStartDate = simpleDateFormat.parse(gameStartDate);
            Date dtOrgEndDate = simpleDateFormat.parse(gameEndDate);
            Date dtOrgCurrentDate = simpleDateFormat.parse(currentDate);

            if (dtOrgStartDate.after(dtOrgCurrentDate)) {
                String startDate = getDateTimeLabel(context, gameStartDate);
                message = String.format(context.getString(R.string.error_msg_start_game_time), startDate);
                return message;

            } else if (dtOrgStartDate.before(dtOrgCurrentDate) || dtOrgStartDate.equals(dtOrgCurrentDate)) {
                if (dtOrgEndDate.before(dtOrgCurrentDate)) {
                    message = String.format(context.getString(R.string.msg_winners_will_be_declared));
                    return message;
                }

            }
            return message;

            /*if (dtOrgCurrentDate.equals(dtOrgStartDate) || dtOrgCurrentDate.after(dtOrgStartDate)) {
                return dtOrgCurrentDate.before(dtOrgEndDate);
            }*/
            //return false;
        } catch (ParseException e) {
            Log.e("Parse Exception", e.getMessage());
        }
        //return false;
        return message;
    }

    /**
     * If start and end both dates matches with today's date then method returns only time - e.g. Today, <start_time> or <end_time>
     * Else it returns both date and time e.g Sep 17, <start_time> or Sep 20, <end_time>
     *
     * @return Returns label stating start or end date time of the game
     */
    public static String getDateTimeLabel(Context context, String gameDate) {

        String dateFormat = "";
        String strLabel = "";


        String endDt = gameDate;


        if (isCurrentDate(endDt)) {
            dateFormat = GameData.timeFormat;
            strLabel = context.getString(R.string.text_end_today_time);

        } else {
            dateFormat = GameData.destDateFormat;
            strLabel = context.getString(R.string.text_end_date_time);
        }

        endDt = Utility.formatDate(GameData.sourceDateFormat, dateFormat, gameDate);
        return String.format(strLabel, endDt);

    }

    /**
     * If start and end both dates matches with today's date then method returns only time - e.g. at Today, <start_time> or <end_time>
     * Else it returns both date and time e.g on Sep 17, <start_time> or Sep 20, <end_time>
     *
     * @return Returns label stating start or end date time of the game
     */
    public static String getDateTimeLabelAT(Context context, String gameDate) {

        String dateFormat = "";
        String strLabel = "";


        String endDt = gameDate;


        if (isCurrentDate(endDt)) {
            dateFormat = GameData.timeFormat;
            strLabel = "at " + context.getString(R.string.text_end_today_time);

        } else {
            dateFormat = GameData.destDateFormat;
            strLabel = "on " + context.getString(R.string.text_end_date_time);
        }

        endDt = Utility.formatDate(GameData.sourceDateFormat, dateFormat, gameDate);
        return String.format(strLabel, endDt);

    }

    public static String getTimeLeft(String dateTime) {
        String timeLeft = "0";
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String strCurrentTime = calendarDateFormat.format(currentTime);

        try {
            Date formattedCurrentDateTime = calendarDateFormat.parse(strCurrentTime);
            Date formattedGivenDateTime = calendarDateFormat.parse(dateTime);
            long difference = formattedGivenDateTime.getTime() - formattedCurrentDateTime.getTime();

            Log.e("game Mill", "" + formattedGivenDateTime.getTime());
            Log.e("current Mill", "" + formattedCurrentDateTime.getTime());
            Log.e("remain Mill", "" + difference);

            if (TimeUnit.MILLISECONDS.toHours(difference) > 0) {
                timeLeft = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(difference),
                        TimeUnit.MILLISECONDS.toMinutes(difference) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)),
                        TimeUnit.MILLISECONDS.toSeconds(difference) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference)));
            } else {
                timeLeft = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(difference) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)),
                        TimeUnit.MILLISECONDS.toSeconds(difference) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference)));
            }

            Log.e("time left", timeLeft);

            return timeLeft;


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeLeft;


    }

    public static String getTimeLeft(long millisecondsLeft) {
        String timeLeft = "";

        if (TimeUnit.MILLISECONDS.toHours(millisecondsLeft) > 0) {
            timeLeft = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisecondsLeft),
                    TimeUnit.MILLISECONDS.toMinutes(millisecondsLeft) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisecondsLeft)),
                    TimeUnit.MILLISECONDS.toSeconds(millisecondsLeft) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecondsLeft)));
        } else {
            timeLeft = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisecondsLeft) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisecondsLeft)),
                    TimeUnit.MILLISECONDS.toSeconds(millisecondsLeft) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecondsLeft)));
        }

        return timeLeft;
    }

}
