package ngo.friendship.satellite.service;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ngo.friendship.satellite.App;

public class HistoryDataWorker extends Worker {

    private final Context context;
    private String TAG = "MyWorker";
    public HistoryDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get the current time
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTimeString = sdf.format(new Date(currentTime));

        // Log the current time (replace with your actual task)
        // Here, we are just logging the time for demonstration
        android.util.Log.d("MyWorker", "Current time: " + currentTimeString);

        App.getContext().getDB().saveCurrentStock();

        return Result.success();
    }
}
