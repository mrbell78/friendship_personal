package ngo.friendship.satellite.storage;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class UpdateDB {

public String getSchema(Context context, int oldVersion,int newVersion){
	 String schema=" "+getStringFromFile(context,"db_patch/db_start.sql");
	 if(oldVersion<0) {
		 schema=schema+" "+getStringFromFile(context,"db_patch/db_patch_0.sql");
	 }
	 schema=schema+" "+getStringFromFile(context,"db_patch/db_end.sql");
	 return schema;
 }
 
 public String getStringFromFile(Context context,String path){
     try {
    	 InputStream input = context.getAssets().open(path);
    	 int size = input.available();
		 byte[] buffer = new byte[size];
		 input.read(buffer);
		 input.close();
		 String text = new String(buffer);
		 return text;
     } catch (IOException e) {
         e.printStackTrace();
     }
     return "";
 }
}