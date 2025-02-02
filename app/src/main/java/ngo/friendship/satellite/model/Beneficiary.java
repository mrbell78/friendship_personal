package ngo.friendship.satellite.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.utility.Utility;

// TODO: Auto-generated Javadoc

/**
 * The Class Beneficiary.
 *
 * @author User
 */
// Table(name = "beneficiary")
public class Beneficiary implements Serializable {

    // Column(name = "BENEF_ID")
    /**
     * The benef id.
     */
    private long benefId;
    // Column(name = "HH_ID")
    /**
     * The hh id.
     */

    private long userId;
    // Column(name = "Userid")
    /**
     * The Userid.
     */
    private long hhId;
    // Column(name = "BENEF_NAME")
    /**
     * The benef name.
     */
    private String benefName="";
    // Column(name = "BENEF_CODE")
    /**
     * The benef code.
     */
    private String benefCode;
    // Column(name = "FAMILY_HEAD")
    /**
     * The family head.
     */
    private int familyHead;
    // Column(name = "DOB")
    /**
     * The dob.
     */
    private String dob;
    // Column(name = "GENDER")
    /**
     * The gender.
     */
    private String gender="";
    // Column(name = "BENEF_IMAGE_PATH")
    /**
     * The benef image path.
     */
    private String benefImagePath;
    // Column(name = "EDU_LEVEL")
    /**
     * The edu level.
     */
    private String eduLevel;
    // Column(name = "CREATE_DATE")
    /**
     * The create date.
     */
    private String createDate;
    // Column(name = "REF_DATA_ID")
    /**
     * The ref data id.
     */
    private int refDataId;
    // Column(name = "REF_DATA_ID")
    /**
     * The mobile number.
     */
    private String mobileNumber;

    /**
     * The mobile comm.
     */
    private String mobileComm;
    // private String relationToFamilyHead;

    // Column(name = "BENEF_NAME_LOCAL")
    /**
     * The benef local name.
     */
    private String benefLocalName;

    /**
     * The hh number.
     */
    private String hhNumber;

    /**
     * The age.
     */
    private String age;

    /**
     * The state.
     */
    private int state;


    // Column(name = "GUARDIAN_NAME")
    /**
     * The guardian name.
     */
    private String guardianName;

    // Column(name = "GUARDIAN_NAME_LOCAL")
    /**
     * The guardian local name.
     */
    private String guardianLocalName;

    // Column(name = "RELATION_GUARDIAN")
    /**
     * The relation to gurdian.
     */
    private String relationToGurdian;

    /**
     * The religion.
     */
    private String religion;

    /**
     * The marital status.
     */
    private String maritalStatus;

    /**
     * The occupation.
     */
    private String occupation;

    /**
     * The national id number.
     */
    private String nationalIdNumber;

    /**
     * The birth certificate number.
     */
    private String birthCertificateNumber;

    /**
     * The saved interview info.
     */
    private SavedInterviewInfo savedInterviewInfo;

    private long maternalStatus = -1;

    private boolean updated = false;

    // Column(name = "LMP") for maternalinfo
    private long lmp = -1;
    private long calculateLmp = -1;

    private long maxMaternalCompleteDate;

    private String fileKey;

    private String regFormPath;

    private String benefCodeFull;
    private String benefCodeShort;

    private String householdLocation;
    private String occupationHerHusband;
    private String monthlyFamilyExpenditure;
    private String entryHouseHoldParams;
    private String hhCharacter;
    private String hhName;
    private long hhFamilyMembers;
    private long hhAdultWomen;
    private String religionOtherSpecofic = "";
    private String mobileCommLang = "";
    private String agreedMobileComm = "";
    private double latitude;
    private double longitude;
    private String remainingDays ="";
    private String lastServiceName ="";
    private String lastServiceDate ="";
    private String benefType ="";
    private String maternalID ="";
    private String heightInGm ="";
    private Long gravida =0L;
    private Double bmiValue =0.0;
    private long edd =0L;

    private String fcmName ="";
    private String locationName ="";
    private String address ="";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getFcmName() {
        return fcmName;
    }

    public void setFcmName(String fcmName) {
        this.fcmName = fcmName;
    }

    public void setHhFamilyMembers(long hhFamilyMembers) {
        this.hhFamilyMembers = hhFamilyMembers;
    }


    public String getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(String lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public String getLastServiceName() {
        return lastServiceName;
    }

    public void setLastServiceName(String lastServiceName) {
        this.lastServiceName = lastServiceName;
    }

    public String getBenefType() {
        return benefType;
    }

    public void setBenefType(String benefType) {
        this.benefType = benefType;
    }

    public String getMaternalID() {
        return maternalID;
    }

    public void setMaternalID(String maternalID) {
        this.maternalID = maternalID;
    }

    public String getHeightInGm() {
        return heightInGm;
    }

    public void setHeightInGm(String heightInGm) {
        this.heightInGm = heightInGm;
    }

    public Long getGravida() {
        return gravida;
    }

    public void setGravida(Long gravida) {
        this.gravida = gravida;
    }

    public Double getBmiValue() {
        return bmiValue;
    }

    public void setBmiValue(Double bmiValue) {
        this.bmiValue = bmiValue;
    }

    public long getEdd() {
        return edd;
    }

    public void setEdd(long edd) {
        this.edd = edd;
    }

    public String getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(String remainingDays) {
        this.remainingDays = remainingDays;
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

    public String getHhName() {
        return hhName;
    }

    public void setHhName(String hhName) {
        this.hhName = hhName;
    }

    public String getMobileCommLang() {
        return mobileCommLang;
    }

    public void setMobileCommLang(String mobileCommLang) {
        this.mobileCommLang = mobileCommLang;
    }

    public String getAgreedMobileComm() {
        return agreedMobileComm;
    }

    public void setAgreedMobileComm(String agreedMobileComm) {
        this.agreedMobileComm = agreedMobileComm;
    }

    public String getReligionOtherSpecofic() {
        return religionOtherSpecofic;
    }

    public void setReligionOtherSpecofic(String religionOtherSpecofic) {
        this.religionOtherSpecofic = religionOtherSpecofic;
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

    public long getHhFamilyMembers() {
        return hhFamilyMembers;
    }

    public void setHhFamilyMembers(int hhFamilyMembers) {
        this.hhFamilyMembers = hhFamilyMembers;
    }

    public void setBenefCodeShort(String benefCodeShort) {
        this.benefCodeShort = benefCodeShort;
    }

    public String getBenefCodeShort() {
        return benefCodeShort;
    }

    /**
     * Sets the saved interview info.
     *
     * @param savedInterviewInfo
     *            the new saved interview info
     */
//	public void setSavedInterviewInfo(SavedInterviewInfo savedInterviewInfo) {
//		this.savedInterviewInfo = savedInterviewInfo;
//	}
//
//	/**
//	 * Gets the saved interview info.
//	 *
//	 * @return the saved interview info
//	 */
//	public SavedInterviewInfo getSavedInterviewInfo() {
//		return savedInterviewInfo;
//	}

    /**
     * Sets the birth certificate number.
     *
     * @param birthCertificateNumber the new birth certificate number
     */
    public void setBirthCertificateNumber(String birthCertificateNumber) {
        this.birthCertificateNumber = birthCertificateNumber;
    }

    /**
     * Gets the birth certificate number.
     *
     * @return the birth certificate number
     */
    public String getBirthCertificateNumber() {
        return birthCertificateNumber;
    }

    /**
     * Sets the national id number.
     *
     * @param nationalIdNumber the new national id number
     */
    public void setNationalIdNumber(String nationalIdNumber) {
        this.nationalIdNumber = nationalIdNumber;
    }

    /**
     * Gets the national id number.
     *
     * @return the national id number
     */
    public String getNationalIdNumber() {
        return nationalIdNumber;
    }

    /**
     * Sets the marital status.
     *
     * @param maritalStatus the new marital status
     */
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * Gets the marital status.
     *
     * @return the marital status
     */
    public String getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the occupation.
     *
     * @param occupation the new occupation
     */
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    /**
     * Gets the occupation.
     *
     * @return the occupation
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * Sets the religion.
     *
     * @param religion the new religion
     */
    public void setReligion(String religion) {
        this.religion = religion;
    }

    /**
     * Gets the religion.
     *
     * @return the religion
     */
    public String getReligion() {
        return religion;
    }

    /**
     * Gets the guardian name.
     *
     * @return the guardian name
     */
    public String getGuardianName() {
        return guardianName;
    }

    /**
     * Sets the guardian name.
     *
     * @param guardianName the new guardian name
     */
    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    /**
     * Gets the guardian local name.
     *
     * @return the guardian local name
     */
    public String getGuardianLocalName() {
        return guardianLocalName;
    }

    /**
     * Sets the guardian local name.
     *
     * @param guardianLocalName the new guardian local name
     */
    public void setGuardianLocalName(String guardianLocalName) {
        this.guardianLocalName = guardianLocalName;
    }

    /**
     * Gets the relation to gurdian.
     *
     * @return the relation to gurdian
     */
    public String getRelationToGurdian() {
        return relationToGurdian;
    }

    /**
     * Sets the relation to gurdian.
     *
     * @param relationToGurdian the new relation to gurdian
     */
    public void setRelationToGurdian(String relationToGurdian) {
        this.relationToGurdian = relationToGurdian;
    }

    /**
     * Sets the state.
     *
     * @param state the new state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the age.
     *
     * @param age the new age
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * Gets the age.
     *
     * @return the age
     */
    public String getAge() {
        return age;
    }

    /**
     * Sets the hh number.
     *
     * @param hhNumber the new hh number
     */
    public void setHhNumber(String hhNumber) {
        this.hhNumber = hhNumber;
    }

    /**
     * Gets the hh number.
     *
     * @return the hh number
     */
    public String getHhNumber() {
        return hhNumber;
    }

    /**
     * Instantiates a new beneficiary.
     */
    public Beneficiary() {
    }

    /**
     * Instantiates a new beneficiary.
     *
     * @param benefId the benef id
     */
    public Beneficiary(int benefId) {
        this.benefId = benefId;
    }

    /**
     * Instantiates a new beneficiary.
     *
     * @param benefId    the benef id
     * @param hhId       the hh id
     * @param familyHead the family head
     * @param gender     the gender
     * @param createDate the create date
     */
    public Beneficiary(int benefId, int hhId, int familyHead, String gender,
                       String createDate) {
        this.benefId = benefId;
        this.hhId = hhId;
        this.familyHead = familyHead;
        this.gender = gender;
        this.createDate = createDate;
    }

    public String getEntryHouseHoldParams() {
        return entryHouseHoldParams;
    }

    public void setEntryHouseHoldParams(String entryHouseHoldParams) {
        this.entryHouseHoldParams = entryHouseHoldParams;
    }

    /**
     * Sets the benef local name.
     *
     * @param benefLocalName the new benef local name
     */
    public void setBenefLocalName(String benefLocalName) {
        this.benefLocalName = benefLocalName;
    }

    /**
     * Gets the benef local name.
     *
     * @return the benef local name
     */
    public String getBenefLocalName() {
        return benefLocalName;
    }

    /**
     * Gets the benef id.
     *
     * @return the benef id
     */
    public long getBenefId() {
        return benefId;
    }

    /**
     * Sets the benef id.
     *
     * @param benefId the new benef id
     */
    public void setBenefId(long benefId) {
        this.benefId = benefId;
    }

    /**
     * Gets the hh id.
     *
     * @return the hh id
     */
    public long getHhId() {
        return hhId;
    }

    /**
     * Sets the hh id.
     *
     * @param hhId the new hh id
     */
    public void setHhId(long hhId) {
        this.hhId = hhId;
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
     * Gets the family head.
     *
     * @return the family head
     */
    public int getFamilyHead() {
        return familyHead;
    }

    /**
     * Sets the family head.
     *
     * @param familyHead the new family head
     */
    public void setFamilyHead(int familyHead) {
        this.familyHead = familyHead;
    }

    /**
     * Gets the dob.
     *
     * @return the dob
     */
    public String getDob() {
        return dob;
    }

    /**
     * Sets the dob.
     *
     * @param dob the new dob
     */
    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * Gets the gender.
     *
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender.
     *
     * @param gender the new gender
     */
    public void setGender(String gender) {
        this.gender = gender;
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
     * Sets the benef image path.
     *
     * @param benefImagePath the new benef image path
     */
    public void setBenefImagePath(String benefImagePath) {
        if (benefImagePath != null && !benefImagePath.contains("null")) {
            this.benefImagePath = benefImagePath;
        } else {
            this.benefImagePath = "";
        }

    }

    /**
     * Gets the edu level.
     *
     * @return the edu level
     */
    public String getEduLevel() {
        return eduLevel;
    }

    /**
     * Sets the edu level.
     *
     * @param eduLevel the new edu level
     */
    public void setEduLevel(String eduLevel) {
        this.eduLevel = eduLevel;
    }

    /**
     * Gets the creates the date.
     *
     * @return the creates the date
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     * Sets the creates the date.
     *
     * @param createDate the new creates the date
     */
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets the ref data id.
     *
     * @return the ref data id
     */
    public int getRefDataId() {
        return refDataId;
    }

    /**
     * Sets the ref data id.
     *
     * @param refDataId the new ref data id
     */
    public void setRefDataId(int refDataId) {
        this.refDataId = refDataId;
    }

    /**
     * Gets the benef code.
     *
     * @return the benef code
     */
    public String getBenefCode() {
        return benefCode;
    }

    /**
     * Sets the benef code.
     *
     * @param benefCode the new benef code
     */
    public void setBenefCode(String benefCode) {
        this.benefCode = benefCode;
    }

    /**
     * Gets the mobile number.
     *
     * @return the mobile number
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Sets the mobile number.
     *
     * @param mobileNumber the new mobile number
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * Gets the mobile comm.
     *
     * @return the mobile comm
     */
    public String getMobileComm() {
        return mobileComm;
    }

    /**
     * Sets the mobile comm.
     *
     * @param mobileComm the new mobile comm
     */
    public void setMobileComm(String mobileComm) {
        this.mobileComm = mobileComm;
    }

    public void setMaternalStatus(long maternalStatus) {
        this.maternalStatus = maternalStatus;
    }

    public long getMaternalStatus() {
        return maternalStatus;
    }

    /**
     * Get current age from date of birth.
     *
     * @return Current age.<br>
     * <i>Exmple: 2m 3d (If age < 1). 2y 3m (If age < 10) else 20y</i>
     * @throws ParseException the parse exception
     */
    public String getDetailedAge() throws ParseException {
        return Utility.getAge(dob);
    }


    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setLmp(long lmp) {
        this.lmp = lmp;
    }

    public long getLmp() {
        return lmp;
    }

    /**
     * Gets the age in year.
     *
     * @return the age in year
     * @throws ParseException the parse exception
     */
    public String getAgeInYear() throws ParseException {
        return Utility.getAgeInYear(dob) + "";
    }

    /**
     * Gets the age in day.
     *
     * @return the age in day
     * @throws ParseException the parse exception
     */
    public String getAgeInDay() throws ParseException {
        return Utility.getAgeInDay(dob) + "";
    }

    public String getAgeInMonth() throws ParseException {
        return Utility.getAgeInMonth(dob) + "";
    }

    public String getAgeInHour() throws ParseException {
        return (Utility.getAgeInDay(dob) * 24) + "";
    }

    public long getPregnancyDurationInWeek() {
        if (lmp > 0) {
            return Utility.getNumberOfWeeksBetweenDate(lmp, Calendar.getInstance().getTimeInMillis());
        }
        return -1;
    }

    public long getPregnancyDurationInDay() {
        if (lmp > 0) {
            return Utility.getNumberOfDaysBetweenDate(lmp, Calendar.getInstance().getTimeInMillis());
        }
        return -1;
    }

    public String getOccupationHerHusband() {
        return occupationHerHusband;
    }

    public void setOccupationHerHusband(String occupationHerHusband) {
        this.occupationHerHusband = occupationHerHusband;
    }

    public String getMonthlyFamilyExpenditure() {
        return monthlyFamilyExpenditure;
    }

    public void setMonthlyFamilyExpenditure(String monthlyFamilyExpenditure) {
        this.monthlyFamilyExpenditure = monthlyFamilyExpenditure;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setRegFormPath(String regFormPath) {
        this.regFormPath = regFormPath;
    }

    public String getRegFormPath() {
        return regFormPath;
    }

    public void setBenefCodeFull(String benefCodeFull) {
        this.benefCodeFull = benefCodeFull;
    }

    public String getBenefCodeFull() {
        return benefCodeFull;
    }

    public void setCalculateLmp(long calculateLmp) {
        this.calculateLmp = calculateLmp;
    }

    public long getCalculateLmp() {
        return calculateLmp;
    }

    public void setMaxMaternalCompleteDate(long maxMaternalCompleteDate) {
        this.maxMaternalCompleteDate = maxMaternalCompleteDate;
    }

    public long getMaxMaternalCompleteDate() {
        return maxMaternalCompleteDate;
    }


    public String isStoredInServer() {
        if (!App.getContext().getDB().isBeneficiaryStoredInServer(benefCode)) {
            return "NO";
        }
        return "YES";
    }

    public long isBeneficiaryExist() {
        if (App.getContext().getDB().isBeneficiaryExist(benefCode)) {
            return 1;
        }
        return 0;
    }

    public void setHouseholdLocation(String householdLocation) {
        this.householdLocation = householdLocation;
    }

    public String getHouseholdLocation() {
        return householdLocation;
    }

//   Beneficiary.getLastServiceList(NUMBER_OF_SERVICE,FORMATTYPE)
//
//   Here  NUMBER_OF_SERVICE is either question (“q10” Answer of this question must be number) or  direct number (5) and    FORMATTYPE either JSON or XML.
//   Return :Recent service list that performed by this beneficiary. 
//   Return format :  Caption (INTERVIEW_START_TIME- QUESTIONNARY_TITLE ) and Value (TRANS_REF)
//
//   Example: Beneficiary.getLastServiceList(q10,JSON)
//                Beneficiary.getLastServiceList(5,JSON)


    public ArrayList<String> getLastServiceList(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (params.length == 2) {
            try {
                long numberOfservic = 0;
                String question = params[0];
                String formatType = params[1].trim();
                QuestionAnswer questionAnswer = questionAnswerMap.get(question);
                if (questionAnswer != null) {
                    numberOfservic = Utility.parseLong(questionAnswer.getAnswerList().get(0));
                } else {
                    numberOfservic = Utility.parseLong(question);
                }

                JSONArray jsonArray = App.getContext().getDB().getServiceList(benefCode, numberOfservic);
                if (jsonArray != null) {
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);
                        String caption = jsonObject.getString("TITLE");
                        String value = jsonObject.getString("VALUE");
                        arrayList.add(Utility.getFormatedData(caption, value, formatType));
                    }
                }
                return arrayList;
            } catch (Exception e) {
                arrayList = new ArrayList<String>();
                arrayList.add("ERROR");
                return arrayList;
            }
        } else {
            arrayList = new ArrayList<String>();
            arrayList.add("ERROR");
            return arrayList;
        }

    }

}
