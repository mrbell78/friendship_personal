package ngo.friendship.satellite.model;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import ngo.friendship.satellite.constants.Column;

// TODO: Auto-generated Javadoc

/**
 * The Class ImmunizableBeneficiary.
 */
public class ImmunizableBeneficiary extends Beneficiary implements Serializable{

	
	// Column(name = "IMMU_BENEF_ID")
    private long immuBenefId;
    //Column(name = "IMMU_TYPE")
    private String immuType;
    //Column(name = "AGE_DAY_WHEN_DETECTED")
    private long ageDayWhenDetected;
   //Column(name = "IMMU_COMPLETE_DATE")
    private long immuCompleteDate;
    //JoinColumn(name = "BENEF_ID", referencedColumnName = "BENEF_ID")
    private long benefId;
    //JoinColumn(name = "REASON_ID", referencedColumnName = "REASON_ID")
    private Long reasonId;

		
	public long getImmuBenefId() {
		return immuBenefId;
	}

	public void setImmuBenefId(long immuBenefId) {
		this.immuBenefId = immuBenefId;
	}

	public String getImmuType() {
		return immuType;
	}

	public void setImmuType(String immuType) {
		this.immuType = immuType;
	}

	public long getAgeDayWhenDetected() {
		return ageDayWhenDetected;
	}

	public void setAgeDayWhenDetected(long ageDayWhenDetected) {
		this.ageDayWhenDetected = ageDayWhenDetected;
	}


	public long getImmuCompleteDate() {
		return immuCompleteDate;
	}

	public void setImmuCompleteDate(long immuCompleteDate) {
		this.immuCompleteDate = immuCompleteDate;
	}

	public long getBenefId() {
		return benefId;
	}

	public void setBenefId(long benefId) {
		this.benefId = benefId;
	}

	public Long getReasonId() {
		return reasonId;
	}

	public void setReasonId(Long reasonId) {
		this.reasonId = reasonId;
	}

	public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(Column.IMMU_BENEF_ID, benefId);
            jSONObject.put(Column.IMMU_TYPE,immuType);
            jSONObject.put(Column.AGE_DAY_WHEN_DETECTED,ageDayWhenDetected);
            jSONObject.put(Column.CREATE_DATE,getCreateDate());
            jSONObject.put(Column.IMMU_COMPLETE_DATE, immuCompleteDate);
            jSONObject.put(Column.BENEF_ID, benefId);
            jSONObject.put(Column.REASON_ID, reasonId);
        } catch (Exception exception) {
        }
        return jSONObject;
    }
    
    
    public static final String MODEL_NAME="immunizable_beneficiary";

    
    private ArrayList<ImmunizationInfo> immunizationlist;
	private String nextImmunizationDateStr;
	private long nextImmunizationDateInMillis;
	private String immunizationCompleteReasonName;
	private String immunizationMissReasonName;
	private String immunizationMissReasonCaption;

	public String getImmunizationCompleteReasonName() {
		return immunizationCompleteReasonName;
	}
	
	public void setImmunizationCompleteReasonName(
			String immunizationCompleteReasonName) {
		this.immunizationCompleteReasonName = immunizationCompleteReasonName;
	}
	
	
	public String getImmunizationMissReasonName() {
		return immunizationMissReasonName;
	}
	
	
	public void setImmunizationMissReasonName(String immunizationMissReasonName) {
		this.immunizationMissReasonName = immunizationMissReasonName;
	}

	public String getImmunizationMissReasonCaption() {
		return immunizationMissReasonCaption;
	}
	
	
	public void setImmunizationMissReasonCaption(
			String immunizationMissReasonCaption) {
		this.immunizationMissReasonCaption = immunizationMissReasonCaption;
	}
	
	
	public void setImmunizationlist(ArrayList<ImmunizationInfo> immunizationlist) {
		this.immunizationlist = immunizationlist;
	}
	

	public ArrayList<ImmunizationInfo> getImmunizationlist() {
		return immunizationlist;
	}
	public void setNextImmunizationDateInMillis(long nextImmunizationDateInMillis) {
		this.nextImmunizationDateInMillis = nextImmunizationDateInMillis;
	}
	public long getNextImmunizationDateInMillis() {
		return nextImmunizationDateInMillis;
	}
	
	public String getNextImmunizationDateStr() {
		return nextImmunizationDateStr;
	}

	public void setNextImmunizationDateStr(String nextImmunizationDateStr) {
		this.nextImmunizationDateStr = nextImmunizationDateStr;
	}



}
