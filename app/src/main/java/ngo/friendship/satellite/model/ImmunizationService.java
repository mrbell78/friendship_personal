package ngo.friendship.satellite.model;

import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Date;

import ngo.friendship.satellite.constants.Column;
//table name immunization_service

public class ImmunizationService implements Serializable{

    //Column(name = "IMMU_SERV_ID")
    private long immuServId;
    //Column(name = "IMMU_DATE")
    private long immuDate;
    //Column(name = "AGE_DAY_WHEN_IMMUNIZED")
    private long ageDayWhenImmunized;


    //Column(name = "IMMU_TYPE")
    private String immuType;
    //Column(name = "INTERVIEW_ID")
    private long interviewId;
    //JoinColumn(name = "IMMU_ID", referencedColumnName = "IMMU_ID")
    private long immuId;
    //JoinColumn(name = "BENEF_ID", referencedColumnName = "BENEF_ID")

    //ManyToOne
    private long benefId;
    
    
    // 
    private boolean complete; 
    private String completeReason;
    private Date nextImmuDate;
    
    
    private String benefCode;
    public void setBenefCode(String benefCode) {
        this.benefCode = benefCode;
    }

    public String getBenefCode() {
        return benefCode;
    }
    public long getImmuServId() {
        return immuServId;
    }

    public void setImmuServId(long immuServId) {
        this.immuServId = immuServId;
    }

    public long getImmuDate() {
        return immuDate;
    }

    public void setImmuDate(long immuDate) {
        this.immuDate = immuDate;
    }

    public long getAgeDayWhenImmunized() {
        return ageDayWhenImmunized;
    }

    public void setAgeDayWhenImmunized(long ageDayWhenImmunized) {
        this.ageDayWhenImmunized = ageDayWhenImmunized;
    }

    public Date getNextImmuDate() {
        return nextImmuDate;
    }

    public void setNextImmuDate(Date nextImmuDate) {
        this.nextImmuDate = nextImmuDate;
    }

    public String getImmuType() {
        return immuType;
    }

    public void setImmuType(String immuType) {
        this.immuType = immuType;
    }

    public long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(long interviewId) {
        this.interviewId = interviewId;
    }

    public long getImmuId() {
        return immuId;
    }

    public void setImmuId(long immuId) {
        this.immuId = immuId;
    }

    public long getBenefId() {
        return benefId;
    }

    public void setBenefId(long benefId) {
        this.benefId = benefId;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setCompleteReason(String completeReason) {
        this.completeReason = completeReason;
    }

    public String getCompleteReason() {
        return completeReason;
    }
    
    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(Column.IMMU_SERV_ID, immuServId);
            jSONObject.put(Column.IMMU_DATE, immuDate);
            jSONObject.put(Column.AGE_DAY_WHEN_IMMUNIZED, ageDayWhenImmunized);
            jSONObject.put(Column.IMMU_TYPE, immuType);
            jSONObject.put(Column.INTERVIEW_ID, interviewId);
            jSONObject.put(Column.IMMU_ID, immuId);
            
        } catch (Exception exception) {
        }
        return jSONObject;
    }
    
    //TRANS_REF
    private long transRef;

    public void setTransRef(long transRef) {
        this.transRef = transRef;
    }

    public long getTransRef() {
        return transRef;
    }
    
    public static final String MODEL_NAME = "immunization_service";

}
