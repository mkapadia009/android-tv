package com.app.itaptv.custom_interface;

import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by poonam on 4/2/19.
 */

public class CustomRunnable implements Runnable {
    public TextView holder;
    public long millisUntilFinished;
    public static boolean isCancelled;
    Handler handler;
    String gameEndDate;
    CountDownTimer countDownTimer;

    public CustomRunnable(Handler handler, TextView holder, String gameEndDate) {
        this.handler = handler;
        this.holder = holder;
        //this.millisUntilFinished = millisUntilFinished;
        this.gameEndDate = gameEndDate;
    }

    @Override
    public void run() {
        /*String timeLeft = GameDateValidation.getTimeLeft(millisUntilFinished);
        holder.setText(timeLeft);
        millisUntilFinished -= 1000;
        handler.postDelayed(this,1000);*/


        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (!isCancelled) {
                Date formattedGivenDateTime = calendarDateFormat.parse(gameEndDate);
                long gameExpireMilliseconds = formattedGivenDateTime.getTime();
                long currentTimeMilliseconds = System.currentTimeMillis();
                millisUntilFinished = gameExpireMilliseconds - currentTimeMilliseconds;
                String timeLeft = GameDateValidation.getTimeLeft(millisUntilFinished);
                Log.e("Start Time", timeLeft);
                holder.setText(timeLeft);

                millisUntilFinished -= 1000;
                handler.postDelayed(this, 1000);
            }


            /*countDownTimer = new CountDownTimer(difference, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String timeLeft = GameDateValidation.getTimeLeft(millisUntilFinished);
                    Log.e("Start Time", timeLeft);
                    holder.setText(timeLeft);
                }

                @Override
                public void onFinish() {
                    //showAlertDialog();

                }
            }.start();*/

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
