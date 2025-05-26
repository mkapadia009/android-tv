package com.app.itaptv.utils;

import com.app.itaptv.API.Url;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;

public class GameConstants {

    // public static String GAME_KEY = "185c1909-7c13-4d45-8"; // local (same for all servers)
    // public static String GAME_KEY = "7ff8e237-75a9-44d5-9";
    public static String GAME_KEY = "185c1909-7c13-4d45-8"; // staging (UAT)

    // public static String GAME_HOST = "192.168.2.20"; // local
    //public static String GAME_HOST = "203.153.53.54";
    private final static String GAME_HOST_PROD = "itapuat.infini.work"; // staging (UAT)
    private final static String GAME_HOST_DEV = "itap.infini.work"; // DEV

    public static final int RECCOVERY_ALLOWANCE_TIME = 60;

    public static final int MAX_RECOVERY_ATTEMPT = 5;

    public static final int TURN_TIME = 120;

    public static String SERVER_NAME = "AppWarpS2";

    public static final byte USER_HAND = 1;

    public static final byte RESULT_GAME_OVER = 3;
    public static final byte RESULT_USER_LEFT = 4;

    // error code
    public static final int INVALID_MOVE = 121;
    public static final byte SUBMIT_CARD = 111;

    public static final byte CODE_QUESTION = 78;
    public static final byte CODE_TIMER = 79;
    public static final byte CODE_ATTACHMENT_DATA = 80;
    public static final byte CODE_OPPONENT_NOT_FOUND = 81;
    public static final byte CODE_RESUMED_TIME = 83;

    // GAME_STATUS
    public static final int STOPPED = 71;
    public static final int RUNNING = 72;
    public static final int PAUSED = 73;

    // String constants
    public static String RECOVER_TEXT = MyApp.getAppContext().getString(R.string.recovering);

    // Alert Messages
    public static String ALERT_INIT_EXEC = MyApp.getAppContext().getString(R.string.exception_in_initilization);
    public static String ALERT_ERR_DISCONN = MyApp.getAppContext().getString(R.string.can_not_disconnect);
    public static String ALERT_INV_MOVE = MyApp.getAppContext().getString(R.string.invalid_move);
    public static String ALERT_ROOM_CREATE = MyApp.getAppContext().getString(R.string.room_creation_failed);
    public static String ALERT_CONN_FAIL = MyApp.getAppContext().getString(R.string.connection_failed);
    public static String ALERT_SEND_FAIL = MyApp.getAppContext().getString(R.string.send_move_failed);
    public static String ALERT_CONN_SUCC = MyApp.getAppContext().getString(R.string.connection_success);
    public static String ALERT_CONN_RECOVERED = MyApp.getAppContext().getString(R.string.connection_recovered);
    public static String ALERT_CONN_ERR_RECOVABLE = MyApp.getAppContext().getString(R.string.recoverable_connection_error);
    public static String ALERT_CONN_ERR_NON_RECOVABLE = MyApp.getAppContext().getString(R.string.non_recoverable_connection_error);
    public static String GENERIC_ERR_MESSAGE = MyApp.getAppContext().getString(R.string.GENERIC_API_ERROR_MESSAGE);

    public static final String HOW_TO_PLAY = MyApp.getAppContext().getString(R.string.login_with);

    public static String getGameHost() {
        if (Url.getEnvironment() == Url.Environment.PRODUCTION) {
            return GAME_HOST_PROD;
        }
        return GAME_HOST_DEV;
    }

}
