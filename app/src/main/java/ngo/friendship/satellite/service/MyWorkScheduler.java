package ngo.friendship.satellite.service;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MyWorkScheduler {

    public static void scheduleWork() {
        // Define constraints for the task (optional)
        Constraints constraints = new Constraints.Builder()
                .build();

        // Create a PeriodicWorkRequest to run the task every day
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                HistoryDataWorker.class, // Your worker class
                1, // Repeat interval
                TimeUnit.DAYS)// Set constraints (optional)
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS) // Set initial delay until 6 PM
                .build();

        // Enqueue the work request
        WorkManager.getInstance().enqueue(workRequest);
    }

    private static long calculateInitialDelay() {
        // Calculate the initial delay until 6 PM
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16); // Set the hour to 18:00 (6 PM)
        calendar.set(Calendar.MINUTE, 47); // Set minute to 0
        calendar.set(Calendar.SECOND, 0); // Set second to 0

        long currentTimeMillis = System.currentTimeMillis();
        long triggerTimeMillis = calendar.getTimeInMillis();

        // If the current time is past 6 PM, schedule it for the next day
        if (currentTimeMillis > triggerTimeMillis) {
            calendar.add(Calendar.DAY_OF_YEAR, 1); // Move to the next day
            triggerTimeMillis = calendar.getTimeInMillis(); // Get the trigger time for the next day
        }

        return triggerTimeMillis - currentTimeMillis; // Calculate initial delay
    }
}
