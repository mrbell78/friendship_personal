package ngo.friendship.satellite.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.model.maternal.MaternalAbortion;
import ngo.friendship.satellite.model.maternal.MaternalBabyInfo;
import ngo.friendship.satellite.model.maternal.MaternalCareInfo;
import ngo.friendship.satellite.model.maternal.MaternalDelivery;
import ngo.friendship.satellite.model.maternal.MaternalInfo;
import ngo.friendship.satellite.model.maternal.MaternalService;

public class MyData implements Serializable{
	

	/** The referra center list. */
	private JSONArray referraCenterList;

	/** The questionnaire list. */
	private ArrayList<QuestionnaireInfo> questionnaireList;

	/** The household list. */
	private ArrayList<Household> householdList;

	/** The fcm info. */
	private UserInfo userInfo;
	private ArrayList<UserInfo> userInfoList;
	private ArrayList<LocationModel> locationList;

	/** The questionnaire category list. */
	private ArrayList<QuestionnaireCategoryInfo> questionnaireCategoryList;

	/** The beneficiary list. */
	private ArrayList<Beneficiary> beneficiaryList;

	/** The medicine list. */
	private ArrayList<MedicineInfo> medicineList;

	/** The version list. */
	private ArrayList<DataVersionInfo> versionList;

	/** The schedule list. */
	private ArrayList<ScheduleInfo> scheduleList;


	
	
	private JSONArray fcmProfileJson;
	
	private JSONArray ccsEligibleJsonArry;
	private JSONArray ccsTreatmentJsonArry;
	private JSONArray topicInfoJsonArray   ;
	private JSONArray courtyardMeetingTopicMonthJsonArray;
	private JSONObject versionNos;
	

	/** The immunization list. */
	private ArrayList<ImmunizationInfo> immunizationList;
	private ArrayList<ImmunizableBeneficiary> immunizableBeneficiaries;
	private ArrayList<ImmunizationFollowup> immunizationFollowups;
	private ArrayList<ImmunizationService> immunizationServices;
	private ArrayList<ImmunizationTarget> immunizationTargets;

	
	private ArrayList<MaternalInfo> maternalInfos ;
	private ArrayList<MaternalBabyInfo> maternalBabyInfos;
	private ArrayList<MaternalService> maternalServices;
	private ArrayList<MaternalDelivery> maternalDeliveries;
	private ArrayList<MaternalCareInfo> maternalCareInfos;
	private ArrayList<MaternalAbortion> maternalAbortions ;
	
	private ArrayList<EventInfo> eventInfos;
	
	ArrayList<PatientInterviewMaster> patientInterviewMasters;
	ArrayList<NotificationItem> notificationItems;

	private ArrayList<DiagnosisInfo> diagnosisInfos;
	private AppVersionHistory appVersionHistory;
	private JSONArray growthMeasurementParams;
	
	private JSONArray reportAsst;
	private JSONArray questionnaireServiceCategory;
	private JSONArray questionnaireBroadCategory;
	
	private JSONArray courtyardMeetingTarget;
	private JSONArray courtyardMeeting;
	
	private String fcmComfiguration;
	
	private JSONArray referralCenterCategory;
	private JSONArray deathRegistration;
	
	private JSONArray suggestionText;
	private JSONArray medicineStock;

	private JSONArray coupleRegistration;
	
	ArrayList<PatientInterviewDoctorFeedback> patientInterviewDoctorFeedbacks;

	ArrayList<MedicineConsumptionInfoModel> medicineConsumptionList = new ArrayList<>();
	ArrayList<SatelliteSessionModel> satelliteSessionModelArrayList = new ArrayList<>();

	ArrayList<MedicineAdjustmentInfoModel> medicineAdjustmentList = new ArrayList<>();
	ArrayList<MedicineReceiveModel> medicineReceiveList = new ArrayList<>();


	public ArrayList<SatelliteSessionModel> getSatelliteSessionModelArrayList() {
		return satelliteSessionModelArrayList;
	}

	public void setSatelliteSessionModelArrayList(ArrayList<SatelliteSessionModel> satelliteSessionModelArrayList) {
		this.satelliteSessionModelArrayList = satelliteSessionModelArrayList;
	}

	public ArrayList<MedicineReceiveModel> getMedicineReceiveList() {
		return medicineReceiveList;
	}

	public void setMedicineReceiveList(ArrayList<MedicineReceiveModel> medicineReceiveList) {
		this.medicineReceiveList = medicineReceiveList;
	}

	public ArrayList<MedicineConsumptionInfoModel> getMedicineConsumptionList() {
		return medicineConsumptionList;
	}

	public void setMedicineConsumptionList(ArrayList<MedicineConsumptionInfoModel> medicineConsumptionList) {
		this.medicineConsumptionList = medicineConsumptionList;
	}

	public ArrayList<MedicineAdjustmentInfoModel> getMedicineAdjustmentList() {
		return medicineAdjustmentList;
	}

	public void setMedicineAdjustmentList(ArrayList<MedicineAdjustmentInfoModel> medicineAdjustmentList) {
		this.medicineAdjustmentList = medicineAdjustmentList;
	}

	public JSONArray getCoupleRegistration() {
		return coupleRegistration;
	}

	public void setCoupleRegistration(JSONArray coupleRegistration) {
		this.coupleRegistration = coupleRegistration;
	}

	public ArrayList<LocationModel> getLocationList() {
		return locationList;
	}

	public void setLocationList(ArrayList<LocationModel> locationList) {
		this.locationList = locationList;
	}

	public ArrayList<UserInfo> getUserInfoList() {
		return userInfoList;
	}

	public void setUserInfoList(ArrayList<UserInfo> userInfoList) {
		this.userInfoList = userInfoList;
	}

	public ArrayList<NotificationItem> getNotificationMasters() {
		return notificationItems;
	}

	public void setNotificationMasters(ArrayList<NotificationItem> notificationItems) {
		this.notificationItems = notificationItems;
	}


	public ArrayList<PatientInterviewDoctorFeedback> getPatientInterviewDoctorFeedbacks() {
		return patientInterviewDoctorFeedbacks;
	}

	public void setPatientInterviewDoctorFeedbacks(ArrayList<PatientInterviewDoctorFeedback> patientInterviewDoctorFeedbacks) {
		this.patientInterviewDoctorFeedbacks = patientInterviewDoctorFeedbacks;
	}
	public void setFcmComfiguration(String fcmComfiguration) {
		this.fcmComfiguration = fcmComfiguration;
	}
	
	public String getFcmComfiguration() {
		return fcmComfiguration;
	}

	/**
	 * Sets the immunization list.
	 *
	 * @param immunizationList the new immunization list
	 */
	public void setImmunizationList(ArrayList<ImmunizationInfo> immunizationList) {
		this.immunizationList = immunizationList;
	}

	/**
	 * Gets the immunization list.
	 *
	 * @return the immunization list
	 */
	public ArrayList<ImmunizationInfo> getImmunizationList() {
		return immunizationList;
	}


	/** The ccs status list. */
	private ArrayList<CCSStatus> ccsStatusList;

	/** The reason list. */
	private ArrayList<TextRef> textRefList;

	/**
	 * Sets the ccs status list.
	 *
	 * @param ccsStatusList the new ccs status list
	 */
	public void setCcsStatusList(ArrayList<CCSStatus> ccsStatusList) {
		this.ccsStatusList = ccsStatusList;
	}

	/**
	 * Gets the ccs status list.
	 *
	 * @return the ccs status list
	 */
	public ArrayList<CCSStatus> getCcsStatusList() {
		return ccsStatusList;
	}

	public void setTextRefList(ArrayList<TextRef> textRefList) {
		this.textRefList = textRefList;
	}


	public ArrayList<TextRef> getTextRefList() {
		return textRefList;
	}

	/**
	 * Sets the schedule list.
	 *
	 * @param scheduleList the new schedule list
	 */
	public void setScheduleList(ArrayList<ScheduleInfo> scheduleList) {
		this.scheduleList = scheduleList;
	}

	/**
	 * Gets the schedule list.
	 *
	 * @return the schedule list
	 */
	public ArrayList<ScheduleInfo> getScheduleList() {
		return scheduleList;
	}

	/**
	 * Sets the version list.
	 *
	 * @param versionList the new version list
	 */
	public void setVersionList(ArrayList<DataVersionInfo> versionList) {
		this.versionList = versionList;
	}

	/**
	 * Gets the version list.
	 *
	 * @return the version list
	 */
	public ArrayList<DataVersionInfo> getVersionList() {
		return versionList;
	}

	/** The response info. */
	ResponseData responseDataInfo;

	/**
	 * Sets the response info.
	 *
	 * @param responseDataInfo the new response info
	 */
	public void setResponseInfo(ResponseData responseDataInfo) {
		this.responseDataInfo = responseDataInfo;
	}

	/**
	 * Gets the response info.
	 *
	 * @return the response info
	 */
	public ResponseData getResponseInfo() {
		return responseDataInfo;
	}


	
	public void setReferraCenterList(JSONArray referraCenterList) {
		this.referraCenterList = referraCenterList;
	}
	
	public JSONArray getReferraCenterList() {
		return referraCenterList;
	}
	
	/**
	 * Gets the questionnaire list.
	 *
	 * @return the questionnaire list
	 */
	public ArrayList<QuestionnaireInfo> getQuestionnaireList() {
		return questionnaireList;
	}

	/**
	 * Sets the questionnaire list.
	 *
	 * @param questionnaireList the new questionnaire list
	 */
	public void setQuestionnaireList(ArrayList<QuestionnaireInfo> questionnaireList) {
		this.questionnaireList = questionnaireList;
	}

	/**
	 * Gets the household list.
	 *
	 * @return the household list
	 */
	public ArrayList<Household> getHouseholdList() {
		return householdList;
	}

	/**
	 * Sets the household list.
	 *
	 * @param householdList the new household list
	 */
	public void setHouseholdList(ArrayList<Household> householdList) {
		this.householdList = householdList;
	}

	/**
	 * Gets the fcm info.
	 *
	 * @return the fcm info
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}

	/**
	 * Sets the fcm info.
	 *
	 * @param userInfo the new fcm info
	 */
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	/**
	 * Gets the questionnaire category list.
	 *
	 * @return the questionnaire category list
	 */
	public ArrayList<QuestionnaireCategoryInfo> getQuestionnaireCategoryList() {
		return questionnaireCategoryList;
	}

	/**
	 * Sets the questionnaire category list.
	 *
	 * @param questionnaireCategoryList the new questionnaire category list
	 */
	public void setQuestionnaireCategoryList(
			ArrayList<QuestionnaireCategoryInfo> questionnaireCategoryList) {
		this.questionnaireCategoryList = questionnaireCategoryList;
	}

	/**
	 * Gets the beneficiary list.
	 *
	 * @return the beneficiary list
	 */
	public ArrayList<Beneficiary> getBeneficiaryList() {
		return beneficiaryList;
	}

	/**
	 * Sets the beneficiary list.
	 *
	 * @param beneficiaryList the new beneficiary list
	 */
	public void setBeneficiaryList(ArrayList<Beneficiary> beneficiaryList) {
		this.beneficiaryList = beneficiaryList;
	}

	/**
	 * Gets the medicine list.
	 *
	 * @return the medicine list
	 */
	public ArrayList<MedicineInfo> getMedicineList() {
		return medicineList;
	}

	/**
	 * Sets the medicine list.
	 *
	 * @param medicineList the new medicine list
	 */
	public void setMedicineList(ArrayList<MedicineInfo> medicineList) {
		this.medicineList = medicineList;
	}



	// Mohammed Jubayer


	/**
	 * Sets the appVersionHistory
	 *
	 * @param appVersionHistory 
	 */
	public void setAppVersionHistory(AppVersionHistory appVersionHistory) {
		this.appVersionHistory = appVersionHistory;
	}

	/**
	 * Gets the appVersionHistory.
	 *
	 * @return appVersionHistory
	 */
	public AppVersionHistory getAppVersionHistory() {
		return appVersionHistory;
	}


	public void setMaternalCareInfos(
			ArrayList<MaternalCareInfo> maternalCareInfos) {
		this.maternalCareInfos = maternalCareInfos;
	}
	public ArrayList<MaternalCareInfo> getMaternalCareInfos() {
		return maternalCareInfos;
	}

	public void setDiagnosisInfos(ArrayList<DiagnosisInfo> diagnosisInfos) {
		this.diagnosisInfos = diagnosisInfos;
	}

	public ArrayList<DiagnosisInfo> getDiagnosisInfos() {
		return diagnosisInfos;
	}



	public ArrayList<ImmunizableBeneficiary> getImmunizableBeneficiaries() {
		return immunizableBeneficiaries;
	}

	public void setImmunizableBeneficiaries(
			ArrayList<ImmunizableBeneficiary> immunizableBeneficiaries) {
		this.immunizableBeneficiaries = immunizableBeneficiaries;
	}

	public ArrayList<ImmunizationFollowup> getImmunizationFollowups() {
		return immunizationFollowups;
	}

	public void setImmunizationFollowups(
			ArrayList<ImmunizationFollowup> immunizationFollowups) {
		this.immunizationFollowups = immunizationFollowups;
	}

	public ArrayList<ImmunizationService> getImmunizationServices() {
		return immunizationServices;
	}

	public void setImmunizationServices(
			ArrayList<ImmunizationService> immunizationServices) {
		this.immunizationServices = immunizationServices;
	}

	public ArrayList<ImmunizationTarget> getImmunizationTargets() {
		return immunizationTargets;
	}

	public void setImmunizationTargets(
			ArrayList<ImmunizationTarget> immunizationTargets) {
		this.immunizationTargets = immunizationTargets;
	}

	public void setGrowthMeasurementParams(JSONArray growthMeasurementParams) {
		this.growthMeasurementParams = growthMeasurementParams;
	}
	public JSONArray getGrowthMeasurementParams() {
		return growthMeasurementParams;
	}

	public void setEventInfos(ArrayList<EventInfo> eventInfos) {
		this.eventInfos = eventInfos;
	}

	public ArrayList<EventInfo> getEventInfos() {
		return eventInfos;
	}

	public void setMaternalAbortions(
			ArrayList<MaternalAbortion> maternalAbortions) {
		this.maternalAbortions = maternalAbortions;
	}
	public ArrayList<MaternalAbortion> getMaternalAbortions() {
		return maternalAbortions;
	}
	public void setMaternalBabyInfos(
			ArrayList<MaternalBabyInfo> maternalBabyInfos) {
		this.maternalBabyInfos = maternalBabyInfos;
	}
	public ArrayList<MaternalBabyInfo> getMaternalBabyInfos() {
		return maternalBabyInfos;
	}
	
	public void setMaternalDeliveries(
			ArrayList<MaternalDelivery> maternalDeliveries) {
		this.maternalDeliveries = maternalDeliveries;
	}
	public ArrayList<MaternalDelivery> getMaternalDeliveries() {
		return maternalDeliveries;
	}
	
	public void setMaternalInfos(ArrayList<MaternalInfo> maternalInfos) {
		this.maternalInfos = maternalInfos;
	}
	public ArrayList<MaternalInfo> getMaternalInfos() {
		return maternalInfos;
	}
	
	public void setMaternalServices(ArrayList<MaternalService> maternalServices) {
		this.maternalServices = maternalServices;
	}
	
	public ArrayList<MaternalService> getMaternalServices() {
		return maternalServices;
	}
	
	public void setPatientInterviewMasters(
			ArrayList<PatientInterviewMaster> patientInterviewMasters) {
		this.patientInterviewMasters = patientInterviewMasters;
	}
	
	public void setFcmProfileJson(JSONArray fcmProfileJson) {
		this.fcmProfileJson = fcmProfileJson;
	}
	public JSONArray getFcmProfileJson() {
		return fcmProfileJson;
	}
	
	public ArrayList<PatientInterviewMaster> getPatientInterviewMasters() {
		return patientInterviewMasters;
	}
	
	public void setCcsTreatmentJsonArry(JSONArray ccsTreatmentJsonArry) {
		this.ccsTreatmentJsonArry = ccsTreatmentJsonArry;
	}
	
	public JSONArray getCcsTreatmentJsonArry() {
		return ccsTreatmentJsonArry;
	}
	
	public void setTopicInfoJsonArray(JSONArray topicInfoJsonArray) {
		this.topicInfoJsonArray = topicInfoJsonArray;
	}
	
	public JSONArray getTopicInfoJsonArray() {
		return topicInfoJsonArray;
	}
	
	public void setCourtyardMeetingTopicMonthJsonArray(
			JSONArray courtyardMeetingTopicMonthJsonArray) {
		this.courtyardMeetingTopicMonthJsonArray = courtyardMeetingTopicMonthJsonArray;
	}
	
	public JSONArray getCourtyardMeetingTopicMonthJsonArray() {
		return courtyardMeetingTopicMonthJsonArray;
	}
	
	
	public void setVersionNos(JSONObject versionNos) {
		this.versionNos = versionNos;
	}
	
	public JSONObject getVersionNos() {
		return versionNos;
	}
	public void setCcsEligibleJsonArry(JSONArray ccsEligibleJsonArry) {
		this.ccsEligibleJsonArry = ccsEligibleJsonArry;
	}
	
	public JSONArray getCcsEligibleJsonArry() {
		return ccsEligibleJsonArry;
	}
	
	public void setReportAsst(JSONArray reportAsst) {
		this.reportAsst = reportAsst;
	}
	
	public JSONArray getReportAsst() {
		return reportAsst;
	}
	
    public void setQuestionnaireBroadCategory(
			JSONArray questionnaireBroadCategory) {
		this.questionnaireBroadCategory = questionnaireBroadCategory;
	}
    
    public JSONArray getQuestionnaireBroadCategory() {
		return questionnaireBroadCategory;
	}
    
    public void setQuestionnaireServiceCategory(
			JSONArray questionnaireServiceCategory) {
		this.questionnaireServiceCategory = questionnaireServiceCategory;
	}
    public JSONArray getQuestionnaireServiceCategory() {
		return questionnaireServiceCategory;
	}
    
    public void setCourtyardMeetingTarget(JSONArray courtyardMeetingTarget) {
		this.courtyardMeetingTarget = courtyardMeetingTarget;
	}
    
    public JSONArray getCourtyardMeetingTarget() {
		return courtyardMeetingTarget;
	}
    
    public void setCourtyardMeeting(JSONArray courtyardMeeting) {
		this.courtyardMeeting = courtyardMeeting;
	}
    public JSONArray getCourtyardMeeting() {
		return courtyardMeeting;
	}
    public void setReferralCenterCategory(JSONArray referralCenterCategory) {
		this.referralCenterCategory = referralCenterCategory;
	}
    
    public JSONArray getDeathRegistration() {
		return deathRegistration;
	}

	public JSONArray getReferralCenterCategory() {
		return referralCenterCategory;
	}

	public void setDeathRegistration(JSONArray deathRegistration) {
		this.deathRegistration = deathRegistration;
	}
    
	public void setSuggestionText(JSONArray suggestionText) {
		this.suggestionText = suggestionText;
	}
	
	public JSONArray getSuggestionText() {
		return suggestionText;
	}
	
	public void setMedicineStock(JSONArray medicineStock) {
		this.medicineStock = medicineStock;
	}
	public JSONArray getMedicineStock() {
		return medicineStock;
	}
    
}
