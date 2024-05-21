package ngo.friendship.satellite.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ngo.friendship.satellite.error.ErrorCode;
import ngo.friendship.satellite.error.MhealthException;


public class Couple implements Serializable {

    private long interviewId;
    private long coupleId;
    private String coupleCode;
    private String hhNumber;
    private String husbandBenefName;
    private String wifeBenefName;

    private String husbandBenefAge;
    private String wifeBenefAge;
    private long hhId;
    private long benefId;
    private String benefCode;
    private String husbandBenefCode;
    private String ageFirstPregnacy;
    private String wifeBenefCode;
    private String marriageDate;
    private String ttInfo;
    private String livingChildren;
    private int state;
    private int sent;

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public String getCoupleCode() {
        return coupleCode;
    }

    public void setCoupleCode(String coupleCode) {
        this.coupleCode = coupleCode;
    }

    public long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(long interviewId) {
        this.interviewId = interviewId;
    }

    public String getHusbandBenefAge() {
        return husbandBenefAge;
    }

    public void setHusbandBenefAge(String husbandBenefAge) {
        this.husbandBenefAge = husbandBenefAge;
    }

    public String getWifeBenefAge() {
        return wifeBenefAge;
    }

    public void setWifeBenefAge(String wifeBenefAge) {
        this.wifeBenefAge = wifeBenefAge;
    }

    public String getHusbandBenefName() {
        return husbandBenefName;
    }

    public void setHusbandBenefName(String husbandBenefName) {
        this.husbandBenefName = husbandBenefName;
    }

    public String getWifeBenefName() {
        return wifeBenefName;
    }

    public void setWifeBenefName(String wifeBenefName) {
        this.wifeBenefName = wifeBenefName;
    }

    public long getCoupleId() {
        return coupleId;
    }

    public void setCoupleId(long coupleId) {
        this.coupleId = coupleId;
    }

    public String getHhNumber() {
        return hhNumber;
    }

    public void setHhNumber(String hhNumber) {
        this.hhNumber = hhNumber;
    }

    public String getAgeFirstPregnacy() {
        return ageFirstPregnacy;
    }

    public void setAgeFirstPregnacy(String ageFirstPregnacy) {
        this.ageFirstPregnacy = ageFirstPregnacy;
    }

    public long getHhId() {
        return hhId;
    }

    public void setHhId(long hhId) {
        this.hhId = hhId;
    }

    public long getBenefId() {
        return benefId;
    }

    public void setBenefId(long benefId) {
        this.benefId = benefId;
    }

    public String getBenefCode() {
        return benefCode;
    }

    public void setBenefCode(String benefCode) {
        this.benefCode = benefCode;
    }

    public String getHusbandBenefCode() {
        return husbandBenefCode;
    }

    public void setHusbandBenefCode(String husbandBenefCode) {
        this.husbandBenefCode = husbandBenefCode;
    }

    public String getWifeBenefCode() {
        return wifeBenefCode;
    }

    public void setWifeBenefCode(String wifeBenefCode) {
        this.wifeBenefCode = wifeBenefCode;
    }

    public String getMarriageDate() {
        return marriageDate;
    }

    public void setMarriageDate(String marriageDate) {
        this.marriageDate = marriageDate;
    }

    public String getTtInfo() {
        return ttInfo;
    }

    public void setTtInfo(String ttInfo) {
        this.ttInfo = ttInfo;
    }

    public String getLivingChildren() {
        return livingChildren;
    }

    public void setLivingChildren(String livingChildren) {
        this.livingChildren = livingChildren;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public JSONObject toJson() throws MhealthException {

        try {
            JSONObject jHouseholdInfoObj = new JSONObject();
            jHouseholdInfoObj.put("COUPLE_ID", getCoupleId());
            jHouseholdInfoObj.put("COUPLE_CODE", getCoupleCode());
            jHouseholdInfoObj.put("HUSBAND_BENEF_CODE", getHusbandBenefCode());
            jHouseholdInfoObj.put("HUSBAND_BENEF_NAME", getHusbandBenefName());
            jHouseholdInfoObj.put("WIFE_BENEF_CODE", getWifeBenefCode());
            jHouseholdInfoObj.put("WIFE_BENEF_NAME", getWifeBenefName());
            jHouseholdInfoObj.put("MARRIAGE_DATE", getMarriageDate());
            jHouseholdInfoObj.put("TT_INFO", getTtInfo());
            jHouseholdInfoObj.put("LIVING_CHILDREN", getLivingChildren());
            jHouseholdInfoObj.put("AGE_FIRST_PREGNANCY", getAgeFirstPregnacy());
            jHouseholdInfoObj.put("BENEF_CODE", getBenefCode());
            jHouseholdInfoObj.put("BENEF_ID", getBenefId());
            jHouseholdInfoObj.put("WIFE_DOB", getWifeBenefAge());
            jHouseholdInfoObj.put("HUSBAND_DOB", getHusbandBenefAge());
            jHouseholdInfoObj.put("STATE", getState());
            return jHouseholdInfoObj;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON_EXCEPTION");
        }

    }

    public static final String MODEL_NAME = "couple_registration";


}
