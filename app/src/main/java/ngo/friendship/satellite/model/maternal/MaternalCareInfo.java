package ngo.friendship.satellite.model.maternal;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Mohammed Jubayer
 *
 * Created Date: 13/1/2014 Last Update: 13/1/2014
 *
 */
// Table(name = "maternal_care_info")
public class MaternalCareInfo implements Serializable{

    // Column(name = "MATERNAL_CARE_ID")
    private long maternalCareId;
    // Column(name = "CARE_NAME")
    private String careName;
    // Column(name = "CARE_DESC")
    private String careDesc;
    // Column(name = "CARE_TYPE")
    private String careType;
    // Column(name = "SCHED_RANGE_START_DAY")
    private long schedRangeStartDay;
    // Column(name = "SCHED_RANGE_END_DAY")
    private long schedRangeEndDay;
    //Column(name = "QUESTIONNAIRE_ID")
    private long questionnaireId;
    
    private Date date;

    // OneToMany(cascade = CascadeType.ALL, mappedBy = "maternalCareId")
    public long getMaternalCareId() {
        return maternalCareId;
    }

    public void setMaternalCareId(long maternalCareId) {
        this.maternalCareId = maternalCareId;
    }

    public String getCareName() {
        return careName;
    }

    public void setCareName(String careName) {
        this.careName = careName;
    }

    public String getCareDesc() {
        return careDesc;
    }

    public void setCareDesc(String careDesc) {
        this.careDesc = careDesc;
    }

    public String getCareType() {
        return careType;
    }

    public void setCareType(String careType) {
        this.careType = careType;
    }

    public long getSchedRangeStartDay() {
        return schedRangeStartDay;
    }

    public void setSchedRangeStartDay(long schedRangeStartDay) {
        this.schedRangeStartDay = schedRangeStartDay;
    }

    public long getSchedRangeEndDay() {
        return schedRangeEndDay;
    }

    public void setSchedRangeEndDay(long schedRangeEndDay) {
        this.schedRangeEndDay = schedRangeEndDay;
    }

    public void setQuestionnaireId(long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public long getQuestionnaireId() {
        return questionnaireId;
    }
    
    public void setDate(Date date) {
		this.date = date;
	}
    public Date getDate() {
		return date;
	}
    

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("MATERNAL_CARE_ID", maternalCareId);
            jSONObject.put("CARE_NAME", careName);
            jSONObject.put("CARE_DESC", careDesc);
            jSONObject.put("CARE_TYPE", careType);
            jSONObject.put("QUESTIONNAIRE_ID", questionnaireId);
            jSONObject.put("SCHED_RANGE_START_DAY", schedRangeStartDay);
            jSONObject.put("SCHED_RANGE_END_DAY", schedRangeEndDay);
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
    
    
    public static final String MODEL_NAME = "maternal_care_info";
    
    
    
}
