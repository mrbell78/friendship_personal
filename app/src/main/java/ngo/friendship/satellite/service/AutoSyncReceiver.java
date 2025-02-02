package ngo.friendship.satellite.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.utility.AppPreference;

// TODO: Auto-generated Javadoc

/**
 * The Class AlarmReceiver.
 */
public class AutoSyncReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Log.e("Alarm ", "Fire at AutoSyncReceiver " + Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS.format(Calendar.getInstance().getTime()));

                long userState = AppPreference.getLong(context, Column.STATE, -1);

                JSONArray configArry = AppPreference.getFCMConfigration(context);

                String StartTime = JSONParser.getFcmConfigValue(configArry, "Auto_Sync", "start_time");
                String EndTime = JSONParser.getFcmConfigValue(configArry, "Auto_Sync", "end_time");

                Calendar calStart = Calendar.getInstance();
                String startTimeParts[] = StartTime.split(":");
                calStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
                calStart.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));
                calStart.set(Calendar.SECOND, 0);

                Calendar calEnd = Calendar.getInstance();
                String endTimeParts[] = EndTime.split(":");
                calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeParts[0]));
                calEnd.set(Calendar.MINUTE, Integer.parseInt(endTimeParts[1]));
                calEnd.set(Calendar.SECOND, 0);
                Calendar calCurrent = Calendar.getInstance();
                if (userState == 1 && (calCurrent.getTimeInMillis() >= calStart.getTimeInMillis()) && (calCurrent.getTimeInMillis() <= calEnd.getTimeInMillis())) {
                    AutoSyncService.MAX_TRY = Integer.parseInt(JSONParser.getFcmConfigValue(configArry, "Auto_Sync", "number_of_try"));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder ( AutoSyncWorker.class ).addTag ( "BACKUP_WORKER_TAG" ).build ();
                        WorkManager.getInstance(context).enqueue (request);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        context.startForegroundService(new Intent(context, AutoSyncService.class));
                        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder ( AutoSyncWorker.class ).addTag ( "BACKUP_WORKER_TAG" ).build ();
                        WorkManager.getInstance(context).enqueue (request);
                    } else {
                        context.startService(new Intent(context, AutoSyncService.class));
                    }
                }
        } catch (Exception e) {
            Log.e("AlamManagerError", e.getMessage());
            e.printStackTrace();
        }

        try {
            JSONArray configArry = AppPreference.getFCMConfigration(context);
            String StartTime = JSONParser.getFcmConfigValue(configArry, "Auto_Sync", "start.time.stock.history");
            String EndTime = JSONParser.getFcmConfigValue(configArry, "Auto_Sync", "end.time.stock.history");
            Calendar calStart = Calendar.getInstance();
            String startTimeParts[] = StartTime.split(":");
            calStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
            calStart.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));
            calStart.set(Calendar.SECOND, 0);

            Calendar calEnd = Calendar.getInstance();
            String endTimeParts[] = EndTime.split(":");
            calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeParts[0]));
            calEnd.set(Calendar.MINUTE, Integer.parseInt(endTimeParts[1]));
            calEnd.set(Calendar.SECOND, 0);
            Calendar calCurrent = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
            if ((calCurrent.getTimeInMillis() >= calStart.getTimeInMillis()) && (calCurrent.getTimeInMillis() <= calEnd.getTimeInMillis())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    OneTimeWorkRequest request = new OneTimeWorkRequest.Builder ( CurrentStockSyncWorker.class ).addTag ( "BACKUP_WORKER_TAG" ).build ();
                    WorkManager.getInstance(context).enqueue (request);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        context.startForegroundService(new Intent(context, AutoSyncService.class));
                    OneTimeWorkRequest request = new OneTimeWorkRequest.Builder ( CurrentStockSyncWorker.class ).addTag ( "BACKUP_WORKER_TAG" ).build ();
                    WorkManager.getInstance(context).enqueue (request);
                } else {
                    context.startService(new Intent(context, CurrentStockSyncWorker.class));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
