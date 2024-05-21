package ngo.friendship.satellite.service;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificaitonWorker extends Worker {
    private final Context context;
    private String TAG = "MyWorker";

    public NotificaitonWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Intent intent = new Intent(this.context, NotificationService.class);
        ContextCompat.startForegroundService(context, intent);
        return Result.success();
    }

    @Override
    public void onStopped() {
//        Log.d(TAG, "onStopped called for: noti" + this.getId());
        super.onStopped();
    }
}