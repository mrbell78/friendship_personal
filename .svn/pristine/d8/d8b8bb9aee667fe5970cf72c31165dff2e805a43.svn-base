package ngo.friendship.satellite.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ngo.friendship.satellite.App;

public class CurrentStockSyncWorker extends Worker {
    private final Context context;
    public CurrentStockSyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        copyCurrentStock();
        return Result.success();
    }


    @Override
    public void onStopped() {
//        Log.d(TAG, "onStopped called for: " + this.getId());
        super.onStopped();
    }

    private void copyCurrentStock() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        Log.d("CurrentDate", "Current Date: " + formattedDate);
        if (!App.getContext().getDB().isExitMedicineStockHistory(formattedDate)) {
            App.getContext().getDB().saveCurrentStock();
        }
    }
}