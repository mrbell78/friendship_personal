package ngo.friendship.satellite.utility;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

//import ngo.friendship.mhealth.activities.NotificationShowActivity;
import ngo.friendship.satellite.model.NotificationItem;
import ngo.friendship.satellite.model.PatientInterviewDoctorFeedback;

public class NotificationUtility extends Service {

    public static StringBuilder sbMessageToBePrint = new StringBuilder();
    public static ArrayList<NotificationItem> notificationItemList = new ArrayList<NotificationItem>();
    PatientInterviewDoctorFeedback patientInterviewDoctorFeedback;

//    NotificationUtility(PatientInterviewDoctorFeedback patientInterviewDoctorFeedback){
//        this.patientInterviewDoctorFeedback=patientInterviewDoctorFeedback;
//    }


    public void showNotification(String title, String message, int imageId, boolean showProgress, boolean isAutoCancel) {
        Log.e("notification_data", "title -> " + title + "  message ->" + message + "  isAutoCancel ->" + isAutoCancel);
        sbMessageToBePrint.append("\n");
        sbMessageToBePrint.append(message);
        sbMessageToBePrint.append("\n");


//        Intent intent = new Intent(this, NotificationShowActivity.class);
//        intent.putExtra("NOTIF_MESSAGE", sbMessageToBePrint.toString());
//        intent.putExtra("NOTIF_ID", 9999);

//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder mBuilder;
//        String channelId = title;
//        String channelName = title;
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel mChannel = new NotificationChannel(
//                    channelId, channelName, importance);
//            mChannel.setSound(null, null);
//            mNotificationManager.createNotificationChannel(mChannel);
//
//            mBuilder =
//                    new NotificationCompat.Builder(this, channelId)
//                            .setSmallIcon(imageId)
//                            .setContentTitle("[mHealth] is running...")
//                            .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
//                            .setDefaults(0)
//                            .setAutoCancel(true);
//
//
//            Notification notif = mBuilder.build();
//            notif.flags |= Notification.FLAG_FOREGROUND_SERVICE;
//            startForeground(1, notif);
//            mBuilder.setProgress(100, 0, showProgress);
//        } else {
//            mBuilder =
//                    new NotificationCompat.Builder(this)
//                            .setSmallIcon(imageId)
//                            .setContentTitle("[mHealth] " + title)
//                            .setContentText(message)
//                            .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
//                            .setDefaults(0)
//                            .setAutoCancel(true);
//            mBuilder.setProgress(100, 0, showProgress);
//            Notification notif = mBuilder.build();
//
//            notif.flags |= Notification.FLAG_AUTO_CANCEL;
//            mNotificationManager.cancel(9999);
//        }
//
//
//        Log.e("sdfsdf", sbMessageToBePrint.toString());
//        Log.e("*************", "-------------------------");
    }

    public void addNotification(String message, String type) {
        NotificationItem notifItem = new NotificationItem();
        notifItem.setTitle(message);
        notifItem.setNotificationType(type);
        Log.e("NOTIFICATION ADD", notifItem+"");
        notificationItemList.add(notifItem);
    }

//    public void startSync()
//    {
//        String type="Sync Start";
//        String message="Sync started at "+Utility.getDateTimeFromMillisecond(Calendar.getInstance().getTimeInMillis(), Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
//        showNotification(type,message, R.drawable.ic_launcher, true ,false);
//        addNotification(message, type);
//    }

    public final IBinder mBinder = new Binder()
    {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply,
                                     int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("onBind start","");
      //  App.getContext().setServiceRunning(true);
//        startSync();
        return mBinder;
    }
}
