package ngo.friendship.satellite.asynctask;

import android.content.Context;
import android.util.Log;


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
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnDataSentListener;
import ngo.friendship.satellite.jsonoperation.JSONCreateor;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.RequisitionInfo;
import ngo.friendship.satellite.utility.Utility;

// TODO: Auto-generated Javadoc

/**
 * The Class SendMedicineReceivedListTask.
 */
public class SendMedicineReceivedListTask extends AsyncTask<Void, Void, ResponseData> {

	/** The ctx. */
	Context ctx;
	
	/** The on data sent listerner. */
	OnDataSentListener onDataSentListerner;

	/** The unsent medicine received list. */
	ArrayList<RequisitionInfo> unsentMedicineReceivedList;

	/**
	 * Instantiates a new send medicine received list task.
	 *
	 * @param ctx the ctx
	 * @param unsentMedicineReceivedList the unsent medicine received list
	 */
	
	String moduleName="";
	public SendMedicineReceivedListTask(Context ctx, ArrayList<RequisitionInfo> unsentMedicineReceivedList,String moduleName) {
		this.ctx = ctx;
		this.unsentMedicineReceivedList = unsentMedicineReceivedList;
		this.moduleName=moduleName; 
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

	/**
	 * Sets the on data sent listener.
	 *
	 * @param onDataSentListerner the new on data sent listener
	 */
	public void setOnDataSentListener(OnDataSentListener onDataSentListerner)
	{
		this.onDataSentListerner = onDataSentListerner;
	}

	/**
	 * Make web request.
	 *
	 * @param entityBuilder the entity builder
	 * @param baseUrl the base url
	 * @return the web response info
	 */
	private ResponseData makeWebRequest(MultipartEntityBuilder entityBuilder, String baseUrl)
	{
		ResponseData webResponseDataInfo = new ResponseData();
		try {


			webResponseDataInfo = APICommunication.uploadMultipartData(ctx,
					baseUrl + App.getContext().getStockInventoryAPI(), 
					entityBuilder);


		} catch (ClientProtocolException e) {

			webResponseDataInfo = Utility.createErrorWebResponse(ctx, R.string.network_error, e);
			e.printStackTrace();
		} catch (MhealthException e) {
			webResponseDataInfo = Utility.createErrorWebResponse(ctx, R.string.error_json_parse_exception, e);
			e.printStackTrace();
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
	protected ResponseData doInBackground(Void params) throws Exception {
		ResponseData webResponseDataInfo = null;
		boolean isError = false;
		for(RequisitionInfo medReqInfo: unsentMedicineReceivedList)
		{
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			try
			{
				/*
				 * Create Param JSON object
				 */
				JSONObject jParamObj = new JSONObject();
				jParamObj.put(Column.REQ_NO, Long.toString(medReqInfo.getRequisitionNo()));
				jParamObj.put(Column.RECEIVED_DATE,Utility.parseLong(medReqInfo.getReceiveDate()));

				/*
				 *  Create JSON with medicine which are received
				 *
				 *
				 */
				ArrayList<MedicineInfo> medInfo= medReqInfo.getMedicineList();
				JSONObject medicineReceiveData = JSONCreateor.createMedicineReceivcedJSON(medInfo,Utility.parseLong(medReqInfo.getReceiveDate()));

				/*
				 *  Create Web request JSON with required data
				 */
				String requestJson = JSONCreateor.createRequestJson(ctx, RequestType.STOCK_INVENTORY, RequestName.MEDICINE_RECEIVE,moduleName, null, medicineReceiveData, jParamObj);

				Log.e("Receive", requestJson);
				Charset cs = Charset.forName("UTF-8");
				entityBuilder.setCharset(cs);
				entityBuilder.addBinaryBody("data",  requestJson.getBytes(HTTP.UTF_8 ));
			}catch (Exception e){
				e.printStackTrace();
			}


			webResponseDataInfo = APICommunication.makeWebRequest(ctx, entityBuilder, App.getContext().getStockInventoryAPI());

			if((webResponseDataInfo != null && webResponseDataInfo.getWebResponseStatusCode() == 200 && webResponseDataInfo.getResponseCode().equalsIgnoreCase("01")))
			{
				ArrayList<MedicineInfo> medList;
				try {
					medList = JSONParser.parseMedicineReceivedJSON(webResponseDataInfo.getData());
					App.getContext().getDB().saveMedicineReceived(medList, 0, medReqInfo.getRequisitionNo(),"Y", medReqInfo.getReceivedId(),0);

				} catch (MhealthException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				Log.e("sdfsdfd", webResponseDataInfo.getErrorDesc());
				isError = true;
			}
		}

		if(isError)
			return null;
		else
			return webResponseDataInfo;
	}
}
