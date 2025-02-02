package ngo.friendship.satellite.jsonoperation;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.model.AdjustmentMedicineInfo;
import ngo.friendship.satellite.model.AppVersionHistory;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionAnswer;
import ngo.friendship.satellite.model.QuestionBranch;
import ngo.friendship.satellite.model.QuestionList;
import ngo.friendship.satellite.model.ScheduleInfo;
import ngo.friendship.satellite.utility.Utility;

// TODO: Auto-generated Javadoc

/**
 * The Class CreateJson.
 */
public class JSONCreateor {

	/**
	 * Create the questionnaire JSON with user input.
	 *
	 * @param questionList is the interview question list. It contains all information to build a question like branching, option list, user input etc
	 * @return The JSON string which is build from questionList
	 * @throws JSONException the JSON exception
	 */
	public static String createQuestionnaireJson(QuestionList questionList) throws JSONException
	{
		JSONObject jDataObj = new JSONObject();
		jDataObj.put(KEY.QUESTIONNAIRE_ID, questionList.getQuestionnaireId());
		jDataObj.put(KEY.TITLE, questionList.getQuestionnaireTitle());
		jDataObj.put("NAME", questionList.getQuestionnaireName());
		
		
		JSONObject jQuestionnaireObj = new JSONObject();
		JSONObject jQuestionnaireDataObj = new JSONObject();
         
		jQuestionnaireDataObj.put(KEY.PARENT, questionList.getFirstQuestionKey());
		jQuestionnaireDataObj.put(KEY.QUESTIONNAIRE, jQuestionnaireObj);

		jDataObj.put("QUESTIONNAIRE_DATA", jQuestionnaireDataObj);
		
		ArrayList<Question> allQuestion = questionList.getQuestionList();

		for(int i=0;i<allQuestion.size();i++)
		{
			Question question = allQuestion.get(i);

			JSONObject jQuestionObj = new JSONObject();

			if(question.getDefaultValue() != null)
			{
				//// Default Value
				StringBuilder sbDefaultVal = new StringBuilder();
				for(int j=0;j<question.getDefaultValue().size();j++)
				{
					if(j>0)
						sbDefaultVal.append("|");
					sbDefaultVal.append(question.getDefaultValue().get(j));
				}
				jQuestionObj.put(KEY.DEFAULT_VAL, sbDefaultVal.toString());
			}


			jQuestionObj.put(KEY.QNAME, question.getName());
			jQuestionObj.put(KEY.HINT, question.getHint());
			jQuestionObj.put(KEY.CAPTION, question.getCaption());
			jQuestionObj.put(KEY.QTYPE, question.getType());
			jQuestionObj.put(KEY.REQUIRED, question.isRequired());
			jQuestionObj.put(KEY.READONLY, question.isReadonly());
			jQuestionObj.put(KEY.HIDDEN, question.isHidden());
			jQuestionObj.put(KEY.SAVABLE, question.isSavable());
			jQuestionObj.put(KEY.MULTI_LINE, question.isMultiLine());
			
			
			jQuestionObj.put(KEY.ADD_MEDICINE, question.isAddMedicine());
			jQuestionObj.put(KEY.SUGGESTION, question.isSuggestion());
			jQuestionObj.put(KEY.SUGGESTION_TYPE, question.getSuggestionType());
			
			jQuestionObj.put(KEY.SCALE, question.getScaleTo()+"");
			jQuestionObj.put(KEY.QUALITY, question.getQuality()+"");
			jQuestionObj.put(KEY.ADD_MEDICINE, question.isAddMedicine());
			
			
			// added by Mohammed Jubayer 10/11/2014
			if( question.getExpression()!=null)
				jQuestionObj.put(KEY.EXPRESSION, question.getExpression());
			/////
			if(question.getUserInput() != null)
			{
				StringBuilder sbUserInput = new StringBuilder();
				for(int j=0;j<question.getUserInput().size();j++)
				{
					if(j>0)
						sbUserInput.append("|");
					sbUserInput.append(question.getUserInput().get(j));
				}
				jQuestionObj.put(KEY.USER_INPUT, sbUserInput.toString());
			}

			if(question.getMediaType() != null && !question.getMediaType().equalsIgnoreCase(""))
				jQuestionObj.put(KEY.MEDIA_TYPE, question.getMediaType());

			if(question.getNumType() != null && !question.getNumType().equalsIgnoreCase(""))
				jQuestionObj.put(KEY.NUM_TYPE, question.getNumType());

			//////// Validations/////////////
			if(question.getValidationMsg() != null && !question.getValidationMsg().equalsIgnoreCase(""))
				jQuestionObj.put(KEY.VALIDATION_MSG, question.getValidationMsg());

			if(question.getValidationList() != null)
			{
				JSONArray jValidationArr = new JSONArray();

				for(int j=0;j<question.getValidationList().size();j++)
				{
					JSONObject jValidationObj = new JSONObject();
					jValidationObj.put(KEY.VALIDATION_TYPE, question.getValidationList().get(j).getValidationType());
					jValidationObj.put(KEY.VALUE, question.getValidationList().get(j).getValue());
                    if( question.getValidationList().get(j).getBaseDate()!=null){
                    	jValidationObj.put(KEY.VALIDATION_BASE_DATE, question.getValidationList().get(j).getBaseDate());
                    }
                    jValidationArr.put(jValidationObj);
				}
				jQuestionObj.put(KEY.VALIDATIONS, jValidationArr);
			}
			//////////////////////

			/////// Branch Item//////////
			if(question.getBranchItemList() != null)
			{
				JSONArray jBranchItemArr = new JSONArray();

				for(int j=0;j<question.getBranchItemList().size();j++)
					jBranchItemArr.put(question.getBranchItemList().get(j));

				jQuestionObj.put(KEY.BRANCH_ITEMS, jBranchItemArr);
			}
			///////////////

			///// Options////////////
			if(question.getOptionList() != null)
			{
				JSONArray jOptionArr = new JSONArray();

				for(int j=0;j<question.getOptionList().size();j++)
				{
					JSONObject jOptionObj = new JSONObject();
					jOptionObj.put(KEY.ID, Integer.toString(question.getOptionList().get(j).getId()));
					jOptionObj.put(KEY.VALUE, question.getOptionList().get(j).getValue());
					jOptionObj.put(KEY.CAPTION, question.getOptionList().get(j).getCaption());

					jOptionArr.put(jOptionObj);
				}

				jQuestionObj.put(KEY.OPTIONS, jOptionArr);
			}
			//////////////////////////

			/////////// Branches/////////////////
			if(question.getBranchList() != null)
			{
				JSONArray jBranchsArr = new JSONArray();

				for(int j=0;j<question.getBranchList().size();j++)
				{
					QuestionBranch qb = question.getBranchList().get(j);
					JSONObject jBranchesObj = new JSONObject();

					if(qb.getCalcValue() != null)
						jBranchesObj.put(KEY.CALCVAL, qb.getCalcValue());

					if(qb.getNextQuestionKey() != null)
						jBranchesObj.put(KEY.NEXT_Q, qb.getNextQuestionKey());

					if(qb.getRule() != null)
						jBranchesObj.put(KEY.RULE, qb.getRule());

					if(qb.getValue() != null)
						jBranchesObj.put(KEY.VALUE, qb.getValue());

					jBranchsArr.put(jBranchesObj);
				}

				jQuestionObj.put(KEY.BRANCHES, jBranchsArr);
			}
			///////////////////////////////////
			jQuestionnaireObj.put(question.getKey(), jQuestionObj);
		}
		//jDataObj.put(JSONKey.QUESTIONNAIRE, jQuestionnaireObj);
		return jDataObj.toString();
	}

	/**
	 * Create JSON which contain question keys and there respective answer. 
	 *
	 * @param ctx is the application context
	 * @param userCode is the FCM code
	 * @param password  is the FCM password
	 * @param beneficiaryCode is the beneficiary code
	 * @param startTimeMilli is the interview start time in millisecond
	 * @param endTimeMilli is the interview end time in millisecond
	 * @param questionnaireId is the Questionnaire ID
	 * @param longitude is the Longitude value where the interview taken place
	 * @param latitude is the Latitude value where the interview taken place
	 * @param questionAnswerList is the list of the question with answer
	 * @return JSON object created by provided data
	 * @throws JSONException the JSON exception
	 */
	public static JSONObject getQuestionAnswerJson(Context ctx, String userCode, String password, String beneficiaryCode, long startTimeMilli ,long endTimeMilli , int questionnaireId, double longitude, double latitude, HashMap<String, QuestionAnswer> questionAnswerList) throws JSONException
	{

		long duration = (endTimeMilli - startTimeMilli)/1000; // in second
		JSONObject jObj = new JSONObject();
		jObj.put(KEY.ID, questionnaireId);
		jObj.put("longitude", longitude);
		jObj.put("latitude", latitude);
		jObj.put("user_code", Utility.md5(userCode));
		jObj.put("user_pass", Utility.md5(password));
		jObj.put("start_time", startTimeMilli);
		jObj.put("end_time", endTimeMilli);
		jObj.put(KEY.DURATION, duration);
		jObj.put("b_code", beneficiaryCode);
		jObj.put(KEY.IMEI, Utility.getIMEInumber(ctx));
		jObj.put(KEY.OLD_IMEI, Utility.getOldIMEInumber(ctx));

		/*
		 * get question ans list from question answer map 
		 * sort question ans list 
		 * use  unsorted list when sorted list is empty  
		 */
		
		
		List<QuestionAnswer> qaArrList =new ArrayList<QuestionAnswer>();
		List<QuestionAnswer> qaArrListUnSorted =new ArrayList<QuestionAnswer>();
	    for (String key : questionAnswerList.keySet()) {
			qaArrList.add(questionAnswerList.get(key));
			qaArrListUnSorted.add(questionAnswerList.get(key));
		}
	    
		qaArrList = Utility.sortQuestionAnswerList(qaArrList);	
		if(qaArrList==null || qaArrList.size()==0){
			qaArrList=qaArrListUnSorted;
		}
		
		JSONArray jQestionArr = new JSONArray();
		for(QuestionAnswer qa : qaArrList)
		{
			Log.e("Question Name", qa.getQuestionKey());
			if(qa.isSavable() && !qa.isAdded() && qa.getAnswerList() != null)
			{    
				JSONObject jQuestionObj = new JSONObject();
				jQuestionObj.put(KEY.Q_KEY, qa.getQuestionKey());
				jQuestionObj.put(KEY.ANSWER, TextUtils.join("|",qa.getAnswerList()));	
		        jQestionArr.put(jQuestionObj);
				questionAnswerList.get("q"+qa.getQuestionKey()).setAdded(true);
				if(qa.getMediaType() != null) jQuestionObj.put(KEY.MEDIA_TYPE, qa.getMediaType());
			}

			//added by ashraf for try to fix bug at 21th june,2020
			else if(qa.isSavable() && qa.isAdded() && qa.getAnswerList() != null)
			{
				JSONObject jQuestionObj = new JSONObject();
				jQuestionObj.put(KEY.Q_KEY, qa.getQuestionKey());
				jQuestionObj.put(KEY.ANSWER, TextUtils.join("|",qa.getAnswerList()));
				jQestionArr.put(jQuestionObj);
				questionAnswerList.get("q"+qa.getQuestionKey()).setAdded(true);
				if(qa.getMediaType() != null) jQuestionObj.put(KEY.MEDIA_TYPE, qa.getMediaType());
			}

		}
		Log.e("JSON", jQestionArr.toString());
		jObj.put(KEY.QUESTIONS, jQestionArr);
		return jObj;
	}


	/**
	 * Creates JSON string which is used to make a web request.
	 *
	 * @param userCode is the FCM code
	 * @param password is the FCM password
	 * @param imei is the device IMEI
	 * @param requestType is the request type (e.g USER_GATE, USER_ACTIVITY, TRANSACTION, STOCK_INVENTORY etc)
	 * @param requestName is the request name (e.g LOGIN, PW_CHANGE, MY_DATA, FCM_PROFILE etc)
	 * @param requestAction is the action will be taken on data at server end (e.g INSERT, DELETE, UPDATE etc)
	 * @param data is the user data
	 * @param param1 is the additional parameter like priority, interview id etc.
	 * @return The created request JSON string
	 * @throws JSONException the JSON exception
	 */
	public static String createRequestJson(Context ctx, String requestType, String requestName,String moduleName, String requestAction, JSONObject data, JSONObject param1) throws JSONException
	{

		JSONObject jObj = new JSONObject();
		jObj.put(KEY.USER_CODE, Utility.md5(App.getContext().getUserInfo().getUserCode()));
		jObj.put(KEY.PASSWORD, Utility.md5(App.getContext().getUserInfo().getPassword()));
		jObj.put(KEY.IMEI, Utility.getIMEInumber(ctx));
		jObj.put(KEY.OLD_IMEI, Utility.getOldIMEInumber(ctx));
		jObj.put(Column.ORG_ID, App.getContext().getUserInfo().getOrgId());
		jObj.put(Column.ORG_CODE, App.getContext().getUserInfo().getOrgCode());
		jObj.put(KEY.REQUEST_TYPE, requestType);
		jObj.put(KEY.MODULE_NAME, moduleName);
		jObj.put(KEY.REQUEST_NAME, requestName);
		jObj.put(KEY.REQUEST_TIME, Constants.ddIMMIyyyyHHmmss.format(Calendar.getInstance().getTime()));

		if(requestAction != null)
			jObj.put(KEY.REQUEST_ACTION, requestAction);
		else
			jObj.put(KEY.REQUEST_ACTION, "");

		if(data != null)
		{
			jObj.put(KEY.DATA_LENGTH, data.toString().replace("\\", "").length());	
			jObj.put(KEY.DATA, data);	
		}
		else
		{
			jObj.put(KEY.DATA, new JSONObject());
			jObj.put(KEY.DATA_LENGTH, 0);	
		}
		jObj.put(KEY.LANG, App.getContext().getAppSettings().getLanguage());
		if(param1 != null)
			jObj.put(KEY.PARAM1, param1);
		else
			jObj.put(KEY.PARAM1, new JSONObject());

		return jObj.toString();
	}

	/**
	 * Create JSON data by medicine ID and required quantity for requisition request.
	 *
	 * @param medicineList The medicine list with required quantity
	 * @return Requisition request data JSON
	 * @throws JSONException the JSON exception
	 */
	public static JSONObject createMedicineRequisitionJSON(ArrayList<MedicineInfo> medicineList)
	{
		JSONObject jObj = new JSONObject();
		JSONArray jArr = new JSONArray();
		try {
			for(MedicineInfo medicineInfo : medicineList)
			{
				if(Utility.parseInt(medicineInfo.getRequiredQuantity())> 0)
				{
					JSONObject jMedObj = new JSONObject();
					jMedObj.put(Column.MEDICINE_ID, medicineInfo.getMedId());
					jMedObj.put(Column.QTY, Utility.parseInt(medicineInfo.getRequiredQuantity()));

					jArr.put(jMedObj);
				}
			}

			jObj.put("medicineList", jArr);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return jObj;
	}
	
	
	/**
	 * Create JSON data by AppVersionHistory.
	 *
	 * @param AppVersionHistory
	 * @return AppVersionHistory request data JSON
	 * @throws JSONException the JSON exception
	 */
	public static JSONObject createAppVersionHistoryJson(AppVersionHistory appVersionHistory) throws JSONException
	{
		JSONObject jObj = new JSONObject();
		jObj.put(KEY.VERSION_ID, appVersionHistory.getVersionId());
		jObj.put(KEY.INSTALL_FLAG,  appVersionHistory.getInstallFlag());
		jObj.put(KEY.INSTALL_DATE,  appVersionHistory.getInstallDate());
		jObj.put(KEY.OPEN_DATE,  appVersionHistory.getOpenDate());
		return jObj;
	}

	/**
	 * Create JSON data by medicine ID and required quantity for requisition request.
	 *
	 * @param medicineList The medicine list with required quantity
	 * @return Requisition request data JSON
	 * @throws JSONException the JSON exception
	 */
	public static JSONObject createMedicineSellJSON(ArrayList<MedicineInfo> medicineList) throws JSONException
	{
		JSONArray jArr = new JSONArray();

		for(MedicineInfo medicineInfo : medicineList)
		{
			if(Utility.parseInt(medicineInfo.getSoldQuantity()) > 0)
			{
				JSONObject jMedObj = new JSONObject();
				jMedObj.put(Column.MEDICINE_ID, medicineInfo.getMedId());
				jMedObj.put(Column.QTY, Utility.parseInt(medicineInfo.getSoldQuantity()));
				jMedObj.put(Column.CONSUMP_DATE, medicineInfo.getUpdateTime());
			    jArr.put(jMedObj);
			}
		}
		
		JSONObject jObj = new JSONObject();
		jObj.put("medicineList", jArr);
		return jObj;
	}


	/**
	 * Create JSON data by medicine ID and required quantity for requisition request.
	 *
	 * @param medicineList The medicine list with required quantity
	 * @return Requisition request data JSON
	 * @throws JSONException the JSON exception
	 */
	public static JSONObject createMedicineReceivcedJSON(ArrayList<MedicineInfo> medicineList ,long currentTimeInMillis) throws JSONException
	{
		JSONArray jArr = new JSONArray();

		for(MedicineInfo medicineInfo : medicineList)
		{
			if(medicineInfo.getQtyReceived()> 0)
			{
				JSONObject jMedObj = new JSONObject();
				jMedObj.put(Column.MEDICINE_ID, medicineInfo.getMedId());
				jMedObj.put(Column.QTY, medicineInfo.getQtyReceived());
				jMedObj.put(Column.RECEIVED_DATE,currentTimeInMillis);
				jArr.put(jMedObj);
			}
		}

		JSONObject jObj = new JSONObject();
		jObj.put("medicineList", jArr);
		return jObj;
	}


	public static JSONObject createMedicineAdjutmentRequestJSON(ArrayList<AdjustmentMedicineInfo> medicineList)
	{
		JSONArray jArr = new JSONArray();
        JSONObject jObj = new JSONObject();
        try{
			for(AdjustmentMedicineInfo medicineInfo : medicineList)
			{
				jArr.put(medicineInfo.toJson());
			}
		    jObj.put("medicineList", jArr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jObj;
	}
	

	// Mohammed Jubayer
	public static String createScheduleInfosJson(ArrayList<ScheduleInfo> scheduleList,String dateInputRule) throws JSONException
	{
		JSONArray jArr = new JSONArray();
		for(ScheduleInfo scheduleInfo : scheduleList)
		{
			JSONObject jSchedObj = new JSONObject();
			jSchedObj.put("SCHED_ID", scheduleInfo.getScheduleId());
			jSchedObj.put("SCHED_NAME", scheduleInfo.getScheduleName());
			jSchedObj.put("SCHED_DATE", scheduleInfo.getScheduleDate());
			jSchedObj.put("SCHED_DESC", scheduleInfo.getScheduleDesc());
			jSchedObj.put("SCHED_TYPE", scheduleInfo.getScheduleType());
			jSchedObj.put("REFERENCE_ID", scheduleInfo.getReferenceId());
			jSchedObj.put("DATE_INPUT_RULE", dateInputRule);
			jArr.put(jSchedObj);
		}

		JSONObject jObj = new JSONObject();
		jObj.put("schedList", jArr);
		return jObj.toString();
	}
}

