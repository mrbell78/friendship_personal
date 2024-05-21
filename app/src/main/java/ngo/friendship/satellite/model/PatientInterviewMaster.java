package ngo.friendship.satellite.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
*
* @author User
*/
//Table(name = "patient_interview_master")
public class PatientInterviewMaster implements Serializable{
   //Column(name = "INTERVIEW_ID")
   private long interviewId;
   //Column(name = "BENEF_ID")
   private long benefId;
   //Column(name = "USER_ID")
   private long userId;
   //Column(name = "CREATE_DATE")
   private long createDate;
   //Column(name = "QUESTIONNAIRE_ID")
   private long questionnaireId;
   //Column(name = "REF_DATA_ID")
   private long refDataId;
   //Column(name = "START_TIME")
   private long startTime;
   //Column(name = "DURATION")
   private int duration;
   //Column(name = "REF_CENTER_ID")
   private Integer refCenterId = null;
   //Column(name = "NEXT_FOLLOWUP_DATE")
   private long nextFollowupDate;
   //Column(name = "UPDATED_ON")
   private long updatedOn;
   //Column(name = "PRIORITY")
   private int priority;
   //Column(name = "STATUS")
   private String status;
   //JoinColumn(name = "PARENT_INTERVIEW_ID")
   private String benefCode;
   
   private String benefName;
   
   private Long parentInterviewId;
   private String diagDesc;
   private long locationId;
   private String gender;
   private long ageInDay;
   private long feedBack;
	private long fcmInterviewId;
	private String benefAddress;
	private String RecordDate;

	public String getRecordDate() {
		return RecordDate;
	}

	public void setRecordDate(String recordDate) {
		RecordDate = recordDate;
	}

	public String getBenefAddress() {
		return benefAddress;
	}

	public void setBenefAddress(String benefAddress) {
		this.benefAddress = benefAddress;
	}

	public long getFcmInterviewId() {
		return fcmInterviewId;
	}

	public void setFcmInterviewId(long fcmInterviewId) {
		this.fcmInterviewId = fcmInterviewId;
	}



   ArrayList<PatientInterviewDetail> details ;
   
   public void setDetails(ArrayList<PatientInterviewDetail> details) {
	this.details = details;
   }
   public ArrayList<PatientInterviewDetail> getDetails() {
	return details;}
   
   
	public long getInterviewId() {
		return interviewId;
	}
	public void setInterviewId(long interviewId) {
		this.interviewId = interviewId;
	}
	public long getBenefId() {
		return benefId;
	}
	public void setBenefId(long benefId) {
		this.benefId = benefId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getCreateDate() {
		return createDate;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	public long getQuestionnaireId() {
		return questionnaireId;
	}
	public void setQuestionnaireId(long questionnaireId) {
		this.questionnaireId = questionnaireId;
	}
	public long getRefDataId() {
		return refDataId;
	}
	public void setRefDataId(long refDataId) {
		this.refDataId = refDataId;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public Integer getRefCenterId() {
		return refCenterId;
	}
	public void setRefCenterId(Integer refCenterId) {
		this.refCenterId = refCenterId;
	}
	public long getNextFollowupDate() {
		return nextFollowupDate;
	}
	public void setNextFollowupDate(long nextFollowupDate) {
		this.nextFollowupDate = nextFollowupDate;
	}
	public long getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(long updatedOn) {
		this.updatedOn = updatedOn;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getParentInterviewId() {
		return parentInterviewId;
	}
	public void setParentInterviewId(Long parentInterviewId) {
		this.parentInterviewId = parentInterviewId;
	}
	public String getDiagDesc() {
		return diagDesc;
	}
	public void setDiagDesc(String diagDesc) {
		this.diagDesc = diagDesc;
	}
	public long getLocationId() {
		return locationId;
	}
	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public long getAgeInDay() {
		return ageInDay;
	}
	public void setAgeInDay(long ageInDay) {
		this.ageInDay = ageInDay;
    }
	
	public void setBenefCode(String benefCode) {
		this.benefCode = benefCode;
	}
	public String getBenefCode() {
		return benefCode;
	}
	
	
	   //TRANS_REF
    private long transRef;

    public void setTransRef(long transRef) {
        this.transRef = transRef;
    }

    public long getTransRef() {
        return transRef;
    }
	public static final String MODEL_NAME = "patient_interview_master";
    
	public void setFeedBack(long feedBack) {
		this.feedBack = feedBack;
	}
	public long getFeedBack() {
		return feedBack;
	}
	
	public void setBenefName(String benefName) {
		this.benefName = benefName;
	}
	public String getBenefName() {
		return benefName;
	}

	private String questionAnsJson;
	public String getQuestionAnsJson() {
		return questionAnsJson;
	}
	public void setQuestionAnsJson(String questionAnsJson) {
		this.questionAnsJson = questionAnsJson;
	}
	
}
