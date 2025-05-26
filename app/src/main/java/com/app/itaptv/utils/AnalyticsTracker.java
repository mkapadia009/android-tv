package com.app.itaptv.utils;

import static com.app.itaptv.utils.Analyticals.ACTIVITY_TYPE_AVATAR;
import static com.app.itaptv.utils.Analyticals.GAME_ACTIVITY_TYPE;
import static com.app.itaptv.utils.LocalStorage.KEY_USER_ANALYTICS;

import android.content.Context;

import com.app.itaptv.roomDatabase.AnalyticsData;
import com.app.itaptv.roomDatabase.ListRoomDatabase;
import com.app.itaptv.structure.DeviceDetailsData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.LocationData;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AnalyticsTracker {

    public static final String GAME = "Game";
    public static final String SHOP = "Shop";
    public static final String AVATAR = "Avatar";
    public static final String USER = "User";
    public static final String VIDEO = "Video";

    public static Context context;
    public static Timer playedSecondsTimerGames = null;
    public static TimerTask secondsPlayedTaskGames = null;
    public static int secondsPlayedGames = 0;
    public static boolean isGameOngoing = false;
    public static GameData gamedata;

    public static Timer playedSecondsTimerAvatar = null;
    public static TimerTask secondsPlayedTaskAvatar = null;
    public static int secondsPlayedAvatar = 0;

    public static Timer playedSecondsTimerShop = null;
    public static TimerTask secondsPlayedTaskShop = null;
    public static int secondsPlayedShop = 0;

    public static Timer playedSecondsTimerUser = null;
    public static TimerTask secondsPlayedTaskUser = null;
    public static int secondsPlayedUser = 0;
    public static LocationData locationData = new LocationData("", "", "", "");
    public static DeviceDetailsData deviceDetailsData;

    public static Timer playedSecondsTimerVideo = null;
    public static TimerTask secondsPlayedTaskVideo = null;
    public static int secondsPlayedVideo = 0;
    public static FeedContentData feedContentData;

    private static ListRoomDatabase listRoomDatabase;

    public static void startDurationGames() {
        if (playedSecondsTimerGames == null && secondsPlayedTaskGames == null && gamedata != null) {
            secondsPlayedTaskGames = new TimerTask() {
                @Override
                public void run() {
                    secondsPlayedGames++;
                    if (LocalStorage.getUserData() != null && locationData != null && deviceDetailsData != null) {
                        AnalyticsData analyticsData = new AnalyticsData(GAME_ACTIVITY_TYPE, gamedata.id, gamedata.postTitle, gamedata.postType,
                                String.valueOf(secondsPlayedGames),
                                locationData.getLatitude(), locationData.getLongitude(), locationData.getCity(), locationData.getState(),
                                deviceDetailsData.getBrand(), deviceDetailsData.getDeviceID(), deviceDetailsData.getModel(),
                                deviceDetailsData.getSDK(), deviceDetailsData.getManufacture(), deviceDetailsData.getUser(),
                                deviceDetailsData.getType(), deviceDetailsData.getBase(), deviceDetailsData.getIncremental(), deviceDetailsData.getBoard(),
                                deviceDetailsData.getHost(), deviceDetailsData.getFingerPrint(), deviceDetailsData.getVersionCode(),
                                LocalStorage.getUserData().userId, LocalStorage.getUserData().displayName, LocalStorage.getUserData().mobile,
                                Utility.getCurrentDateTime());
                        LocalStorage.savePlayedGameData(analyticsData);
                    }
                }
            };

            playedSecondsTimerGames = new Timer();
            playedSecondsTimerGames.schedule(secondsPlayedTaskGames, new Date(), 1000);
            isGameOngoing = true;
        }
    }

    public static void stopDurationGames() {
        Log.i("Duration", String.valueOf(secondsPlayedGames));
        listRoomDatabase = ListRoomDatabase.getDatabase(context);
        if (LocalStorage.getPlayedGameData() != null) {
            listRoomDatabase.mediaDAO().saveAnalytics(LocalStorage.getPlayedGameData());
            LocalStorage.savePlayedGameData(null);
            isGameOngoing = false;
        }

        if (playedSecondsTimerGames != null) {
            playedSecondsTimerGames.cancel();
            playedSecondsTimerGames.purge();
            playedSecondsTimerGames = null;
        }
        if (secondsPlayedTaskGames != null) {
            secondsPlayedTaskGames.cancel();
            secondsPlayedTaskGames = null;
        }
    }

    public static void startDurationUser() {
        if (secondsPlayedTaskUser == null && playedSecondsTimerUser == null) {
            secondsPlayedTaskUser = new TimerTask() {
                @Override
                public void run() {
                    secondsPlayedUser++;
                    if (LocalStorage.getUserData() != null && deviceDetailsData != null) {
                        AnalyticsData analyticsData = new AnalyticsData(KEY_USER_ANALYTICS, "", "", "",
                                String.valueOf(secondsPlayedUser),
                               "", "","", "",
                                deviceDetailsData.getBrand(), deviceDetailsData.getDeviceID(), deviceDetailsData.getModel(),
                                deviceDetailsData.getSDK(), deviceDetailsData.getManufacture(), deviceDetailsData.getUser(),
                                deviceDetailsData.getType(), deviceDetailsData.getBase(), deviceDetailsData.getIncremental(), deviceDetailsData.getBoard(),
                                deviceDetailsData.getHost(), deviceDetailsData.getFingerPrint(), deviceDetailsData.getVersionCode(),
                                LocalStorage.getUserData().userId, LocalStorage.getUserData().displayName, LocalStorage.getUserData().mobile,
                                Utility.getCurrentDateTime());
                        LocalStorage.saveUserAnalytics(analyticsData);
                    }
                }
            };

            playedSecondsTimerUser = new Timer();
            playedSecondsTimerUser.schedule(secondsPlayedTaskUser, new Date(), 1000);
        }
    }

    public static void stopDurationUser() {
        listRoomDatabase = ListRoomDatabase.getDatabase(context);
        if (LocalStorage.getUserAnalytics() != null) {
            listRoomDatabase.mediaDAO().saveAnalytics(LocalStorage.getUserAnalytics());
            FirebaseAnalyticsLogs.logEvent().sessionDuration(LocalStorage.getUserAnalytics().getDuration());
            LocalStorage.saveUserAnalytics(null);
        }

        if (playedSecondsTimerAvatar != null) {
            playedSecondsTimerAvatar.cancel();
            playedSecondsTimerAvatar.purge();
            playedSecondsTimerUser = null;
        }
        if (secondsPlayedTaskAvatar != null) {
            secondsPlayedTaskAvatar.cancel();
            secondsPlayedTaskAvatar = null;
        }
    }

    public static void startDurationAvatar() {
        if (playedSecondsTimerAvatar == null && secondsPlayedTaskAvatar == null) {
            secondsPlayedTaskAvatar = new TimerTask() {
                @Override
                public void run() {
                    secondsPlayedAvatar++;
                    if (LocalStorage.getUserData() != null && locationData != null && deviceDetailsData != null) {
                        AnalyticsData analyticsData = new AnalyticsData(ACTIVITY_TYPE_AVATAR, "", "", "",
                                String.valueOf(secondsPlayedAvatar),
                                locationData.getLatitude(), locationData.getLongitude(), locationData.getCity(), locationData.getState(),
                                deviceDetailsData.getBrand(), deviceDetailsData.getDeviceID(), deviceDetailsData.getModel(),
                                deviceDetailsData.getSDK(), deviceDetailsData.getManufacture(), deviceDetailsData.getUser(),
                                deviceDetailsData.getType(), deviceDetailsData.getBase(), deviceDetailsData.getIncremental(), deviceDetailsData.getBoard(),
                                deviceDetailsData.getHost(), deviceDetailsData.getFingerPrint(), deviceDetailsData.getVersionCode(),
                                LocalStorage.getUserData().userId, LocalStorage.getUserData().displayName, LocalStorage.getUserData().mobile,
                                Utility.getCurrentDateTime());
                        LocalStorage.saveAvatarAnalyticsData(analyticsData);
                    }
                }
            };
            playedSecondsTimerAvatar = new Timer();
            playedSecondsTimerAvatar.schedule(secondsPlayedTaskAvatar, new Date(), 1000);
        }
    }

    public static void stopDurationAvatar() {
        listRoomDatabase = ListRoomDatabase.getDatabase(context);
        if (LocalStorage.getAvatarAnalyticsData() != null) {
            listRoomDatabase.mediaDAO().saveAnalytics(LocalStorage.getAvatarAnalyticsData());
            LocalStorage.saveAvatarAnalyticsData(null);
        }

        if (playedSecondsTimerAvatar != null) {
            playedSecondsTimerAvatar.cancel();
            playedSecondsTimerAvatar.purge();
            playedSecondsTimerUser = null;
        }
        if (secondsPlayedTaskAvatar != null) {
            secondsPlayedTaskAvatar.cancel();
            secondsPlayedTaskAvatar = null;
        }
    }

    public static void startDurationShop() {
        if (playedSecondsTimerShop == null && secondsPlayedTaskShop == null) {
            secondsPlayedTaskShop = new TimerTask() {
                @Override
                public void run() {
                    secondsPlayedShop++;
                    if (LocalStorage.getUserData() != null && locationData != null && deviceDetailsData != null) {
                        AnalyticsData analyticsData = new AnalyticsData(Analyticals.ACTIVITY_TYPE_SHOP, "", "", "",
                                String.valueOf(secondsPlayedShop),
                                locationData.getLatitude(), locationData.getLongitude(), locationData.getCity(), locationData.getState(),
                                deviceDetailsData.getBrand(), deviceDetailsData.getDeviceID(), deviceDetailsData.getModel(),
                                deviceDetailsData.getSDK(), deviceDetailsData.getManufacture(), deviceDetailsData.getUser(),
                                deviceDetailsData.getType(), deviceDetailsData.getBase(), deviceDetailsData.getIncremental(), deviceDetailsData.getBoard(),
                                deviceDetailsData.getHost(), deviceDetailsData.getFingerPrint(), deviceDetailsData.getVersionCode(),
                                LocalStorage.getUserData().userId, LocalStorage.getUserData().displayName, LocalStorage.getUserData().mobile,
                                Utility.getCurrentDateTime());
                        LocalStorage.saveShopAnalyticsData(analyticsData);
                    }
                }
            };
            playedSecondsTimerShop = new Timer();
            playedSecondsTimerShop.schedule(secondsPlayedTaskShop, new Date(), 1000);
        }
    }

    public static void stopDurationShop() {
        if (playedSecondsTimerShop != null && secondsPlayedTaskShop != null && secondsPlayedShop != 0) {
            listRoomDatabase = ListRoomDatabase.getDatabase(context);
            if (LocalStorage.getShopAnalyticsData() != null) {
                listRoomDatabase.mediaDAO().saveAnalytics(LocalStorage.getShopAnalyticsData());
                LocalStorage.saveShopAnalyticsData(null);
                secondsPlayedShop = 0;
            }
        }
        if (playedSecondsTimerShop != null) {
            playedSecondsTimerShop.cancel();
            playedSecondsTimerShop.purge();
            playedSecondsTimerShop = null;
        }
        if (secondsPlayedTaskShop != null) {
            secondsPlayedTaskShop.cancel();
            secondsPlayedTaskShop = null;
        }
    }

    public static void startDurationVideo() {
        if (playedSecondsTimerVideo == null && secondsPlayedTaskVideo == null) {
            secondsPlayedTaskVideo = new TimerTask() {
                @Override
                public void run() {
                    secondsPlayedVideo++;
                    if (LocalStorage.getUserData() != null && locationData != null && deviceDetailsData != null && feedContentData != null) {
                        AnalyticsData analyticsData = new AnalyticsData(Analyticals.ACTIVITY_TYPE_PLAY_AUDIO, feedContentData.postId,
                                feedContentData.postTitle,
                                feedContentData.postType,
                                String.valueOf(secondsPlayedVideo),
                                locationData.getLatitude(), locationData.getLongitude(), locationData.getCity(), locationData.getState(),
                                deviceDetailsData.getBrand(), deviceDetailsData.getDeviceID(), deviceDetailsData.getModel(),
                                deviceDetailsData.getSDK(), deviceDetailsData.getManufacture(), deviceDetailsData.getUser(),
                                deviceDetailsData.getType(), deviceDetailsData.getBase(), deviceDetailsData.getIncremental(), deviceDetailsData.getBoard(),
                                deviceDetailsData.getHost(), deviceDetailsData.getFingerPrint(), deviceDetailsData.getVersionCode(),
                                LocalStorage.getUserData().userId, LocalStorage.getUserData().displayName, LocalStorage.getUserData().mobile,
                                Utility.getCurrentDateTime());
                        LocalStorage.saveVideoAnalyticsData(analyticsData);
                    }
                }
            };
            playedSecondsTimerVideo = new Timer();
            playedSecondsTimerVideo.schedule(secondsPlayedTaskVideo, new Date(), 1000);
        }
    }

    public static void stopDurationVideo() {
        if (playedSecondsTimerVideo != null && secondsPlayedTaskVideo != null && secondsPlayedVideo != 0) {
            listRoomDatabase = ListRoomDatabase.getDatabase(context);
            if (LocalStorage.getVideoAnalyticsData() != null) {
                listRoomDatabase.mediaDAO().saveAnalytics(LocalStorage.getVideoAnalyticsData());
                LocalStorage.saveVideoAnalyticsData(null);
                secondsPlayedVideo = 0;
            }
        }
        if (playedSecondsTimerVideo != null) {
            playedSecondsTimerVideo.cancel();
            playedSecondsTimerVideo.purge();
            playedSecondsTimerVideo = null;
        }
        if (secondsPlayedTaskVideo != null) {
            secondsPlayedTaskVideo.cancel();
            secondsPlayedTaskVideo = null;
        }
    }


    public static void pauseTimer(String entity) {
        switch (entity) {
            case GAME:
                if (playedSecondsTimerGames != null) {
                    playedSecondsTimerGames.cancel();
                    playedSecondsTimerGames.purge();
                    playedSecondsTimerGames = null;
                }
                if (secondsPlayedTaskGames != null) {
                    secondsPlayedTaskGames.cancel();
                    secondsPlayedTaskGames = null;
                }
                break;
            case SHOP:
                if (playedSecondsTimerShop != null) {
                    playedSecondsTimerShop.cancel();
                    playedSecondsTimerShop.purge();
                    playedSecondsTimerShop = null;
                }
                if (secondsPlayedTaskShop != null) {
                    secondsPlayedTaskShop.cancel();
                    secondsPlayedTaskShop = null;
                }
                break;
            case AVATAR:
                if (playedSecondsTimerAvatar != null) {
                    playedSecondsTimerAvatar.cancel();
                    playedSecondsTimerAvatar.purge();
                    playedSecondsTimerAvatar = null;
                }
                if (secondsPlayedTaskAvatar != null) {
                    secondsPlayedTaskAvatar.cancel();
                    secondsPlayedTaskAvatar = null;
                }
                break;
            case USER:
                if (playedSecondsTimerUser != null) {
                    playedSecondsTimerUser.cancel();
                    playedSecondsTimerUser.purge();
                    playedSecondsTimerUser = null;
                }
                if (secondsPlayedTaskUser != null) {
                    secondsPlayedTaskUser.cancel();
                    secondsPlayedTaskUser = null;
                }
                break;
            case VIDEO:
                if (playedSecondsTimerVideo != null) {
                    playedSecondsTimerVideo.cancel();
                    playedSecondsTimerVideo.purge();
                    playedSecondsTimerVideo = null;
                }
                if (secondsPlayedTaskVideo != null) {
                    secondsPlayedTaskVideo.cancel();
                    secondsPlayedTaskVideo = null;
                }
                break;
        }
    }

    public static void resumeTimer(String entity) {
        switch (entity) {
            case GAME:
                startDurationGames();
                break;
            case SHOP:
                startDurationShop();
                break;
            case AVATAR:
                break;
            case USER:
                startDurationUser();
                break;
        }
    }
}
