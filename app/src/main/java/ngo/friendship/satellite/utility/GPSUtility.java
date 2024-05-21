package ngo.friendship.satellite.utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import ngo.friendship.satellite.R;

// TODO: Auto-generated Javadoc

/**
 * The Class GPSUtility.
 */
public class GPSUtility {
    private static final String permissionFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String permissionCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;

    public static final int REQUEST_CODE_LOCATION = 2001;

    /**
     * Checks if is GPS enable.
     * <p>
     * //     * @param context the context
     *
     * @return true, if is GPS enable
     */
    public static boolean isGPSEnable(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Open the Internet settings activity of the device.
     *
     * @param context The application context
     */
    public static void openGPSSettingsActivity(final Context context) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.no_GPS_prompt);
        alert.setMessage(R.string.enable_GPS_prompt);
        alert.setPositiveButton(R.string.btn_yes, new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        alert.setNegativeButton(R.string.btn_no, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub

            }
        });

        alert.show();
    }



    public static void requestPermissions(Activity activity) {
        Boolean contextProvider = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionFineLocation);

        if (contextProvider) {
            Utility.displayMessage(null, activity.getApplicationContext(), activity.getApplicationContext().getResources().getString(R.string.permission_denied));

        }

        permissionRequest(activity);
    }

    public static Boolean validatePermissionsLocation(Activity activity) {
        final Boolean fineLocationAvailable = ActivityCompat.checkSelfPermission(activity.getApplicationContext(), permissionFineLocation) == PackageManager.PERMISSION_GRANTED;
        final Boolean coarseLocationAvailable = ActivityCompat.checkSelfPermission(activity.getApplicationContext(), permissionCoarseLocation) == PackageManager.PERMISSION_GRANTED;

        return fineLocationAvailable && coarseLocationAvailable;
    }

    public static void permissionRequest(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{permissionFineLocation, permissionCoarseLocation}, REQUEST_CODE_LOCATION);
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation, long maxTimeDealy, long maxAccuracy) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > maxTimeDealy;
        boolean isSignificantlyOlder = timeDelta < -maxTimeDealy;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > maxAccuracy;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * This uses the ‘haversine’ formula to calculate the great-circle distance between
     * two points – that is, the shortest distance over the earth’s surface –
     * giving an ‘as-the-crow-flies’ distance between the points
     * (ignoring any hills they fly over, of course!). Haversine formula:
     * a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
     * c = 2 ⋅ atan2( √a, √(1−a) )
     * d = R ⋅ c
     * where	φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km);
     * note that angles need to be in radians to pass to trig functions!
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return double  distance  as meter
     */

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.4; // Radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);  // deg2rad below
        double dLon = Math.toRadians(lon2 - lon1);
        double a
                = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = earthRadius * c; // Distance in km
        return d * 1000;
    }
}
