package ngo.friendship.satellite.communication;

import android.content.Context;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Calendar;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.utility.WebServerCommunication;

public class APICommunication {
	
	 public static ResponseData uploadMultipartData(Context context, String url, MultipartEntityBuilder entityBuilder) throws IOException, MhealthException
	{
		ResponseData responseData = WebServerCommunication.uploadMultipartData(context,url, entityBuilder);

		if(responseData.getWebResponseStatusCode() == 200)
		{   
			App.LAST_SUCCESS_REQUEST_TIME=Calendar.getInstance().getTimeInMillis();	
			App.STATUS_COLOR=context.getResources().getColor(R.color.online);
			
			ResponseData webResponseDataInfo = JSONParser.parseMultipartUploadResponse(responseData.getData());
			Utility.checkUserActive(context, webResponseDataInfo);
			webResponseDataInfo.setWebResponseStatusCode(responseData.getWebResponseStatusCode());
			return webResponseDataInfo;
		}else{
			App.STATUS_COLOR=context.getResources().getColor(R.color.offline);
		}

		return responseData;
	}


	public static ResponseData makeWebRequest(Context context, MultipartEntityBuilder entityBuilder, String apiUrl)
	{

		boolean isAlternativeApi = false;
		String apiBase = App.getContext().getGateWayBasePath();
		if(apiBase == null)
		{
			apiBase = App.getContext().getAlternativeGateWayBasePath();
			isAlternativeApi = true;
		}

		ResponseData webResponseDataInfo = callApi(context, entityBuilder, apiBase, apiUrl);

		if(!isAlternativeApi && (webResponseDataInfo == null || webResponseDataInfo.getResponseCode() == null|| webResponseDataInfo.getResponseCode().equalsIgnoreCase("00")))
		{
			apiBase = App.getContext().getAlternativeGateWayBasePath();
			if(apiBase != null)
			{
				webResponseDataInfo = callApi(context, entityBuilder, apiBase, apiUrl);
			}
		}
		return webResponseDataInfo;
	}

	private static ResponseData callApi(Context context, MultipartEntityBuilder entityBuilder, String baseUrl, String apiUrl)
	{
		ResponseData webResponseDataInfo = new ResponseData();
		try
		{
			//webResponseInfo.setWebResponseStatusCode(200);
			webResponseDataInfo = APICommunication.uploadMultipartData(context,baseUrl+ "/usergate", entityBuilder);
		} catch (ClientProtocolException e) {

			webResponseDataInfo = Utility.createErrorWebResponse(context, R.string.network_error, e);
		} catch (MhealthException e) {
			webResponseDataInfo = Utility.createErrorWebResponse(context, R.string.error_json_parse_exception, e);
		}
		catch (HttpHostConnectException e) {
			webResponseDataInfo = Utility.createErrorWebResponse(context, R.string.exception_host_connection, e);
			e.printStackTrace();
		}
		catch (ConnectTimeoutException e) {
			webResponseDataInfo = Utility.createErrorWebResponse(context, R.string.exception_time_out, e);
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
			webResponseDataInfo = Utility.createErrorWebResponse(context, R.string.exception_time_out, e);
			e.printStackTrace();
		}
		catch (ConnectException e)
		{
			webResponseDataInfo = Utility.createErrorWebResponse(context, R.string.network_error,e);
			e.printStackTrace();
		}
		catch (Exception e) {
			webResponseDataInfo = Utility.createErrorWebResponse(context, R.string.error_exception, e);
			e.printStackTrace();
		}

		return webResponseDataInfo;
	}
}
