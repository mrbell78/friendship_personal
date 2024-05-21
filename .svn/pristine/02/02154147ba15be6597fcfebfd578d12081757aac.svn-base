package ngo.friendship.satellite.asynctask;

import android.content.Context;
import android.util.Log;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.async.AsyncTask;
import ngo.friendship.satellite.communication.APICommunication;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.DBTable;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestAction;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.interfaces.OnInterviewUploadListener;
import ngo.friendship.satellite.jsonoperation.JSONCreateor;
import ngo.friendship.satellite.model.SavedInterviewInfo;


public class UploadInterviewTask extends AsyncTask<Void, Void, String> {

	/** The selected interview lis. */
	ArrayList<SavedInterviewInfo> selectedInterviewLis;
	HashMap<String,String > filesMap=new HashMap<String, String>();
	/** The ctx. */
	Context ctx;

	/** The listener. */
	OnInterviewUploadListener listener;
	private String moduleType="";

	/**
	 * Instantiates a new upload interview task.
	 *
	 * @param ctx the ctx
	 * @param selectedInterviewLis the selected interview lis
	 */
	public UploadInterviewTask(Context ctx, ArrayList<SavedInterviewInfo> selectedInterviewLis,String moduleType)
	{
		this.selectedInterviewLis = selectedInterviewLis;
		this.ctx = ctx;
		this.moduleType=moduleType;
	}




	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String report) {
		// TODO Auto-generated method stub
		super.onPostExecute(report);
		if(listener != null) listener.onInterviewUploadFinished(report);

	}

	@Override
	protected void onBackgroundError(Exception e) {

	}


	public void setOnInterviewUploadListener(OnInterviewUploadListener listener)
	{
		this.listener = listener;
	}
	
//	 public void execute(){
//	    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//			}else {
//				super.execute();
//		    }
//  }

	@Override
	protected String doInBackground(Void params) throws Exception {
		StringBuilder uploadReport = new StringBuilder();
		filesMap=new HashMap<String, String>();
		for(int i=0;i<selectedInterviewLis.size();i++)
		{
			SavedInterviewInfo savedInterview = selectedInterviewLis.get(i);

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			String requestData= null;
			ResponseData responseData = new ResponseData();
			try {

				/*
				 *  Prepare Param1
				 */
				JSONObject jParamObj = new JSONObject();


				/*
				 * Check if it is a follow up questionnaire
				 */
				if(savedInterview.getScheduleInfo() != null)
				{
					jParamObj.put("SCHED_DATE", savedInterview.getScheduleInfo().getScheduleDate());
					jParamObj.put("ATTENDED_DATE", savedInterview.getScheduleInfo().getAttendedDate());
					jParamObj.put("PARENT_QUESTIONNAIRE_ID", savedInterview.getScheduleInfo().getQuestionnaireId());
					jParamObj.put("QUESTIONNAIRE_TYPE", "FOLLOW_UP");
				}

				// add beneficiary interview full json in the case registration
				//				if(savedInterview.getQuestionnarieName().startsWith("BENEFICIARY_REGISTRATION")){
				//					try{
				//						File regFile=new File(App.getContext().getQuestionnaireJSONDir(),savedInterview.getFileFullName());
				//						if(regFile.exists() && regFile.lastModified()>0){
				//							filesMap.put(regFile.getUserName(),regFile.getPath());
				//							entityBuilder.addBinaryBody("reg_form",regFile );
				//							jParamObj.put("FILE_KEY",regFile.getUserName());
				//							jParamObj.put("REG_FORM_PATH",savedInterview.getFileFullName());
				//						}
				//					}catch(Exception exception){
				//						exception.printStackTrace();
				//					}
				//				}


				Charset cs = Charset.forName(HTTP.UTF_8);
				entityBuilder.setCharset(cs);

				requestData = JSONCreateor.createRequestJson(ctx, RequestType.TRANSACTION, RequestName.CASE_ENTRY,moduleType, RequestAction.INSERT, new JSONObject(savedInterview.getQuestionAnswerJson()), jParamObj);
				byte byteArr []  = requestData.getBytes(HTTP.UTF_8);
				entityBuilder.addBinaryBody("data",  byteArr);


				/*
				 *  Check if there is any binary answer in the answer list
				 */
				if(savedInterview.getInputBinaryFilePathList() != null && !savedInterview.getInputBinaryFilePathList().equalsIgnoreCase(""))
				{
					String filePaths [] = savedInterview.getInputBinaryFilePathList().split("\\|");
					String sendFiles=App.getContext().getDB().getSendFiles(savedInterview.getTransRef());
					for(String filePath : filePaths)
					{
						File file=new File(filePath);
						if(file.exists() && !sendFiles.contains(file.getName()) ){
							filesMap.put(file.getName(),file.getPath());
							entityBuilder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());
						}
					}
				}

				responseData = APICommunication.makeWebRequest(ctx, entityBuilder, App.getContext().getTransectionAPI());

			}
			catch(UnsupportedEncodingException e){}
			catch (JSONException e) {}

			if(responseData.getWebResponseStatusCode() == 200)
			{
				try {
					JSONObject param=new JSONObject(responseData.getParam());
					if(param!=null && param.has("SAVED_FILES")){
						JSONArray savedFiles=param.getJSONArray("SAVED_FILES");
						for (int index = 0; index < savedFiles.length(); index++) {
							String fileName=savedFiles.getString(index);
							String filePath=filesMap.get(fileName);
							App.getContext().getDB().saveFileBank(fileName, filePath, savedInterview.getTransRef(), 1);
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}


				if(responseData.getResponseCode().equalsIgnoreCase("01"))
				{
					long affectedRow=0;



					try {
						JSONObject pram=new JSONObject(responseData.getParam());
						if(pram.has("AFFECTED_ROW")){
							affectedRow=pram.getLong("AFFECTED_ROW");
						}

						if(pram.has("OVER_CAPACITY_FLAG")){
							long overCapacityflag=pram.getLong("OVER_CAPACITY_FLAG");
							App.getContext().getDB().getCcsDao().updateOverCapacityFlag( savedInterview.getTransRef(), overCapacityflag);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}


					App.getContext().getDB().updateInterviewSent(savedInterview.getInterviewId(), savedInterview.getParentInterviewId(), "Y", affectedRow);

					uploadReport.append("<font color='black'>"+savedInterview.getDate()+" "+savedInterview.getTime()+"-"+savedInterview.getBenefName()+"-"+savedInterview.getQuestionnarieTitle()+"-</font>");
					uploadReport.append("<font color='green'>"+ctx.getResources().getString(R.string.successfull)+"</font>");
					uploadReport.append("<br><br>");


					App.getContext().getDB().updateMedicineConsumptionStatus(savedInterview.getInterviewId(), -1, "Y");
				}
				else
				{
					App.getContext().getDB().updateInterviewSent(savedInterview.getInterviewId(), savedInterview.getParentInterviewId(), "N",0);
					uploadReport.append("<font color='black'>"+savedInterview.getDate()+" "+savedInterview.getTime()+"-"+savedInterview.getBenefName()+"-"+savedInterview.getQuestionnarieTitle()+"-</font>");
					uploadReport.append("<font color='red'>"+ctx.getResources().getString(R.string.unsuccessfull)+"</font>");
					uploadReport.append("<br>");
					uploadReport.append("<font color='red'>"+ responseData.getErrorCode()+" - "+ responseData.getErrorDesc()+"</font>");
					uploadReport.append("<br><br>");
				}
			}
			else
			{
				App.getContext().getDB().updateInterviewSent(savedInterview.getInterviewId(), savedInterview.getParentInterviewId(), "N",0);
				uploadReport.append("<font color='black'>"+savedInterview.getDate()+" "+savedInterview.getTime()+"-"+savedInterview.getBenefName()+"-"+savedInterview.getQuestionnarieTitle()+"-</font>");
				uploadReport.append("<font color='red'>"+ctx.getResources().getString(R.string.unsuccessfull)+"</font>");
				uploadReport.append("<br>");
				uploadReport.append("<font color='red'>"+ responseData.getErrorDesc()+"</font>");
				uploadReport.append("<br><br>");
			}

			if(responseData.getWebResponseStatusCode() == 200 && responseData.getResponseCode().equalsIgnoreCase("01"))
			{
				if(responseData.getParam() != null && !responseData.getParam().equalsIgnoreCase(""))
				{
					try {
						JSONObject jResParamObj = new JSONObject(responseData.getParam());
						Log.e("Param", jResParamObj.toString());
						if(jResParamObj.has("BENEF_NAME_LOCAL"))
						{
							String benefLocalName = jResParamObj.getString("BENEF_NAME_LOCAL");
							App.getContext().getDB().updateBenefLocalName(savedInterview.getBeneficiaryCode(), benefLocalName);
						}

						if(jResParamObj.has("GUARDIAN_NAME_LOCAL"))
						{
							String guardianLocalName = jResParamObj.getString("GUARDIAN_NAME_LOCAL");
							App.getContext().getDB().updateGuardianLocalName(savedInterview.getBeneficiaryCode(), guardianLocalName);
						}

						/*
						 *  Update data version
						 */
						if(jResParamObj.has(KEY.VERSION_NO_BENEFICIARY))
						{
							App.getContext().getDB().updateDataVersion(DBTable.BENEFICIARY, 1,1,jResParamObj,KEY.VERSION_NO_BENEFICIARY);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return uploadReport.toString();
	}

}
