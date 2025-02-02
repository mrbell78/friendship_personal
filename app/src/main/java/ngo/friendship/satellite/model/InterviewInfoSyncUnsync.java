package ngo.friendship.satellite.model;

import java.io.Serializable;


public class InterviewInfoSyncUnsync implements Serializable {

    public String questionnarieName;

    /**
     * The file full name.
     */
    private String fileFullName;

    /**
     * The question answer json.
     */
    private String questionAnswerJson;

    /**
     * The input binary file path list.
     */
    private String inputBinaryFilePathList;


    /**
     * The benef name.
     */
    private String benefName;

    /**
     * The household number.
     */
    private String householdNumber;

    /**
     * The time.
     */
    private String time;

    /**
     * The date.
     */
    private String date;
    private boolean newDate = false;

    /**
     * The questionnarie title.
     */
    private String questionnarieTitle;

    /**
     * The benef image path.
     */
    private String benefImagePath;

    /**
     * The interview id.
     */
    private long interviewId;

    /**
     * The beneficiary code.
     */
    private String beneficiaryCode;
    /**
     * The beneficiary gender.
     */
    private String beneficiarGender;

    /**
     * The beneficiary age.
     */
    private String age;

    /**
     * The beneficiary id.
     */
    private long beneficiaryId;

    /**
     * The parent interview id.
     */
    private long parentInterviewId;

    /**
     * The parent questionnaire id.
     */
    private long parentQuestionnaireId;

    /**
     * The schedule info.
     */
    private ScheduleInfo scheduleInfo;

    private long transRef;

    private String fileKey;

    private boolean selected;

    private long questionnaireId;

    //for doctor feedback
    private long docFollowupId;
    private long feedbackDate;
    private String prescribedMedicine;
    private long nextFollowupDate;
    private long refCenterId;
    private String messageToFCM;
    private String questionAnsJson;
    private String questionAnsJson2;
    private int sendStatus;
    private String houseHoldNumber;
    private String interviewName;
    private String interviewTime;
    private long isFeedbackOnTime;
    private String feedbackSource;
    private String invesAdvice;
    private String invesResult;
    private String invesStatus;
    private long feedbackReceiveTime;
    private int notificationStatus;
    private long userId;
    private String doctorFindings;
    private String benefCode;
    private long benefId;
    private String qName;
    private long fcmInterviewId;
    private String questionnaireIcon;
//household list
    private long hhId;
    private String hhNumber;
    private String hhName;
    private long locationId;
    private String hhGrade;
    private long createDate;
    private double latitude;
    private double longitude;
    private long refDataId;
    private long state;
    private long noOfFamilyMember;
    private long regDate;
    private long sent;
    private String monthlyFamilyExpenditure;
    private long hhAdultWomen;
    private String hhCharacter;


    private String householdHeadName;
    private String householdHeadCode;
    private String householdHeadAge;
    private String householdHeadImagePath;
    private String householdHeadGender;
    private boolean hasLocation;


    private long numberOfBeneficiary;
    private String latitudeStr;
    private String longitudeStr;

    private String updateHistory;

    private String fullHouseHoldNumber;

    private long updateBy;
    private long updateOn;

    private String benefAddress;
    private String sentStatus;
    private String recordDate;

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(String sentStatus) {
        this.sentStatus = sentStatus;
    }

    public String getBenefAddress() {
        return benefAddress;
    }

    public void setBenefAddress(String benefAddress) {
        this.benefAddress = benefAddress;
    }

    public String getQuestionnaireIcon() {
        return questionnaireIcon;
    }

    public void setQuestionnaireIcon(String questionnaireIcon) {
        this.questionnaireIcon = questionnaireIcon;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public long getDocFollowupId() {
        return docFollowupId;
    }

    public void setDocFollowupId(long docFollowupId) {
        this.docFollowupId = docFollowupId;
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

    public long getRefCenterId() {
        return refCenterId;
    }

    public void setRefCenterId(long refCenterId) {
        this.refCenterId = refCenterId;
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

    public String getQuestionAnsJson2() {
        return questionAnsJson2;
    }

    public void setQuestionAnsJson2(String questionAnsJson2) {
        this.questionAnsJson2 = questionAnsJson2;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
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

    public String getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(String interviewTime) {
        this.interviewTime = interviewTime;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDoctorFindings() {
        return doctorFindings;
    }

    public void setDoctorFindings(String doctorFindings) {
        this.doctorFindings = doctorFindings;
    }

    public String getBenefCode() {
        return benefCode;
    }

    public void setBenefCode(String benefCode) {
        this.benefCode = benefCode;
    }

    public long getBenefId() {
        return benefId;
    }

    public void setBenefId(long benefId) {
        this.benefId = benefId;
    }

    public String getqName() {
        return qName;
    }

    public void setqName(String qName) {
        this.qName = qName;
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

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public boolean isSelected() {
        return this.selected;
    }

    public void setTransRef(long transRef) {
        this.transRef = transRef;
    }

    public long getTransRef() {
        return transRef;
    }

    /**
     * Sets the parent questionnaire id.
     *
     * @param parentQuestionnaireId the new parent questionnaire id
     */
    public void setParentQuestionnaireId(long parentQuestionnaireId) {
        this.parentQuestionnaireId = parentQuestionnaireId;
    }

    /**
     * Gets the parent questionnaire id.
     *
     * @return the parent questionnaire id
     */
    public long getParentQuestionnaireId() {
        return parentQuestionnaireId;
    }

    /**
     * Sets the schedule info.
     *
     * @param scheduleInfo the new schedule info
     */
    public void setScheduleInfo(ScheduleInfo scheduleInfo) {
        this.scheduleInfo = scheduleInfo;
    }

    /**
     * Gets the schedule info.
     *
     * @return the schedule info
     */
    public ScheduleInfo getScheduleInfo() {
        return scheduleInfo;
    }

    /**
     * Sets the parent interview id.
     *
     * @param parentInterviewId the new parent interview id
     */
    public void setParentInterviewId(long parentInterviewId) {
        this.parentInterviewId = parentInterviewId;
    }

    /**
     * Gets the parent interview id.
     *
     * @return the parent interview id
     */
    public long getParentInterviewId() {
        return parentInterviewId;
    }

    /**
     * Sets the beneficiary id.
     *
     * @param beneficiaryId the new beneficiary id
     */
    public void setBeneficiaryId(long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    /**
     * Gets the beneficiary id.
     *
     * @return the beneficiary id
     */
    public long getBeneficiaryId() {
        return beneficiaryId;
    }

    /**
     * Sets the beneficiary code.
     *
     * @param beneficiaryCode the new beneficiary code
     */
    public void setBeneficiaryCode(String beneficiaryCode) {
        this.beneficiaryCode = beneficiaryCode;
    }

    /**
     * Gets the beneficiary code.
     *
     * @return the beneficiary code
     */
    public String getBeneficiaryCode() {
        return beneficiaryCode;
    }


    public String getBeneficiarGender() {
        return beneficiarGender;
    }

    public void setBeneficiarGender(String beneficiarGender) {
        this.beneficiarGender = beneficiarGender;
    }

    /**
     * Sets the age.
     *
     * @param age the new age
     */
    public void setBeneficiarAge(String age) {
        this.age = age;
    }

    /**
     * Gets the age.
     *
     * @return the age
     */
    public String getBeneficiarAge() {
        return age;
    }

    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the interview id.
     *
     * @param interviewId the new interview id
     */
    public void setInterviewId(long interviewId) {
        this.interviewId = interviewId;
    }

    /**
     * Gets the interview id.
     *
     * @return the interview id
     */
    public long getInterviewId() {
        return interviewId;
    }

    /**
     * Sets the benef image path.
     *
     * @param benefImagePath the new benef image path
     */
    public void setBenefImagePath(String benefImagePath) {
        this.benefImagePath = benefImagePath;
    }

    /**
     * Gets the benef image path.
     *
     * @return the benef image path
     */
    public String getBenefImagePath() {
        return benefImagePath;
    }

    /**
     * Gets the benef name.
     *
     * @return the benef name
     */
    public String getBenefName() {
        return benefName;
    }

    /**
     * Sets the benef name.
     *
     * @param benefName the new benef name
     */
    public void setBenefName(String benefName) {
        this.benefName = benefName;
    }

    /**
     * Gets the household number.
     *
     * @return the household number
     */
    public String getHouseholdNumber() {
        return householdNumber;
    }

    /**
     * Sets the household number.
     *
     * @param householdNumber the new household number
     */
    public void setHouseholdNumber(String householdNumber) {
        this.householdNumber = householdNumber;
    }

    /**
     * Gets the time.
     *
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the time.
     *
     * @param time the new time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets the questionnarie title.
     *
     * @return the questionnarie title
     */
    public String getQuestionnarieTitle() {
        return questionnarieTitle;
    }

    /**
     * Sets the questionnarie title.
     *
     * @param questionnarieTitle the new questionnarie title
     */
    public void setQuestionnarieTitle(String questionnarieTitle) {
        this.questionnarieTitle = questionnarieTitle;
    }

    /**
     * Gets the file full name.
     *
     * @return the file full name
     */
    public String getFileFullName() {
        return fileFullName;
    }

    /**
     * Sets the file full name.
     *
     * @param fileFullName the new file full name
     */
    public void setFileFullName(String fileFullName) {
        this.fileFullName = fileFullName;
    }

    /**
     * Gets the question answer json.
     *
     * @return the question answer json
     */
    public String getQuestionAnswerJson() {
        return questionAnswerJson;
    }

    /**
     * Sets the question answer json.
     *
     * @param questionAnswerJson the new question answer json
     */
    public void setQuestionAnswerJson(String questionAnswerJson) {
        this.questionAnswerJson = questionAnswerJson;
    }

    /**
     * Gets the input binary file path list.
     *
     * @return the input binary file path list
     */
    public String getInputBinaryFilePathList() {
        return inputBinaryFilePathList;
    }

    public void setNewDate(boolean newDate) {
        this.newDate = newDate;
    }


    public boolean isNewDate() {
        return newDate;
    }

    /**
     * Sets the input binary file path list.
     *
     * @param inputBinaryFilePathList the new input binary file path list
     */
    public void setInputBinaryFilePathList(String inputBinaryFilePathList) {
        this.inputBinaryFilePathList = inputBinaryFilePathList;
    }

    public void setQuestionnarieName(String questionnarieName) {
        this.questionnarieName = questionnarieName;
    }

    public String getQuestionnarieName() {
        return questionnarieName;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileKey() {
        return fileKey;
    }


    public void setQuestionnaireId(long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public long getQuestionnaireId() {
        return questionnaireId;
    }

    @Override
    public String toString() {
        return "SavedInterviewInfo [interviewId=" + interviewId
                + ", householdNumber=" + householdNumber + ", beneficiaryCode="
                + beneficiaryCode + ", questionnarieName=" + questionnarieName
                + ", benefName=" + benefName + ", time=" + time + ", date=" + date
                + ", questionnarieTitle=" + questionnarieTitle + "]";
    }
    public String getHhName() {
        return hhName;
    }

    public void setHhName(String hhName) {
        this.hhName = hhName;
    }

    public long getHhAdultWomen() {
        return hhAdultWomen;
    }

    public void setHhAdultWomen(long hhAdultWomen) {
        this.hhAdultWomen = hhAdultWomen;
    }

    public String getHhCharacter() {
        return hhCharacter;
    }

    public void setHhCharacter(String hhCharacter) {
        this.hhCharacter = hhCharacter;
    }

    public String getMonthlyFamilyExpenditure() {
        return monthlyFamilyExpenditure;
    }

    public void setMonthlyFamilyExpenditure(String monthlyFamilyExpenditure) {
        this.monthlyFamilyExpenditure = monthlyFamilyExpenditure;
    }

    public void setLatitudeStr(String latitudeStr) {
        this.latitudeStr = latitudeStr;
    }

    public String getLatitudeStr() {
        return latitudeStr;
    }

    public void setLongitudeStr(String longitudeStr) {
        this.longitudeStr = longitudeStr;
    }

    public String getLongitudeStr() {
        return longitudeStr;
    }

    public long getHhId() {
        return hhId;
    }

    public void setHhId(long hhId) {
        this.hhId = hhId;
    }

    public String getHhNumber() {
        return hhNumber;
    }

    public void setHhNumber(String hhNumber) {
        this.hhNumber = hhNumber;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getHhGrade() {
        return hhGrade;
    }

    public void setHhGrade(String hhGrade) {
        this.hhGrade = hhGrade;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getRefDataId() {
        return refDataId;
    }

    public void setRefDataId(long refDataId) {
        this.refDataId = refDataId;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public long getNoOfFamilyMember() {
        return noOfFamilyMember;
    }

    public void setNoOfFamilyMember(long noOfFamilyMember) {
        this.noOfFamilyMember = noOfFamilyMember;
    }

    public long getRegDate() {
        return regDate;
    }

    public void setRegDate(long regDate) {
        this.regDate = regDate;
    }

    public String getHouseholdHeadName() {
        return householdHeadName;
    }

    public void setHouseholdHeadName(String householdHeadName) {
        this.householdHeadName = householdHeadName;
    }

    public String getHouseholdHeadCode() {
        return householdHeadCode;
    }

    public void setHouseholdHeadCode(String householdHeadCode) {
        this.householdHeadCode = householdHeadCode;
    }

    public String getHouseholdHeadAge() {
        return householdHeadAge;
    }

    public void setHouseholdHeadAge(String householdHeadAge) {
        this.householdHeadAge = householdHeadAge;
    }

    public String getHouseholdHeadImagePath() {
        return householdHeadImagePath;
    }

    public void setHouseholdHeadImagePath(String householdHeadImagePath) {
        this.householdHeadImagePath = householdHeadImagePath;
    }

    public String getHouseholdHeadGender() {
        return householdHeadGender;
    }

    public void setHouseholdHeadGender(String householdHeadGender) {
        this.householdHeadGender = householdHeadGender;
    }

    public boolean isHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(boolean hasLocation) {
        this.hasLocation = hasLocation;
    }

    public void setNumberOfBeneficiary(long numberOfBeneficiary) {
        this.numberOfBeneficiary = numberOfBeneficiary;
    }

    public long getNumberOfBeneficiary() {
        return numberOfBeneficiary;
    }

    public void setSent(long sent) {
        this.sent = sent;
    }

    public long getSent() {
        return sent;
    }

    public String getUpdateHistory() {
        return updateHistory;
    }

    public void setUpdateHistory(String updateHistory) {
        this.updateHistory = updateHistory;
    }

    public String getFullHouseHoldNumber() {
        return fullHouseHoldNumber;
    }

    public void setFullHouseHoldNumber(String fullHouseHoldNumber) {
        this.fullHouseHoldNumber = fullHouseHoldNumber;
    }

}
