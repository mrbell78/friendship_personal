package ngo.friendship.satellite.model;

import org.json.JSONObject;

import java.io.Serializable;

import ngo.friendship.satellite.constants.Column;

public class ImmunizationTarget implements Serializable {
    // Column(name = "BENEF_ID")
    private long benefId;
    // Column(name = "SESSION_DATE")
    private long sessionDate;
    //Column(name = "EVENT_ID")
    private Long eventId;
    // Column(name = "IMMU_TYPE")
    private String immuType;

    private String benefCode;
    public void setBenefCode(String benefCode) {
        this.benefCode = benefCode;
    }

    public String getBenefCode() {
        return benefCode;
    }
    
    public long getBenefId() {
        return benefId;
    }

    public void setBenefId(long benefId) {
        this.benefId = benefId;
    }

    public long getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(long sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getImmuType() {
        return immuType;
    }

    public void setImmuType(String immuType) {
        this.immuType = immuType;
    }
    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(Column.BENEF_ID, benefId);
            jSONObject.put(Column.SESSION_DATE, sessionDate);
            jSONObject.put(Column.EVENT_ID, eventId);
            jSONObject.put(Column.IMMU_TYPE, immuType);
        } catch (Exception exception) {
        }
        return jSONObject;
    }
    
    
    
    
    public static final String MODEL_NAME="immunization_target";
    
    
}
