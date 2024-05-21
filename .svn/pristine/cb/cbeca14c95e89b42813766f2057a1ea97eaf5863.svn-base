package ngo.friendship.satellite.model;

import org.json.JSONObject;

import java.io.Serializable;

import ngo.friendship.satellite.constants.Column;


// Table(name = "immunization_followup")
public class ImmunizationFollowup implements Serializable {
    //Column(name = "IMMU_FOLLOWUP_ID")
    private long immuFollowupId;
    //Column(name = "BENEF_ID")
    private long benefId;
    //Column(name = "CREATE_DATE")
    private long createDate;
    //Column(name = "REASON_ID")
    private Long reasonId;
    //Column(name = "INTERVIEW_ID")
    private long interviewId;
    //Column(name = "IMMU_TYPE")
    private String immuType;
    //Column(name = "FOLLOWUP_TYPE")
    private long followupType;
    
    private long followupDate;

    
    private String benefCode;
    public void setBenefCode(String benefCode) {
        this.benefCode = benefCode;
    }

    public String getBenefCode() {
        return benefCode;
    }
    
    public long getImmuFollowupId() {
        return immuFollowupId;
    }

    public void setImmuFollowupId(long immuFollowupId) {
        this.immuFollowupId = immuFollowupId;
    }

    public long getBenefId() {
        return benefId;
    }

    public void setBenefId(long benefId) {
        this.benefId = benefId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getReasonId() {
        return reasonId;
    }

    public void setReasonId(long reasonId) {
        this.reasonId = reasonId;
    }

    public long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(long interviewId) {
        this.interviewId = interviewId;
    }

    public String getImmuType() {
        return immuType;
    }

    public void setImmuType(String immuType) {
        this.immuType = immuType;
    }

    public void setFollowupType(long followupType) {
        this.followupType = followupType;
    }

    public long getFollowupType() {
        return followupType;
    }

    public void setFollowupDate(long followupDate) {
        this.followupDate = followupDate;
    }

    public long getFollowupDate() {
        return followupDate;
    }
    
    
    
      public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(Column.IMMU_FOLLOWUP_ID, immuFollowupId);
            jSONObject.put(Column.BENEF_ID,benefId);
            jSONObject.put(Column.CREATE_DATE,createDate);
            jSONObject.put(Column.REASON_ID, reasonId);
            jSONObject.put(Column.INTERVIEW_ID, interviewId);
            jSONObject.put(Column.IMMU_TYPE, immuType);
            jSONObject.put(Column.FOLLOWUP_TYPE, followupType);
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
     

      
      
    public static final String MODEL_NAME = "immunization_followup";
    
   
    
}
