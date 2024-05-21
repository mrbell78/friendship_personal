package ngo.friendship.satellite.model;

import java.io.Serializable;

// TODO: Auto-generated Javadoc

/**
 * The Class CCSBeneficiary.
 */
public class CCSBeneficiary extends Beneficiary implements Serializable{

	//	private String name;
	//	private String dob;
	//	private String age;
	private Long followUpVisit=-1L;
    private boolean  pregnant=false;
	private long eligibleId;
	private long activityStartDate;
	private long activityState;
	private long referralCenterId;
	private String referralCenterName;
	
	/** The reason for not to do test. */
	private String reasonForNotToDoTest;
	
	/** The comitted date to go hospital. */
	private String comittedDateToGoHospital;
	
	private String reEligibleDate;
	
	public void setReEligibleDate(String reEligibleDate) {
		this.reEligibleDate = reEligibleDate;
	}
	
	public String getReEligibleDate() {
		return reEligibleDate;
	}
	
	/** The number of times fcm go for convience. */
	private int numberOfFollowupVisit;
	private int numberOfFcmVisit;
	
	/** The followup reason. */
	private String followupReason;
	
	/** The next followup date. */
	private long nextFollowupDate;
	
	/** The remaining days to followup. */
	private String remainingDaysTOFollowup;
	
	/** The followup reason id. */
//	private long ccsStatusId;
	
	/** The treatment progress. */
	private int treatmentProgress;
	
	/** The ccs status name. */
	private String ccsStatusName;
	
//	/** The ccs status id. */
 	private long ccsStatusId;
//	
	/** The ccs treatment id. */
	private long ccsTreatmentId;
	
	
	private String ccsStatusCaption;
	
	long overCapacity;
	
	/**
	 * Sets the ccs treatment id.
	 *
	 * @param ccsTreatmentId the new ccs treatment id
	 */
	
	public void setCcsTreatmentId(long ccsTreatmentId) {
		this.ccsTreatmentId = ccsTreatmentId;
	}
	
	/**
	 * Gets the ccs treatment id.
	 *
	 * @return the ccs treatment id
	 */
	
	public long getCcsTreatmentId() {
		return ccsTreatmentId;
	}
	

	/**
	 * Sets the ccs status name.
	 *
	 * @param ccsStatusName the new ccs status name
	 */
	public void setCcsStatusName(String ccsStatusName) {
		this.ccsStatusName = ccsStatusName;
	}
	
	/**
	 * Gets the ccs status name.
	 *
	 * @return the ccs status name
	 */
	public String getCcsStatusName() {
		return ccsStatusName;
	}
	
	/**
	 * Sets the treatment progress.
	 *
	 * @param treatmentProgress the new treatment progress
	 */
	public void setTreatmentProgress(int treatmentProgress) {
		this.treatmentProgress = treatmentProgress;
	}
	
	/**
	 * Gets the treatment progress.
	 *
	 * @return the treatment progress
	 */
	public int getTreatmentProgress() {
		return treatmentProgress;
	}
	
	/**
	 * Sets the followup reason id.
	 *
	 * @param followupReasonId the new followup reason id
	 */
	public void setCcsStatusId(long ccsStatusId) {
		this.ccsStatusId = ccsStatusId;
	}
	
	/**
	 * Gets the followup reason id.
	 *
	 * @return the followup reason id
	 */
	public long getCcsStatusId() {
		return ccsStatusId;
	}
	
	/**
	 * Sets the remaining days to followup.
	 *
	 * @param remainingDaysTOFollowup the new remaining days to followup
	 */
	public void setRemainingDaysTOFollowup(String remainingDaysTOFollowup) {
		this.remainingDaysTOFollowup = remainingDaysTOFollowup;
	}
	
	/**
	 * Gets the remaining days to followup.
	 *
	 * @return the remaining days to followup
	 */
	public String getRemainingDaysTOFollowup() {
		return remainingDaysTOFollowup;
	}
	
	/**
	 * Sets the next followup date.
	 *
	 * @param nextFollowupDate the new next followup date
	 */
	public void setNextFollowupDate(long nextFollowupDate) {
		this.nextFollowupDate = nextFollowupDate;
	}
	
	/**
	 * Gets the next followup date.
	 *
	 * @return the next followup date
	 */
	public long getNextFollowupDate() {
		return nextFollowupDate;
	}
	
	/**
	 * Sets the followup reason.
	 *
	 * @param followupReason the new followup reason
	 */
	public void setFollowupReason(String followupReason) {
		this.followupReason = followupReason;
	}
	
	/**
	 * Gets the followup reason.
	 *
	 * @return the followup reason
	 */
	public String getFollowupReason() {
		return followupReason;
	}
	
	
	public void setReferralCenterId(long referralCenterId) {
		this.referralCenterId = referralCenterId;
	}
	public long getReferralCenterId() {
		return referralCenterId;
	}
	

	
	/**
   * Gets the reason for not to do test.
 *
 * @return the reason for not to do test
 */
    public String getReasonForNotToDoTest() {
		return reasonForNotToDoTest;
	}
	
	/**
	 * Sets the reason for not to do test.
	 *
	 * @param reasonForNotToDoTest the new reason for not to do test
	 */
	public void setReasonForNotToDoTest(String reasonForNotToDoTest) {
		this.reasonForNotToDoTest = reasonForNotToDoTest;
	}
	
	/**
	 * Gets the comitted date to go hospital.
	 *
	 * @return the comitted date to go hospital
	 */
	public String getComittedDateToGoHospital() {
		return comittedDateToGoHospital;
	}
	
	/**
	 * Sets the comitted date to go hospital.
	 *
	 * @param comittedDateToGoHospital the new comitted date to go hospital
	 */
	public void setComittedDateToGoHospital(String comittedDateToGoHospital) {
		this.comittedDateToGoHospital = comittedDateToGoHospital;
	}
	
	public void setNumberOfFcmVisit(int numberOfFcmVisit) {
		this.numberOfFcmVisit = numberOfFcmVisit;
	}
	
	public int getNumberOfFcmVisitAAA() {
		return numberOfFcmVisit;
	}
	
	public void setNumberOfFollowupVisit(int numberOfFollowupVisit) {
		this.numberOfFollowupVisit = numberOfFollowupVisit;
	}
	public int getNumberOfFollowupVisit() {
		return numberOfFollowupVisit;
	}
	
	public void setCcsStatusCaption(String ccsStatusCaption) {
		this.ccsStatusCaption = ccsStatusCaption;
	}
	
	public String getCcsStatusCaption() {
		return ccsStatusCaption;
	}


	
	public long getEligibleId() {
		return eligibleId;
	}

	
	public void setEligibleId(long eligibleId) {
		this.eligibleId = eligibleId;
	}

	
	public long getActivityStartDate() {
		return activityStartDate;
	}

	
	public void setActivityStartDate(long activityStartDate) {
		this.activityStartDate = activityStartDate;
	}

	
	public long getActivityState() {
		return activityState;
	}

	
	public void setActivityState(long activityState) {
		this.activityState = activityState;
	}
 
	public void setPregnant(boolean pregnant) {
		this.pregnant = pregnant;
	}
	public boolean isPregnant(){
		return this.pregnant;
	}
   
	public void setFollowUpVisit(Long followUpVisit) {
		this.followUpVisit = followUpVisit;
	}
	public Long getFollowUpVisit() {
		return followUpVisit;
	}

	public void setReferralCenterName(String referralCenterName) {
		this.referralCenterName = referralCenterName;
	}
	public String getReferralCenterName() {
		return referralCenterName;
	}

	public void setOverCapacity(long overCapacity) {
		this.overCapacity = overCapacity;
	}
	
	public long getOverCapacity() {
		return overCapacity;
	}


}
