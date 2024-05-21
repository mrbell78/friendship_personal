package ngo.friendship.satellite.model;

import java.io.Serializable;

public class PatientInterviewDoctorFeedback implements Serializable {

	
	private long docFollowupId;
	private long interviewId;
	private long feedbackDate;
	private String prescribedMedicine;
	private long nextFollowupDate;
	private long refCenterId;
	private String messageToFCM;
	private String questionAnsJson;
	private String questionAnsJson2;
	private int sendStatus;
	private String benefName;
	private String houseHoldNumber;
	private String interviewName;
	private String interviewTime;
	private String benefImagePath;
	private long isFeedbackOnTime;
	private String feedbackSource;
	private String invesAdvice;
	private String invesResult;
	private String invesStatus;
	private long feedbackReceiveTime;
	private int notificationStatus;
	private long userId;
	private String doctorFindings;
	private long transRef;
	private String benefCode;
	private long benefId;
	private String qName;
	private long fcmInterviewId;
	String gender;
	String dob;
	private String benefCodeShort;

	private boolean newDate=false;

	private long updateBy;
	private long updateOn;


	public boolean isNewDate() {
		return newDate;
	}

	public void setNewDate(boolean newDate) {
		this.newDate = newDate;
	}

	public String getBenefCodeShort() {
		return benefCodeShort;
	}

	public void setBenefCodeShort(String benefCodeShort) {
		this.benefCodeShort = benefCodeShort;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getBenefCode() {
		return benefCode;
	}

	public void setBenefCode(String benefCode) {
		this.benefCode = benefCode;
	}

	public String getqName() {
		return qName;
	}

	public void setqName(String qName) {
		this.qName = qName;
	}

	public String getQuestionAnsJson2() {
		return questionAnsJson2;
	}

	public void setQuestionAnsJson2(String questionAnsJson2) {
		this.questionAnsJson2 = questionAnsJson2;
	}


	public long getFeedbackReceiveTime() {
		return feedbackReceiveTime;
	}

	public void setFeedbackReceiveTime(long feedbackReceiveTime) {
		this.feedbackReceiveTime = feedbackReceiveTime;
	}


	public int getNotificationStatus() {
		return notificationStatus;
	}

	public void setNotificationStatus(int notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	public String getDoctorFindings() {
		return doctorFindings;
	}

	public void setDoctorFindings(String doctorFindings) {
		this.doctorFindings = doctorFindings;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setTransRef(long transRef) {
		this.transRef = transRef;
	}

	public long getTransRef() {
		return transRef;
	}
	public static final String MODEL_NAME = "patient_interview_doctor_feedback";

	public long getDocFollowupId() {
		return docFollowupId;
	}
	
	
	public void setDocFollowupId(long docFollowupId) {
		this.docFollowupId = docFollowupId;
	}
	public long getInterviewId() {
		return interviewId;
	}
	public void setInterviewId(long interviewId) {
		this.interviewId = interviewId;
	}
	public long getFeedbackDate() {
		return feedbackDate;
	}
	public void setFeedbackDate(long feedbackDate) {
		this.feedbackDate = feedbackDate;
	}
	public String getPrescribedMedicine() {
		return prescribedMedicine;
	}
	public void setPrescribedMedicine(String prescribedMedicine) {
		this.prescribedMedicine = prescribedMedicine;
	}
	public long getNextFollowupDate() {
		return nextFollowupDate;
	}
	public void setNextFollowupDate(long nextFollowupDate) {
		this.nextFollowupDate = nextFollowupDate;
	}
	public String getMessageToFCM() {
		return messageToFCM;
	}
	public void setMessageToFCM(String messageToFCM) {
		this.messageToFCM = messageToFCM;
	}
	public String getQuestionAnsJson() {
		return questionAnsJson;
	}
	public void setQuestionAnsJson(String questionAnsJson) {
		this.questionAnsJson = questionAnsJson;
	}
	public int getSendStatus() {
		return sendStatus;
	}
	public void setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
	}
	
	public void setRefCenterId(long refCenterId) {
		this.refCenterId = refCenterId;
	}
	public long getRefCenterId() {
		return refCenterId;
	}
	
	public String getBenefName() {
		return benefName;
	}

	public void setBenefName(String benefName) {
		this.benefName = benefName;
	}

	public String getHouseHoldNumber() {
		return houseHoldNumber;
	}


	public void setHouseHoldNumber(String houseHoldNumber) {
		this.houseHoldNumber = houseHoldNumber;
	}


	public String getInterviewName() {
		return interviewName;
	}


	public void setInterviewName(String interviewName) {
		this.interviewName = interviewName;
	}
	
    public void setInterviewTime(String interviewTime) {
		this.interviewTime = interviewTime;
	}
    public String getInterviewTime() {
		return interviewTime;
	}
    public void setBenefImagePath(String benefImagePath) {
		this.benefImagePath = benefImagePath;
	}
    public String getBenefImagePath() {
		return benefImagePath;
	}


	public long getIsFeedbackOnTime() {
		return isFeedbackOnTime;
	}

	public void setIsFeedbackOnTime(long isFeedbackOnTime) {
		this.isFeedbackOnTime = isFeedbackOnTime;
	}

	public String getFeedbackSource() {
		return feedbackSource;
	}

	public void setFeedbackSource(String feedbackSource) {
		this.feedbackSource = feedbackSource;
	}

	public String getInvesAdvice() {
		return invesAdvice;
	}

	public void setInvesAdvice(String invesAdvice) {
		this.invesAdvice = invesAdvice;
	}

	public String getInvesResult() {
		return invesResult;
	}

	public void setInvesResult(String invesResult) {
		this.invesResult = invesResult;
	}

	public String getInvesStatus() {
		return invesStatus;
	}

	public void setInvesStatus(String invesStatus) {
		this.invesStatus = invesStatus;
	}

	public long getBenefId() {
		return benefId;
	}

	public void setBenefId(long benefId) {
		this.benefId = benefId;
	}

	public long getFcmInterviewId() {
		return fcmInterviewId;
	}

	public void setFcmInterviewId(long fcmInterviewId) {
		this.fcmInterviewId = fcmInterviewId;
	}

	public long getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(long updateBy) {
		this.updateBy = updateBy;
	}

	public long getUpdateOn() {
		return updateOn;
	}

	public void setUpdateOn(long updateOn) {
		this.updateOn = updateOn;
	}


}
