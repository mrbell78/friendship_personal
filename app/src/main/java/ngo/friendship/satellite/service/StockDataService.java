package ngo.friendship.satellite.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.constants.ActivityDataKey;

public class StockDataService extends Service {

    private static BootReceiver panicReceiver;

    public static long MAX_TRY = 1;
    public static StringBuilder sbMessageToBePrint;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        saveCurrentStock();
        return mBinder;
    }

    private final IBinder mBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply,
                                     int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };

    private boolean isFource = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("onStartCommand start", startId + "");
        try {
            if (intent.getExtras().containsKey(ActivityDataKey.IS_FORCE)) {
                isFource = intent.getExtras().getBoolean(ActivityDataKey.IS_FORCE);
              //  manualSync = intent.getExtras().getString(ActivityDataKey.MANUAL_SYNC, "");
                Log.e("Service onStartCommand", "" + isFource);
            }
        } catch (Exception ex) {

        }

        App.getContext().setDataSaveHistoryServiceRunning(true);
        saveCurrentStock();
        return super.onStartCommand(intent, flags, startId);
    }


    public void saveCurrentStock(){
        App.getContext().getDB().saveCurrentStock();
        Log.d("stocksave", "onReceive: .........stock history is saving");
    }
}
