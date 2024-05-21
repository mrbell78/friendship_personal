package ngo.friendship.satellite.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.NotificationItem;
import ngo.friendship.satellite.ui.login.LoginPinActivity;
import ngo.friendship.satellite.ui.notification.NotificationActivity;
import ngo.friendship.satellite.utility.AppPreference;

public class NotificationService extends Service {
    private static BootReceiver panicReceiver;

    public static long MAX_TRY = 1;
    public static StringBuilder sbMessageToBePrint;
    public static ArrayList<NotificationItem> notificationList;
    public static StringBuilder sbMessageToBePrintForDoctorfeedback;
    public static ArrayList<NotificationItem> notificationListForDoctorfeedback;
    private String manualSync = "";

    @Override
    public void onCreate() {
        super.onCreate();
        sbMessageToBePrint = new StringBuilder();
        notificationList = new ArrayList<NotificationItem>();

        sbMessageToBePrintForDoctorfeedback = new StringBuilder();
        notificationListForDoctorfeedback = new ArrayList<NotificationItem>();
        registerScreenReceiver();
    }

    /**
     * Start sync.
     */
    private void startSync() {
      //  BusProvider.getBus().postSticky("Notify");
        sbMessageToBePrint = new StringBuilder();
        String type = "Start notification service";
        String message = "";
        showServiceNotification(type, message, R.drawable.ic_launcher, true, false);
        addNotification(message, type);
        if (App.loadApplicationData(this)) {

            JSONArray allNotification = App.getContext().getDB().getUnNotifyNotificationList();
            if (allNotification.length() > 0) {
                for (int i = 0; i < allNotification.length(); i++) {
                    JSONObject singleNotification = new JSONObject();
                    try {
                        singleNotification = allNotification.getJSONObject(i);
                        App.getContext().getDB().updateNotificationStatus(JSONParser.getLong(singleNotification, "S_NOTIFICATION_ID"), 1);
//                        JSONParser.getString(singleNotification, "S_NOTIFICATION_ID")
                      //  showNotification(NotificationService.this, singleNotification, new Random().nextInt(9999), R.drawable.warning);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                }
            }


            stopForeground(true);
            stopSelf();

        } else {
            type = "Sync Stop";
            message = "Can't load application data";
            showServiceNotification(type, message, R.drawable.ic_launcher, false, false);
            addNotification(message, type);
            //endSync();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(panicReceiver);
        panicReceiver = null;

        App.getContext().setNotificationServiceRunning(false);
    }


    @Override
    public IBinder onBind(Intent arg0) {
        App.getContext().setNotificationServiceRunning(true);
        startSync();
        return mBinder;
    }


    public void showServiceNotification(String title, String message, int imageId, boolean showProgress, boolean isAutoCancel) {
        sbMessageToBePrint.append("\n");
        sbMessageToBePrint.append(message);
        sbMessageToBePrint.append("\n");

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;
        String channelId = title;
        String channelName = title;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(mChannel);

            mBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(imageId)
                            .setContentTitle("[SatelliteCare] is running...")
                            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                            .setDefaults(0)
                            .setAutoCancel(true);


            Notification notif = mBuilder.build();
            notif.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
            startForeground(1, notif);
            mBuilder.setProgress(100, 0, showProgress);
        } else {
            mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(imageId)
                            .setContentTitle("[SatelliteCare] " + title)
                            .setContentText(message)
                            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                            .setDefaults(0)
                            .setAutoCancel(true);
            mBuilder.setProgress(100, 0, showProgress);
            Notification notif = mBuilder.build();


            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.cancel(9999);

        }

    }

    public void addNotification(String message, String type) {
        NotificationItem notifItem = new NotificationItem();
        notifItem.setTitle(message);
        notifItem.setNotificationType(type);
        Log.e("NOTIFICATION ADD", notifItem + "");
        notificationList.add(notifItem);
    }


    /**
     * This is the object that receives interactions from clients.  See RemoteService
     * for a more complete example.
     */
    private final IBinder mBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply,
                                     int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        App.getContext().setNotificationServiceRunning(true);
        startSync();
        return super.onStartCommand(intent, flags, startId);
    }





    private void registerScreenReceiver() {
        panicReceiver = new BootReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);

        registerReceiver(panicReceiver, filter);
    }

}
