package ngo.friendship.satellite.asynctask;

import android.content.Context;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.async.AsyncTask;
import ngo.friendship.satellite.communication.APICommunication;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnDataSentListener;
import ngo.friendship.satellite.jsonoperation.JSONCreateor;
import ngo.friendship.satellite.model.MedicineSellInfo;
import ngo.friendship.satellite.utility.Utility;


public class SendMedicineSellListTask extends AsyncTask<Void, Void, ResponseData> {

	/** The medicine sell list. */
	ArrayList<MedicineSellInfo> medicineSellList;
	

	Context ctx;
	OnDataSentListener   onDataSentListerner;
	private String moduleType="";

	public SendMedicineSellListTask(Context context,ArrayList<MedicineSellInfo> medicineSellList, String moduleType)
	{
		this.medicineSellList = medicineSellList;
		this.ctx = context;
		this.moduleType=moduleType;
	}
	

	@Override
	protected void onPostExecute(ResponseData result) {
		super.onPostExecute(result);
		if(onDataSentListerner != null)
			onDataSentListerner.onDataSendingFinished(result);
	}

	@Override
	protected void onBackgroundError(Exception e) {

	}


	public void setOnDataSentListener(OnDataSentListener onDataSentListerner)
	{
		this.onDataSentListerner = onDataSentListerner;
	}

	private ResponseData makeWebRequest(MultipartEntityBuilder entityBuilder, String baseUrl)
	{
		ResponseData webResponseDataInfo = new ResponseData();
		try {

			webResponseDataInfo = APICommunication.uploadMultipartData(ctx,baseUrl + App.getContext().getTransectionAPI(), entityBuilder);

		} catch (ClientProtocolException e) {

			webResponseDataInfo = Utility.createErrorWebResponse(ctx, R.string.network_error, e);
		} catch (MhealthException e) {
			webResponseDataInfo = Utility.createErrorWebResponse(ctx, R.string.error_json_parse_exception, e);
		}
		catch (HttpHostConnectException e) {
			webResponseDataInfo = Utility.createErrorWebResponse(ctx, R.string.exception_host_connection, e);
			e.printStackTrace();
		}
		catch (ConnectTimeoutException e) {
			webResponseDataInfo = Utility.createErrorWebResponse(ctx, R.string.exception_time_out, e);
			e.printStackTrace();
		}
		catch (ConnectException e)
		{
			webResponseDataInfo = Utility.createErrorWebResponse(ctx, R.string.network_error,e);
			e.printStackTrace();
		}
		catch (Exception e) {
			webResponseDataInfo = Utility.createErrorWebResponse(ctx, R.string.error_exception, e);
			e.printStackTrace();
		}

		return webResponseDataInfo;
	}
	
//	 public void execute(){
//	    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//			}else {
//				super.execute();
//		    }
//  }

	@Override
	protected ResponseData doInBackground(Void unused) throws Exception {
		boolean isError = false;
		String msg="";
		long count=0;
		ResponseData webResponseDataInfo =null;
		for(MedicineSellInfo msi : medicineSellList)
		{
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			try {
				JSONObject medicineSellData = JSONCreateor.createMedicineSellJSON(msi.getMedicineList());
				String requestJson = JSONCreateor.createRequestJson(ctx, RequestType.TRANSACTION, RequestName.DIRECT_MEDICINE_SELL,moduleType, null, medicineSellData, null);
				Charset cs = Charset.forName("UTF-8");
				entityBuilder.setCharset(cs);
				entityBuilder.addBinaryBody("data",  requestJson.getBytes(HTTP.UTF_8 ));
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			webResponseDataInfo = APICommunication.makeWebRequest(ctx, entityBuilder, App.getContext().getTransectionAPI());
			if((webResponseDataInfo != null && webResponseDataInfo.getWebResponseStatusCode() == 200 && webResponseDataInfo.getResponseCode().equalsIgnoreCase("01")))
			{
				count++;
				App.getContext().getDB().updateMedicineConsumptionStatus(-1, msi.getConsumptionId(), "Y");
			}
			else
			{
				isError = true;
			}
		}

		if(isError)
			return null ;
		else
			return webResponseDataInfo;
	}

}
