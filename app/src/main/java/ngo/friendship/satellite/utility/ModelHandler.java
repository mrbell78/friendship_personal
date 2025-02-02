package ngo.friendship.satellite.utility;


import android.content.Context;

import org.json.JSONArray;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.MyData;

public class ModelHandler 
{
	private static ModelHandler instance = null;
	private Context context;
	private ModelHandler() {}
	public static ModelHandler getInstance(Context context) {
		if(instance == null)instance = new ModelHandler();
		instance.context=context;
		return instance;
	}

	public  void handleMydata(MyData myData){
		

		if(myData.getUserInfo()!=null)App.getContext().getDB().saveUserInfo(myData.getUserInfo() ,myData.getVersionNos());
		App.getContext().loadCommonResource(context);
		if(myData.getFcmComfiguration() != null)saveFcmConfigration(myData);
		

		if(myData.getHouseholdList()!=null)App.getContext().getDB().saveHouseholds(myData.getHouseholdList(),myData.getVersionNos());
		if(myData.getBeneficiaryList()!=null) saveBeneficiaryList(myData);
		if(myData.getQuestionnaireCategoryList()!=null)App.getContext().getDB().saveQuestionnaireCategoryList(myData.getQuestionnaireCategoryList(),myData.getVersionNos());
		if(myData.getQuestionnaireList()!=null)App.getContext().getDB().saveQuestionnaireList(context,myData.getQuestionnaireList(),myData.getVersionNos());
		if(myData.getTextRefList()!=null)App.getContext().getDB().saveTextRef(myData.getTextRefList(),myData.getVersionNos());
		if(myData.getGrowthMeasurementParams()!=null)App.getContext().getDB().saveGrowthMeasurementParam(myData.getGrowthMeasurementParams(),myData.getVersionNos());	
		
		if(myData.getCourtyardMeetingTopicMonthJsonArray()!=null)App.getContext().getDB().saveCourtyardMeetingTopicMonth(myData.getCourtyardMeetingTopicMonthJsonArray(),myData.getVersionNos());
		if(myData.getTopicInfoJsonArray()!=null)App.getContext().getDB().saveTopicInfo(myData.getTopicInfoJsonArray(),myData.getVersionNos());
		if(myData.getCourtyardMeetingTarget()!=null)App.getContext().getDB().saveCourtyardMeetingTarget(myData.getCourtyardMeetingTarget(), myData.getVersionNos());
		if(myData.getCourtyardMeeting()!=null)App.getContext().getDB().saveCourtyardMeeting(myData.getCourtyardMeeting());
		
		
		if(myData.getDiagnosisInfos()!=null)App.getContext().getDB().saveDignosisInfo(myData.getDiagnosisInfos(),myData.getVersionNos());
		if(myData.getMedicineList()!=null)App.getContext().getDB().saveMedicineList(myData.getMedicineList(),myData.getVersionNos());
		
		if(myData.getReferralCenterCategory()!=null)App.getContext().getDB().saveReferralCenterCategory(myData.getReferralCenterCategory(),myData.getVersionNos());
		if(myData.getReferraCenterList()!=null)App.getContext().getDB().saveReferralCenterList(myData.getReferraCenterList(),myData.getVersionNos());
		
		
		
		if(myData.getPatientInterviewMasters()!=null){
			App.getContext().getDB().savePatientInterviewMasterList(myData.getPatientInterviewMasters());
		}

		if(myData.getEventInfos()!=null)App.getContext().getDB().saveEventInfoList(myData.getEventInfos(),myData.getVersionNos());	

			
		if(myData.getCcsStatusList()!=null)App.getContext().getDB().getCcsDao().saveCCSStatus(myData.getCcsStatusList(),myData.getVersionNos());
		if(myData.getCcsEligibleJsonArry()!=null )App.getContext().getDB().getCcsDao().saveCCSEligible(myData.getCcsEligibleJsonArry(),myData.getVersionNos());
		if(myData.getCcsTreatmentJsonArry()!=null )App.getContext().getDB().getCcsDao().saveCCSTreatment(myData.getCcsTreatmentJsonArry(),myData.getVersionNos());
		
		if(myData.getImmunizationList()!=null)App.getContext().getDB().getImmunizationDao().saveImmunizationList(myData.getImmunizationList(),myData.getVersionNos());
		if(myData.getImmunizableBeneficiaries()!=null)App.getContext().getDB().getImmunizationDao().saveImmunizableBeneficiaryList(myData.getImmunizableBeneficiaries(),myData.getVersionNos());	
		if(myData.getImmunizationTargets()!=null)App.getContext().getDB().getImmunizationDao().saveImmunizationTargetList(myData.getImmunizationTargets(),myData.getVersionNos());	
		if(myData.getImmunizationServices()!=null)App.getContext().getDB().getImmunizationDao().saveImmunizationServiceList(myData.getImmunizationServices(),myData.getVersionNos());	
		if(myData.getImmunizationFollowups()!=null)App.getContext().getDB().getImmunizationDao().saveImmunizationFollowupList(myData.getImmunizationFollowups(),myData.getVersionNos());	
		
        if(myData.getMaternalCareInfos()!=null)App.getContext().getDB().getMaternalDao().saveMaternalCareInfoList(myData.getMaternalCareInfos(),myData.getVersionNos());
		if(myData.getMaternalInfos()!=null) App.getContext().getDB().getMaternalDao().saveMaternalInfos(myData.getMaternalInfos());
		if(myData.getMaternalAbortions()!=null) App.getContext().getDB().getMaternalDao().saveMaternalAbortions(myData.getMaternalAbortions());
		if(myData.getMaternalBabyInfos()!=null)App.getContext().getDB().getMaternalDao().saveMaternalBabyInfos(myData.getMaternalBabyInfos());
		if(myData.getMaternalServices()!=null)App.getContext().getDB().getMaternalDao().saveMaternalServices(myData.getMaternalServices());
		if(myData.getMaternalDeliveries()!=null)App.getContext().getDB().getMaternalDao().saveMaternalDeliveries(myData.getMaternalDeliveries());
		if (myData.getCoupleRegistration() != null) {
			App.getContext().getDB().saveCoupleRegistrationList(myData.getCoupleRegistration(), myData.getVersionNos());
		}
		if(myData.getDeathRegistration()!=null)App.getContext().getDB().saveDeathRegistration(myData.getDeathRegistration());
		if(myData.getQuestionnaireBroadCategory()!=null)App.getContext().getDB().saveQuestionnaireBroadCategory(myData.getQuestionnaireBroadCategory(), myData.getVersionNos());
		if(myData.getQuestionnaireServiceCategory()!=null)App.getContext().getDB().saveQuestionnaireServiceCategory(myData.getQuestionnaireServiceCategory(),myData.getVersionNos());
		if(myData.getReportAsst()!=null)App.getContext().getDB().saveReportAsst(myData.getReportAsst(), myData.getVersionNos());
		if(myData.getSuggestionText()!=null)App.getContext().getDB().saveSuggestionText(myData.getSuggestionText(),myData.getVersionNos());
        if(myData.getPatientInterviewDoctorFeedbacks()!=null){
			App.getContext().getDB().savePatientInterviewDoctorFeedbacks(myData.getPatientInterviewDoctorFeedbacks(),myData.getVersionNos());
		}
		if(myData.getScheduleList()!=null)App.getContext().getDB().saveScheduleList(myData.getScheduleList(),myData.getVersionNos());


        if(myData.getNotificationMasters()!=null)App.getContext().getDB().saveNotification(myData.getNotificationMasters(),myData.getVersionNos());

		if(myData.getUserInfo()!=null)App.getContext().getDB().saveUserInfoList(myData.getUserInfoList() ,myData.getVersionNos(),myData.getUserInfo().getUserId());
		if(myData.getLocationList()!=null)App.getContext().getDB().saveParamadicWiseLocationList(myData.getLocationList() ,myData.getVersionNos());
		if(myData.getMedicineConsumptionList()!=null)App.getContext().getDB().saveConsumptionList(myData.getMedicineConsumptionList(), App.getContext().getUserInfo().getUserId());
		if(myData.getMedicineAdjustmentList()!=null)App.getContext().getDB().saveAdjustmentList(myData.getMedicineAdjustmentList(), App.getContext().getUserInfo().getUserId());
		if(myData.getMedicineReceiveList()!=null)App.getContext().getDB().saveMedicineReceivedList(myData.getMedicineReceiveList(), App.getContext().getUserInfo().getUserId());
		if(myData.getSatelliteSessionModelArrayList()!=null)App.getContext().getDB().saveSessionList(myData.getSatelliteSessionModelArrayList() ,myData.getVersionNos());
	}

	public  void handleMydataSync(MyData myData){
		App.getContext().loadCommonResource(context);
		if(myData.getFcmComfiguration() != null)saveFcmConfigration(myData);


		if(myData.getHouseholdList()!=null)App.getContext().getDB().saveHouseholds(myData.getHouseholdList(),myData.getVersionNos());
		if(myData.getBeneficiaryList()!=null) saveBeneficiaryList(myData);
		if(myData.getQuestionnaireCategoryList()!=null)App.getContext().getDB().saveQuestionnaireCategoryList(myData.getQuestionnaireCategoryList(),myData.getVersionNos());
		if(myData.getQuestionnaireList()!=null)App.getContext().getDB().saveQuestionnaireList(context,myData.getQuestionnaireList(),myData.getVersionNos());
		if(myData.getTextRefList()!=null)App.getContext().getDB().saveTextRef(myData.getTextRefList(),myData.getVersionNos());
		if(myData.getGrowthMeasurementParams()!=null)App.getContext().getDB().saveGrowthMeasurementParam(myData.getGrowthMeasurementParams(),myData.getVersionNos());

		if(myData.getCourtyardMeetingTopicMonthJsonArray()!=null)App.getContext().getDB().saveCourtyardMeetingTopicMonth(myData.getCourtyardMeetingTopicMonthJsonArray(),myData.getVersionNos());
		if(myData.getTopicInfoJsonArray()!=null)App.getContext().getDB().saveTopicInfo(myData.getTopicInfoJsonArray(),myData.getVersionNos());
		if(myData.getCourtyardMeetingTarget()!=null)App.getContext().getDB().saveCourtyardMeetingTarget(myData.getCourtyardMeetingTarget(), myData.getVersionNos());
		if(myData.getCourtyardMeeting()!=null)App.getContext().getDB().saveCourtyardMeeting(myData.getCourtyardMeeting());


		if(myData.getDiagnosisInfos()!=null)App.getContext().getDB().saveDignosisInfo(myData.getDiagnosisInfos(),myData.getVersionNos());
		if(myData.getMedicineList()!=null)App.getContext().getDB().saveMedicineList(myData.getMedicineList(),myData.getVersionNos());

		if(myData.getReferralCenterCategory()!=null)App.getContext().getDB().saveReferralCenterCategory(myData.getReferralCenterCategory(),myData.getVersionNos());
		if(myData.getReferraCenterList()!=null)App.getContext().getDB().saveReferralCenterList(myData.getReferraCenterList(),myData.getVersionNos());



		if(myData.getPatientInterviewMasters()!=null){
			App.getContext().getDB().savePatientInterviewMasterList(myData.getPatientInterviewMasters());
		}

		if(myData.getEventInfos()!=null)App.getContext().getDB().saveEventInfoList(myData.getEventInfos(),myData.getVersionNos());


		if(myData.getCcsStatusList()!=null)App.getContext().getDB().getCcsDao().saveCCSStatus(myData.getCcsStatusList(),myData.getVersionNos());
		if(myData.getCcsEligibleJsonArry()!=null )App.getContext().getDB().getCcsDao().saveCCSEligible(myData.getCcsEligibleJsonArry(),myData.getVersionNos());
		if(myData.getCcsTreatmentJsonArry()!=null )App.getContext().getDB().getCcsDao().saveCCSTreatment(myData.getCcsTreatmentJsonArry(),myData.getVersionNos());

		if(myData.getImmunizationList()!=null)App.getContext().getDB().getImmunizationDao().saveImmunizationList(myData.getImmunizationList(),myData.getVersionNos());
		if(myData.getImmunizableBeneficiaries()!=null)App.getContext().getDB().getImmunizationDao().saveImmunizableBeneficiaryList(myData.getImmunizableBeneficiaries(),myData.getVersionNos());
		if(myData.getImmunizationTargets()!=null)App.getContext().getDB().getImmunizationDao().saveImmunizationTargetList(myData.getImmunizationTargets(),myData.getVersionNos());
		if(myData.getImmunizationServices()!=null)App.getContext().getDB().getImmunizationDao().saveImmunizationServiceList(myData.getImmunizationServices(),myData.getVersionNos());
		if(myData.getImmunizationFollowups()!=null)App.getContext().getDB().getImmunizationDao().saveImmunizationFollowupList(myData.getImmunizationFollowups(),myData.getVersionNos());

		if(myData.getMaternalCareInfos()!=null)App.getContext().getDB().getMaternalDao().saveMaternalCareInfoList(myData.getMaternalCareInfos(),myData.getVersionNos());
		if(myData.getMaternalInfos()!=null) App.getContext().getDB().getMaternalDao().saveMaternalInfos(myData.getMaternalInfos());
		if(myData.getMaternalAbortions()!=null) App.getContext().getDB().getMaternalDao().saveMaternalAbortions(myData.getMaternalAbortions());
		if(myData.getMaternalBabyInfos()!=null)App.getContext().getDB().getMaternalDao().saveMaternalBabyInfos(myData.getMaternalBabyInfos());
		if(myData.getMaternalServices()!=null)App.getContext().getDB().getMaternalDao().saveMaternalServices(myData.getMaternalServices());
		if(myData.getMaternalDeliveries()!=null)App.getContext().getDB().getMaternalDao().saveMaternalDeliveries(myData.getMaternalDeliveries());
		if (myData.getCoupleRegistration() != null) {
			App.getContext().getDB().saveCoupleRegistrationList(myData.getCoupleRegistration(), myData.getVersionNos());
		}
		if(myData.getDeathRegistration()!=null)App.getContext().getDB().saveDeathRegistration(myData.getDeathRegistration());
		if(myData.getQuestionnaireBroadCategory()!=null)App.getContext().getDB().saveQuestionnaireBroadCategory(myData.getQuestionnaireBroadCategory(), myData.getVersionNos());
		if(myData.getQuestionnaireServiceCategory()!=null)App.getContext().getDB().saveQuestionnaireServiceCategory(myData.getQuestionnaireServiceCategory(),myData.getVersionNos());
		if(myData.getReportAsst()!=null)App.getContext().getDB().saveReportAsst(myData.getReportAsst(), myData.getVersionNos());
		if(myData.getSuggestionText()!=null)App.getContext().getDB().saveSuggestionText(myData.getSuggestionText(),myData.getVersionNos());
		if(myData.getPatientInterviewDoctorFeedbacks()!=null){
			App.getContext().getDB().savePatientInterviewDoctorFeedbacks(myData.getPatientInterviewDoctorFeedbacks(),myData.getVersionNos());
		}
		if(myData.getScheduleList()!=null)App.getContext().getDB().saveScheduleList(myData.getScheduleList(),myData.getVersionNos());


		if(myData.getNotificationMasters()!=null)App.getContext().getDB().saveNotification(myData.getNotificationMasters(),myData.getVersionNos());

		if(myData.getLocationList()!=null)App.getContext().getDB().saveParamadicWiseLocationList(myData.getLocationList() ,myData.getVersionNos());
		if(myData.getMedicineConsumptionList()!=null)App.getContext().getDB().saveConsumptionList(myData.getMedicineConsumptionList(), App.getContext().getUserInfo().getUserId());
		if(myData.getMedicineAdjustmentList()!=null)App.getContext().getDB().saveAdjustmentList(myData.getMedicineAdjustmentList(), App.getContext().getUserInfo().getUserId());
		if(myData.getMedicineReceiveList()!=null)App.getContext().getDB().saveMedicineReceivedList(myData.getMedicineReceiveList(), App.getContext().getUserInfo().getUserId());
		if(myData.getSatelliteSessionModelArrayList()!=null)App.getContext().getDB().saveSessionList(myData.getSatelliteSessionModelArrayList() ,myData.getVersionNos());
	}
	
	private void saveBeneficiaryList(MyData myData) {
		App.getContext().getDB().saveBeneficiaryList(myData.getBeneficiaryList(),myData.getVersionNos());
		Utility.downloadBeneficiaryImage(context, myData.getBeneficiaryList());
	 //	Utility.downloadBeneficiaryRegForm(context, myData.getBeneficiaryList());
	}

	private void saveQuestionnaireList(MyData myData) {
		App.getContext().getDB().saveQuestionnaireList(context,myData.getQuestionnaireList(),myData.getVersionNos());
		if(myData.getQuestionnaireList().size()>0 && myData.getQuestionnaireList().contains(Constants.QUESTIONNAIRE_IMAGE_FLAG))
		{
			Utility.downloadBeneficiaryImage(context, myData.getBeneficiaryList());
		}

		//	Utility.downloadBeneficiaryRegForm(context, myData.getBeneficiaryList());
	}
	
	private void saveFcmConfigration(MyData myData) {
		try {
			JSONArray configArry=new JSONArray(myData.getFcmComfiguration());
			if(configArry==null || configArry.length()<1){ return;}
			

			int value =Integer.parseInt(JSONParser.getFcmConfigValue(configArry,"CCS","automatic_followup_interval"));
			AppPreference.putInt(context, KEY.CCS_AUTOMETIC_FCM_FOLLOWUP_INTERVAL_VALUE,value);
			App.getContext().getAppSettings().setCcsAutometicFCMFollowupInterval(value);
			
		    value =Integer.parseInt(JSONParser.getFcmConfigValue(configArry,"CCS","number_of_maximum_followup"));
			AppPreference.putInt(context, KEY.CCS_NUMBER_OF_MAXIMUM_FOLLOWUP_VALUE,value);
			App.getContext().getAppSettings().setCcsNumberOfMaximumFCMFollowup(value);
			
			
		   value =Integer.parseInt(JSONParser.getFcmConfigValue(configArry,"EPI","min_age"));
		   AppPreference.putInt(context, KEY.EPI_MIN_AGE,value);
		   App.getContext().getAppSettings().setEpiMinAge(value);
		   
		   value =Integer.parseInt(JSONParser.getFcmConfigValue(configArry,"EPI","max_age"));
		   AppPreference.putInt(context, KEY.EPI_MAX_AGE,value);
		   App.getContext().getAppSettings().setEpiMaxAge(value);
		   
		   value =Integer.parseInt(JSONParser.getFcmConfigValue(configArry,"TT","min_age"));
		   AppPreference.putInt(context, KEY.TT_MIN_AGE,value);
		   App.getContext().getAppSettings().setTtMinAge(value);
		   
		   value =Integer.parseInt(JSONParser.getFcmConfigValue(configArry,"TT","max_age"));
		   AppPreference.putInt(context, KEY.TT_MAX_AGE,value);
		   App.getContext().getAppSettings().setTtMaxAge(value);
		   
		   value =Integer.parseInt(JSONParser.getFcmConfigValue(configArry,"SCHEDULE","before_day"));
		   AppPreference.putInt(context, KEY.SCHEDULE_DAY_BEFORE_TODAY,value);
		   App.getContext().getAppSettings().setScheduleDayBeforToday(value);
		   
		   value =Integer.parseInt(JSONParser.getFcmConfigValue(configArry,"SCHEDULE","after_day"));
		   AppPreference.putInt(context, KEY.SCHEDULE_DAY_AFTER_TODAY,value);
		   App.getContext().getAppSettings().setScheduleDayAfterToday(value);
		   
		   
		   
		   value =Integer.parseInt(JSONParser.getFcmConfigValue(configArry,"FOLLOWUP","after_day"));
		   AppPreference.putInt(context, KEY.FOLLOWUP_DAY_AFTER_TODAY,value);
		   App.getContext().getAppSettings().setFollowUpDayAfterToday(value);
		   
		   value =Integer.parseInt(JSONParser.getFcmConfigValue(configArry,"FOLLOWUP","before_day"));
		   AppPreference.putInt(context, KEY.FOLLOWUP_DAY_BEFORE_TODAY,value);
		   App.getContext().getAppSettings().setFollowUpDayBeforToday(value);
		   
		   
		   String typ=JSONParser.getFcmConfigValue(configArry,"FOLLOWUP","type");
		   AppPreference.putString(context, KEY.FOLLOWUP_TYPE,typ);
		   App.getContext().getAppSettings().setFollowUpType(typ);
		   
		   
		   String isImageShow =JSONParser.getFcmConfigValue(configArry,"IMAGE","is_image_show");
		   AppPreference.putString(context, KEY.IS_IMAGE_SHOW,isImageShow);
		   App.getContext().getAppSettings().setIsImageShow(isImageShow);
		  
		   
		   int immuDateGap =Utility.parseInt(JSONParser.getFcmConfigValue(configArry,"Immunization_list_date_gap","delay_day"));
		   AppPreference.putInt(context, KEY.IMMUNIZATION_MISSING_GAP_DATE,immuDateGap);
		   App.getContext().getAppSettings().setImmunizationMissGapDate(immuDateGap);
		   
		   AppPreference.putString(context, KEY.FCM_CONFIGURATION,myData.getFcmComfiguration());
		   App.getContext().getAppSettings().setFcmConfigration(myData.getFcmComfiguration());


			// Update application settings 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
