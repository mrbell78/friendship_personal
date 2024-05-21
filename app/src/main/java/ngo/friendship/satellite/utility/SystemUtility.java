/**
 * @author Kayum Hossan
 * Date: 18-03-2012
 * Description: Internet connectivity check. open WiFi settings activity
 * Last Update: 16th Jan 2014 
 * 
 */

package ngo.friendship.satellite.utility;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;

// TODO: Auto-generated Javadoc

/**
 * The Class InternetConnectivity.
 */
public class SystemUtility {
	
	/**
	 * Check if the device is connected with Internet.
	 *
	 * @param context The application context
	 * @return <strong>true</strong> if connected. <strong>false</strong> otherwise
	 */
	public static boolean isConnectedToInternet(Context context)
	{
		// Check intenet connectivity
		boolean connected = false;
		try
		{
		ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		connected = (   conMgr.getActiveNetworkInfo() != null &&
				conMgr.getActiveNetworkInfo().isAvailable() &&
				conMgr.getActiveNetworkInfo().isConnected()   );
		}catch (Exception e)
		{
			return false;
		}
		
		return connected;

	}

	/**
	 * Open the Internet settings activity of the device.
	 *
	 * @param context The application context
	 */
	public static void openInternetSettingsActivity(final Context context)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setIcon(R.drawable.warning);
		alert.setTitle(R.string.no_internet_prompt);
		alert.setMessage(R.string.connect_to_internet_prompt);
		alert.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
			}
		});

		alert.show();
	}

	/**
	 * Turn On the device mobile data connection.
	 *
	 * @param context The application context
	 * @throws NoSuchMethodException the no such method exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	public static void turnOnDataConnection(Context context) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		ConnectivityManager dataManager;
		dataManager  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
		dataMtd.setAccessible(true);
		dataMtd.invoke(dataManager, true);   
	}
	
	/**
	 * Turn Off the device mobile data connection.
	 *
	 * @param context The application context
	 * @throws NoSuchMethodException the no such method exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	public static void turnOffDataConnection(Context context) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		ConnectivityManager dataManager;
		dataManager  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
		dataMtd.setAccessible(true);
		dataMtd.invoke(dataManager, false);   
	}
	
    public static boolean isAutoTimeEnabled(Context context){
		
    	if( App.getContext().getAppSettings().isUseNetworkProvidedTime()){
    		if(Build.VERSION.SDK_INT >= 17){
                return getAutoTimeStatusApi17AndMore(context) != 0;
    		}
            else {
                return getAutoTimeStatusApi16AndLess(context) != 0;
            }
    	}else{
    	  return true;
    	}
	}
	@TargetApi(17)
	public static int getAutoTimeStatusApi17AndMore(Context context){
		return android.provider.Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0) & android.provider.Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AUTO_TIME_ZONE, 0);
	}
	
	@TargetApi(16)
	public static int getAutoTimeStatusApi16AndLess(Context context){
		return android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) & android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.AUTO_TIME_ZONE, 0);
	}
	
	
	/**
	 * Open the Internet settings activity of the device.
	 *
	 * @param context The application context
	 */
	public static void openDateTimeSettingsActivity(final Context context)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setCancelable(false);
		alert.setIcon(R.drawable.warning);
		alert.setTitle(R.string.date_time_problem_title);
		alert.setMessage(R.string.date_time_problem_message);
		alert.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				context.startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
			}
		});

		alert.show();
	}
	
}
