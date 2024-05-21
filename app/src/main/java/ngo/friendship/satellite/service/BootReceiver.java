package ngo.friendship.satellite.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Alarm.startAutosyncService(context);
			Alarm.startNotificationService(context);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}



}

