package ngo.friendship.satellite.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;

import java.io.File;
import java.util.Set;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.constants.KEY;


public class AppPreference {
    public static final String LIVE="live";
    public static final String DEVS="devs";
    public static final String DEMO="demo";
    public static final String TESTING="testing";
    public static final String COMMON="common";



    public static Object getValue(Context context,String key,Object value,String... prefName){
        String pName;
        if(prefName!=null && prefName.length==1){
            pName=prefName[0];
        }else{
            pName=getString(context, KEY.APPMODE,LIVE,COMMON);
        }
        Log.e("GET PREF VAL" ,"PREF_NAME:"+pName+" KEY:"+key);
        SharedPreferences prefs = context.getSharedPreferences(pName, Context.MODE_PRIVATE);
        if(value instanceof  String){
            return prefs.getString(key, (String) value);
        } else if(value instanceof  Integer){
            return prefs.getInt(key, (Integer)value);
        }else if(value instanceof  Long){
            return prefs.getLong(key, (Long)value);
        }else if(value instanceof  Float){
            return prefs.getFloat(key, (Float) value);
        }else if(value instanceof  Boolean){
            return prefs.getBoolean(key,(Boolean) value);
        }else if(value instanceof Set<?>){
            return prefs.getStringSet(key, (Set<String>) value);
        }else {
            return  prefs.getString(key, (String) value);
        }
    }


    private static void setValue(Context context,String key,Object value,String... prefName){
        String pName;
        if(prefName!=null && prefName.length==1){
            pName=prefName[0];
        }else{
            pName=getString(context, KEY.APPMODE,LIVE,COMMON);
        }

        Log.e("PUT PREF VAL" ,"PREF_NAME:"+pName+" KEY:"+key);
        SharedPreferences pref = context.getSharedPreferences(pName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if(value instanceof  String){
            editor.putString(key, (String) value);
        } else if(value instanceof  Integer){
            editor.putInt(key, (Integer)value);
        }else if(value instanceof  Long){
            editor.putLong(key, (Long)value);
        }else if(value instanceof  Float){
            editor.putFloat(key, (Float) value);
        }else if(value instanceof  Boolean){
            editor.putBoolean(key,(Boolean) value);
        }else if(value instanceof Set<?>){
            editor.putStringSet(key, (Set<String>) value);
        }else {
            editor.putString(key, (String) value);
        }
        editor.commit(); // commit changes
    }



    public static String getString(Context context,String key ,String value,String... prefName ){
        Object val=getValue(context,key,value,prefName);
        return val==null?null:(String)val;
    }

    public static int getInt(Context context,String key ,int value,String... prefName ){
        return (int)getValue(context,key,value,prefName);
    }

    public static float getFloat(Context context,String key ,float value,String... prefName ){
        return (float)getValue(context,key,value,prefName);
    }

    public static long getLong(Context context,String key ,long value,String... prefName ){
        return (long)getValue(context,key,value,prefName);
    }

    public static boolean getBoolean(Context context,String key ,boolean value,String... prefName ){
        return (boolean)getValue(context,key,value,prefName);
    }

    public static Set<String> getStringSet(Context context,String key ,Set<String>  value,String... prefName ){
        Object val=getValue(context,key,value,prefName);
        return val==null?null:(Set<String>)val;
    }


    public static void putString(Context context,String key ,String value ,String... prefName ){
        setValue(context,key,value,prefName);
    }

    public static void putInt(Context context,String key ,int value ,String... prefName ){
        setValue(context,key,value,prefName);
    }


    public static void putFloat(Context context,String key ,float value ,String... prefName ){
        setValue(context,key,value,prefName);
    }


    public static void putLong(Context context,String key ,long value ,String... prefName ){
        setValue(context,key,value,prefName);
    }


    public static void putBoolean(Context context,String key ,boolean value ,String... prefName ){
        setValue(context,key,value,prefName);
    }


    public static void putStringSet(Context context,String key ,Set<String>  value  ,String... prefName ){
        setValue(context,key,value,prefName);
    }


    public static void remove(Context context,String key,String... prefName){
        String pName;
        if(prefName!=null && prefName.length==1){
            pName=prefName[0];
        }else{
            pName=getString(context, KEY.APPMODE,LIVE,COMMON);
        }
        SharedPreferences pref = context.getSharedPreferences(pName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit(); // commit changes
    }

    public static void clear(Context context,String prefName){
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit(); // commit changes
    }

    public static boolean contains(Context context,String key,String... prefName){
        String pName;
        if(prefName!=null && prefName.length==1){
            pName=prefName[0];
        }else{
            pName=getString(context, KEY.APPMODE,LIVE,COMMON);
        }


        SharedPreferences pref = context.getSharedPreferences(pName, Context.MODE_PRIVATE);
        return pref.contains(key);
    }

    public static String  getAppMode(Context context){
        return getString(context, KEY.APPMODE,AppPreference.LIVE,AppPreference.COMMON);
    }

    public static void setAppModeAndReload(Context context,String appMode){
        putString(context, KEY.APPMODE,appMode,AppPreference.COMMON);
        App.getContext().setAppSettings(null);
        App.destroyApp();
        App.getContext().loadApplicationData(context);
    }


    public static void changeAppMode(final Context context , String appMode) {
        if (AppPreference.LIVE.equals(appMode)) {
            AppPreference.setAppModeAndReload(context, AppPreference.TESTING);
            App.getContext().setAppSettings(null);
            App.destroyApp();
        } else {
            App.getContext().deleteDir(new File(App.getContext().getAppDataDir(context)));
            AppPreference.clear(context, AppPreference.TESTING);
            AppPreference.setAppModeAndReload(context, AppPreference.LIVE);
            App.getContext().setAppSettings(null);
            App.destroyApp();
        }
    }

    public static JSONArray  getFCMConfigration(Context context){
        JSONArray configArry = new JSONArray();
        try {

            String confiData= AppPreference.getString(context, KEY.FCM_CONFIGURATION,"[]");
             configArry = new JSONArray(confiData);
        }catch (Exception e){

        }
        return configArry;
    }
}
