package ngo.friendship.satellite.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.async.AsyncTask;
import ngo.friendship.satellite.interfaces.OnDownloadFileCompleteListener;

/**
 * Background Async Task to download file
 * */
public class DownloadFileTask extends AsyncTask<String, String, String> {

	/** The ctx. */
	Activity ctx;
	String url_link;
	String outPath;

	// Progress Dialog
	private ProgressDialog pDialog;
	ImageView my_image;
	// Progress dialog type (0 - for Horizontal progress bar)
	public static final int progress_bar_type = 0;

	//bnbjnj
   OnDownloadFileCompleteListener listener;
	
	public DownloadFileTask(Activity ctx, String url,String outPath) {
		this.ctx = ctx;
		this.url_link = url;
		this.outPath=outPath;
	}
	

	/**
	 * Before starting background thread Show Progress Bar Dialog
	 * */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(ctx);
		pDialog.setMessage(ctx. getResources().getString(R.string.download_file_wait));
		pDialog.setIndeterminate(false);
		pDialog.setMax(100);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.setCancelable(true);
		pDialog.show();

	}

	@Override
	protected String doInBackground(String s) throws Exception {
		try {
			URL url = new URL(url_link);
			URLConnection conection = url.openConnection();

//			String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
//			conection.setRequestProperty("User-Agent", USER_AGENT);
			conection.connect();
			// getting file length
			int lenghtOfFile = conection.getContentLength();

			// input stream to read file - with 8k buffer
			InputStream input = new BufferedInputStream(url.openStream(), 8192);


			// Output stream to write file
			OutputStream output = new FileOutputStream(outPath);

			byte data[] = new byte[1024];

			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				// publishing the progress....
				// After this onProgressUpdate will be called
				onProgressUpdate("" + (int) ((total * 100) / lenghtOfFile));

				// writing data to file
				output.write(data, 0, count);
			}

			// flushing output
			output.flush();

			// closing streams
			output.close();
			input.close();
			return outPath;

		} catch (Exception e) {
			Log.e("Error: ", e.getMessage());
		}

		return null;
	}



	/**
	 * Updating progress bar
	 * */
	protected void onProgressUpdate(String... progress) {
		// setting progress percentage
		pDialog.setProgress(Integer.parseInt(progress[0]));
	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	@Override
	protected void onPostExecute(String file_url) {
		pDialog.dismiss();
		if(listener != null)
			listener.OnDownloadFileCompleteListener(file_url);
	}

	@Override
	protected void onBackgroundError(Exception e) {

	}

	public void setOnDownloadFileCompleteListener(OnDownloadFileCompleteListener listener)
	{
		this.listener = listener;
	}


}