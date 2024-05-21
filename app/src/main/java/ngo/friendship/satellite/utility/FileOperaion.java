package ngo.friendship.satellite.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import ngo.friendship.satellite.App;

// TODO: Auto-generated Javadoc

/**
 * The Class FileOperaion.
 */
public class FileOperaion {

	/**
	 * Store the questionnaire JSON along with the user input into file .
	 *
	 * @param data The content of the file
	 * @param fileName The name of the file to which tha data will be saved
	 */
	public static void saveQuestionAnswerJson(Context context ,String data, String fileName)
	{
		File file = new File(App.getContext().getQuestionnaireJSONDir(context), fileName);

		try {
			FileOutputStream f = new FileOutputStream(file);
			PrintWriter pw = new PrintWriter(f);
			pw.print(data);
			pw.flush();
			pw.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}   
		
		
	}

	public static String decodeAndSaveImageFile(String fromPath,String topath, long scaleTo,long quality)
	{

		File f = new File(fromPath);

		final int IMAGE_MAX_SIZE = (int)scaleTo;
		Bitmap b = null;
		Bitmap tempbMap;
		try {
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			int scale = 1;
			if (IMAGE_MAX_SIZE>0 && (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE)) {
				scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			}

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			b = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

		} catch (FileNotFoundException e) {
		}

		try {

			FileOutputStream out = new FileOutputStream(topath);
			b.compress(Bitmap.CompressFormat.JPEG,(int)quality, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return topath;
	}

	/**
	 * Scale image to imageMaxSize and return the Bitmap object of that image.
	 *
	 * @param imageFilePath The local path of image file
	 * @param imageMaxSize Maximum width, height of the image
	 * @return The Bitmap object of the image
	 */
	public static  Bitmap decodeImageFile(String imageFilePath, int imageMaxSize){

		File f = new File(imageFilePath);

		final int IMAGE_MAX_SIZE = imageMaxSize;
		Bitmap b = null;
		Bitmap tempbMap;
		try {
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			int scale = 1;
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			}

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			b = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

		} catch (FileNotFoundException e) {
		}
		return b;
	}

	/**
	 * Get all file name in a directory.
	 *
	 * @param dir The file object of directory in which the system will search for files
	 * @return The File name list
	 */
	public static ArrayList<String> getFileList(File dir)
	{
		ArrayList<String> fileNameList = new ArrayList<String>();
		File [] fileList = dir.listFiles();

		for(File file : fileList)
		{
			if(file.isFile())
				fileNameList.add(file.getName());
		}
		return fileNameList;
	}

	/**
	 * Read and return the contents of a file.
	 *
	 * @param file The file object which will be read
	 * @return The file contents
	 */
	public static String readFromFile(File file) {
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		BufferedReader in = null;

		try {
			in = new BufferedReader(new FileReader(file));
			while ((line = in.readLine()) != null) 
				stringBuilder.append(line);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		if(in != null)
		{
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return stringBuilder.toString();
	}
	
	/**
	 *  
	 * Write image to SD card.
	 *
	 * @param filePath The file path
	 * @param fileData The file content
	 */
//	public static void writeImageToFile(String filePath, String fileData) {
//		if(fileData == null || fileData.equalsIgnoreCase(""))
//			return;
//		
//		Log.e("writeImageToFile File data length :", fileData.length()+"");
//		Log.e("writeImageToFile File path : ", filePath);
////		Log.e("writeImageToFile File data : ", fileData);
//		InputStream stream;
//		try {
//			stream = new ByteArrayInputStream(Base64.decode(fileData));
//			Bitmap bitmap = BitmapFactory.decodeStream(stream);
//			File file = new File(filePath);
//			FileOutputStream out = new FileOutputStream(file);
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//		    bitmap.recycle();
//		    bitmap=null;
//			out.flush();
//			out.close();
//		}catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//	}
	
	
	public static void writeImageToFileNew(String filePath, String base64ImageData) {
		FileOutputStream out=null;
		try {
		    if (base64ImageData != null) {
			   out = new FileOutputStream(new File(filePath));
		       byte[] decodedString = android.util.Base64.decode(base64ImageData, android.util.Base64.DEFAULT);
		       out.write(decodedString);                        
		       out.flush();
		       out.close();             
		    }
	
		} catch (Exception e) {
	       e.printStackTrace();
		} finally {
		    if (out != null) {
		    	try {
					out.flush();
				    out.close();  
				} catch (IOException e) {
					e.printStackTrace();
				}
			   
		    	out = null;
		    }
		}

	}
	
	

	
	public static void saveFile(String filePath, String fileData) {
		if(fileData == null || fileData.equalsIgnoreCase(""))
			return;
		InputStream stream;
		try {
			File file = new File(filePath);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(Base64.decode(fileData));
			fos.close();
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	 public static String readLogs() {  
	        StringBuilder logBuilder = new StringBuilder();  
	        try {  
	        	 Process process = Runtime.getRuntime().exec("logcat -d  org.friendship.mhealth:E");  
		          BufferedReader bufferedReader = new BufferedReader(  
	                    new InputStreamReader(process.getInputStream()));  
	  
	            String line;  
	            while ((line = bufferedReader.readLine()) != null) {  
	                logBuilder.append(line + "\n");  
	            }  
	        } catch (IOException e) {  
	        }  
	        return logBuilder.toString();  
	    }  
	 
	// write text to file
    public static void writeData(String data ,String filePath) {
			// add-write text into file
			try {
				File file = new File(filePath);
				FileOutputStream fos = new FileOutputStream(filePath);
				OutputStreamWriter outputWriter=new OutputStreamWriter(fos);
				outputWriter.write(data);
				outputWriter.close();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
    
    
    public static boolean isExist(String path){
    	try {
    		if(path!=null && path.trim().length()>3 && path.contains(".") && !path.equals("null")&& !path.equals("NULL") && new File(path).exists() ){
    			return true;
    		}
		} catch (Exception e) {
			
		}
		 return false;
	}
    
    
    public static File getFile(String Dir ,final String startWith) {
        File dir = new File(Dir);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(startWith);
            }
        });
        if(files!=null && files.length==1)
         return  files[0];
        else return null;
    }
    
}
