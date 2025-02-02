package ngo.friendship.satellite.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.json.JSONArray;

import java.util.Calendar;

import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.Utility;

public class Alarm {
    public static final int AUTO_SYNC_ALARM = 1111;
    public static final int ACTIVE_STATUS_ALARM = 1112;
    public static final int NOTIFICATION_STATUS_ALARM = 1113;


    public static void startAutosyncService(Context context) {
        try {
            String confiData = AppPreference.getString(context, KEY.FCM_CONFIGURATION, "[]");
            JSONArray configArry = new JSONArray(confiData);
            int interval = Integer.parseInt(JSONParser.getFcmConfigValue(configArry, "Auto_Sync", "interval"));

            Calendar calCurrent = Calendar.getInstance();
            calCurrent.add(Calendar.MINUTE, interval);
            calCurrent.set(Calendar.SECOND, 0);
            Intent intent = new Intent(context, AutoSyncReceiver.class);
            startRepeating(context, calCurrent, interval * 60 * 1000, AUTO_SYNC_ALARM, intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static void startScheduleService(Context context) {
//        try {
//            String confiData = AppPreference.getString(context, KEY.FCM_CONFIGURATION, "[]");
//            JSONArray configArry = new JSONArray(confiData);
//            int interval = Integer.parseInt(JSONParser.getFcmConfigValue(configArry, "Auto_Sync", "interval"));
//
//            Calendar calCurrent = Calendar.getInstance();
//            calCurrent.add(Calendar.HOUR, 15);
//            calCurrent.add(Calendar.MINUTE, 58);
//            calCurrent.set(Calendar.SECOND, 0);
//            Intent intent = new Intent(context, AutoSyncReceiver.class);
//            startRepeating(context, calCurrent, 14 * 60 * 1000, AUTO_SYNC_ALARM, intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public static void startNotificationService(Context context) {
//        int interval = 1;
//        boolean notificationServiceEnable = true;
//        try {
//
//            String confiData = AppPreference.getString(context, KEY.FCM_CONFIGURATION, "[]");
//            JSONArray configArry = new JSONArray(confiData);
////            try {
////                 interval = Integer.parseInt(JSONParser.getFcmConfigValue(configArry, "Notification_Service", "interval"));
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//            try {
//                 notificationServiceEnable = Boolean.parseBoolean(Utility.getFcmConfigurationValue(context, "Notification_Service", "notificaiton.service.enable", "true"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (notificationServiceEnable){
//                Calendar calCurrent = Calendar.getInstance();
//                calCurrent.add(Calendar.MINUTE, interval);
//                calCurrent.set(Calendar.SECOND, 0);
//                Intent intent = new Intent(context, NotificationReceiver.class);
//                startRepeating(context, calCurrent, interval * 60 * 1000, NOTIFICATION_STATUS_ALARM, intent);
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    public static void startOneTimeAlarmAAA(Context ctx, Calendar cal, int key) {
//        stop(ctx, key);
        Intent intent = new Intent(ctx, AutoSyncReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx.getApplicationContext(), key, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    public static void startRepeating(Context ctx, Calendar time, int interval, int key, Intent intent) {
        stop(ctx, key,intent);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(ctx.getApplicationContext(), key, intent, PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        }else{
            pendingIntent = PendingIntent.getBroadcast(ctx.getApplicationContext(), key, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), interval, pendingIntent);
    }

    public static void stop(Context ctx, int key, Intent intent) {
        PendingIntent pendingIntent = null;
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(ctx.getApplicationContext(), key, intent, PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        }else{
            pendingIntent = PendingIntent.getBroadcast(ctx.getApplicationContext(), key, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        am.cancel(pendingIntent);
    }
}
