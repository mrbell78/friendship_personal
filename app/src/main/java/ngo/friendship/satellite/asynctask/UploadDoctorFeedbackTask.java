package ngo.friendship.satellite.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.async.AsyncTask;
import ngo.friendship.satellite.communication.APICommunication;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestAction;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.interfaces.OnInterviewUploadListener;
import ngo.friendship.satellite.jsonoperation.JSONCreateor;
import ngo.friendship.satellite.model.PatientInterviewDoctorFeedback;
import ngo.friendship.satellite.utility.Utility;

public class UploadDoctorFeedbackTask extends AsyncTask<Void, Void, String> {
	
	/** The selected interview lis. */
	ArrayList<PatientInterviewDoctorFeedback> patientInterviewDoctorFeedbacks;
	Context ctx;
	OnInterviewUploadListener listener;
	boolean flagForupdateFeedbackFromDetails;
	ProgressDialog dlog;

	public UploadDoctorFeedbackTask(Context ctx, ArrayList<PatientInterviewDoctorFeedback> patientInterviewDoctorFeedbacks)
	{
		this.patientInterviewDoctorFeedbacks = patientInterviewDoctorFeedbacks;
		this.ctx = ctx;
	}

	public UploadDoctorFeedbackTask(Context ctx, ArrayList<PatientInterviewDoctorFeedback> patientInterviewDoctorFeedbacks,boolean flagForupdateFeedbackFromDetails)
	{
		this.patientInterviewDoctorFeedbacks = patientInterviewDoctorFeedbacks;
		this.ctx = ctx;
		this.flagForupdateFeedbackFromDetails = flagForupdateFeedbackFromDetails;
	}




	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(flagForupdateFeedbackFromDetails){
			dlog = ProgressDialog.show(ctx, ctx.getResources().getString(R.string.uploading_data), ctx.getResources().getString(R.string.please_wait));
		}

	}

	@Override
	protected void onPostExecute(String report) {
		// TODO Auto-generated method stub
		super.onPostExecute(report);
		if(listener != null)
			listener.onInterviewUploadFinished(report);

	}

	@Override
	protected void onBackgroundError(Exception e) {

	}

	/**
	 * Sets the on interview upload listener.
	 *
	 * @param listener the new on interview upload listener
	 */
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
		for(int i=0;i<patientInterviewDoctorFeedbacks.size();i++)
		{
			PatientInterviewDoctorFeedback doctorFeedback = patientInterviewDoctorFeedbacks.get(i);
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			String requestData= null;
			ResponseData webResponseDataInfo = new ResponseData();
			try {

				/*
				 *  Prepare Param1
				 */
				JSONObject jParamObj = new JSONObject();
				jParamObj.put(KEY.INTERVIEW_ID,doctorFeedback.getInterviewId());

				Charset cs = Charset.forName(HTTP.UTF_8);
				entityBuilder.setCharset(cs);
				JSONObject jsonObject=new JSONObject();
				jsonObject.put(Column.DOC_FOLLOWUP_ID, doctorFeedback.getDocFollowupId());
				jsonObject.put(Column.INTERVIEW_ID, doctorFeedback.getInterviewId());
				jsonObject.put(Column.REF_CENTER_ID, doctorFeedback.getRefCenterId());
				jsonObject.put(Column.PRESCRIBED_MEDICINE, doctorFeedback.getPrescribedMedicine());
				jsonObject.put(Column.NEXT_FOLLOWUP_DATE, doctorFeedback.getNextFollowupDate());
				jsonObject.put(Column.DOCTOR_FINDINGS, doctorFeedback.getDoctorFindings());
				jsonObject.put(Column.MESSAGE_TO_FCM, doctorFeedback.getMessageToFCM());
				jsonObject.put(Column.IS_FEEDBACK_ON_TIME, doctorFeedback.getIsFeedbackOnTime());
				jsonObject.put(Column.FEEDBACK_SOURCE, doctorFeedback.getFeedbackSource());
				jsonObject.put(Column.INVES_ADVICE, doctorFeedback.getInvesAdvice());
				jsonObject.put(Column.INVES_RESULT, doctorFeedback.getInvesResult());
				jsonObject.put(Column.INVES_STATUS, doctorFeedback.getInvesStatus());
				jsonObject.put(Column.NOTIFICATION_STATUS, doctorFeedback.getNotificationStatus());
				jsonObject.put(Column.TRANS_REF, doctorFeedback.getTransRef());
				jsonObject.put(Column.QUESTION_ANSWER_JSON, doctorFeedback.getQuestionAnsJson());
				jsonObject.put(Column.QUESTION_ANSWER_JSON2, doctorFeedback.getQuestionAnsJson2());
				jsonObject.put(Column.UPDATE_BY, doctorFeedback.getUpdateBy());
				jsonObject.put(Column.UPDATE_ON, doctorFeedback.getUpdateOn());
				requestData = JSONCreateor.createRequestJson(ctx, RequestType.TRANSACTION, RequestName.UPDATE_DOCTOR_FEEDBACK,Constants.MODULE_BUNCH_PUSH ,RequestAction.UPDATE, jsonObject, jParamObj);
				byte byteArr []  = requestData.getBytes(HTTP.UTF_8);
				entityBuilder.addBinaryBody("data",  byteArr);


				webResponseDataInfo = APICommunication.makeWebRequest(ctx, entityBuilder, App.getContext().getTransectionAPI());

			}
			catch(UnsupportedEncodingException e){
			}
			catch (JSONException e) {
				Log.e("JSON_ERROR",e.getMessage());

			}

			if(webResponseDataInfo.getWebResponseStatusCode() == 200)
			{
				if(webResponseDataInfo.getResponseCode().equalsIgnoreCase("01"))
				{
					App.getContext().getDB().updatePatientInterviewDoctorFeedbackSent(doctorFeedback.getDocFollowupId(),1);
					String date= Utility.getDateFromMillisecond(doctorFeedback.getFeedbackDate(), Constants.DATE_FORMAT_YYYY_MM_DD);
					if(doctorFeedback.getBenefName()!=null && doctorFeedback.getInterviewName()!=null){
						uploadReport.append("<font color='black'>"+date+" "+doctorFeedback.getInterviewTime()+"-"+doctorFeedback.getBenefName()+"-"+doctorFeedback.getInterviewName()+"-</font>");
					}else{
						uploadReport.append("<font color='black'>"+date+" "+doctorFeedback.getInterviewTime()+"-</font>");
					}

					uploadReport.append("<font color='green'>"+ctx.getResources().getString(R.string.successfull)+"</font>");
					uploadReport.append("<br><br>");

				}
				else
				{
					App.getContext().getDB().updatePatientInterviewDoctorFeedbackSent(doctorFeedback.getDocFollowupId(),0);
					String date= Utility.getDateFromMillisecond(doctorFeedback.getFeedbackDate(), Constants.DATE_FORMAT_YYYY_MM_DD);
					if(doctorFeedback.getBenefName()!=null && doctorFeedback.getInterviewName()!=null){
						uploadReport.append("<font color='black'>"+date+" "+doctorFeedback.getInterviewTime()+"-"+doctorFeedback.getBenefName()+"-"+doctorFeedback.getInterviewName()+"-</font>");
					}else{
						uploadReport.append("<font color='black'>"+date+" "+doctorFeedback.getInterviewTime()+"-</font>");
					}

					uploadReport.append("<font color='red'>"+ctx.getResources().getString(R.string.unsuccessfull)+"</font>");
					uploadReport.append("<br>");
					uploadReport.append("<font color='red'>"+ webResponseDataInfo.getErrorCode()+" - "+ webResponseDataInfo.getErrorDesc()+"</font>");
					uploadReport.append("<br><br>");


				}
			}
			else
			{
				App.getContext().getDB().updatePatientInterviewDoctorFeedbackSent(doctorFeedback.getDocFollowupId(),0);
				String date= Utility.getDateFromMillisecond(doctorFeedback.getFeedbackDate(), Constants.DATE_FORMAT_YYYY_MM_DD);
				if(doctorFeedback.getBenefName()!=null && doctorFeedback.getInterviewName()!=null){
					uploadReport.append("<font color='black'>"+date+" "+doctorFeedback.getInterviewTime()+"-"+doctorFeedback.getBenefName()+"-"+doctorFeedback.getInterviewName()+"-</font>");
				}else{
					uploadReport.append("<font color='black'>"+date+" "+doctorFeedback.getInterviewTime()+"-</font>");
				}
				uploadReport.append("<font color='red'>"+ctx.getResources().getString(R.string.unsuccessfull)+"</font>");
				uploadReport.append("<br>");
				uploadReport.append("<font color='red'>"+ webResponseDataInfo.getErrorDesc()+"</font>");
				uploadReport.append("<br><br>");


			}


		}
		return uploadReport.toString();

	}







}
