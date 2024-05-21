package ngo.friendship.satellite.model.maternal;

import java.io.Serializable;

public class MaternalService implements Serializable {

    // Column(name = "MATERNAL_SERVICE_ID")
    private long maternalServiceId;
    //Column(name = "BMI_VALUE")
    private Double bmiValue;
    //Column(name = "BMI")
    private String bmi;
    //Column(name = "PULSE")
    private Long pulse;
  
    //Column(name = "PULSE_STATUS")
    private String pulseStatus;
    
    
    // Column(name = "BLOOD_PRESSURE")
    private String bloodPressure;
    // Column(name = "BLOOD_PRESSURE_TYPE")
    private String bloodPressureType;
    // Column(name = "TEMPERATURE")
    private Double temperature;
    
    // Column(name = "TEMPERATURE_TYPE")
    private String temperatureType;
    
    // Column(name = "WEIGHT")
    private Double weight;
    // Column(name = "WEEKLY_WEIGHT_GAIN")
    private Double weeklyWeightGain;
    // Column(name = "ANAEMIA")
    private String anaemia;
    // Column(name = "JAUNDICE")
    private String jaundice;
    // Column(name = "OEDEMA")
    private String oedema;
    // Column(name = "VOMITING")
    private String vomiting;
    // Column(name = "SUGAR_OF_URINE")
    private String sugarOfUrine;
    //Column(name = "PROTEIN_OF_URINE")
    private String proteinOfUrine;
    //Column(name = "HEIGHT_OF_UTERUS")
    private String heightOfUterus;
    // Column(name = "FETAL_MOVEMENT")
    private String fetalMovement;
    // Column(name = "FETAL_HEART_RATE")
    private String fetalHeartRate;
    // Column(name = "FETAL_LIE")
    private String fetalLie;
    // Column(name = "FETAL_PRESENTATION")
    private String fetalPresentation;
    // Column(name = "BREAST_PROBLEM")
    private String breastProblem;
    // Column(name = "INTERVIEW_ID")
    private long interviewId;
    // Column(name = "CREATE_DATE")
    private long createDate;
    // Column(name = "MATERENAL_STATUS")
    private String materenalStatus;
    // Column(name = "VITAMIN_A")
    private String vitaminA;
    // Column(name = "RISK_STATE")
    private String riskState;
    // Column(name = "RISK_PROP")
    private Long riskProp;
    // JoinColumn(name = "BENEF_ID", referencedColumnName = "BENEF_ID")
    private long benefId;
    // JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    private long userId;
    // JoinColumn(name = "MATERNAL_CARE_ID", referencedColumnName = "MATERNAL_CARE_ID")
    private long maternalCareId;
    // JoinColumn(name = "MATERNAL_ID", referencedColumnName = "MATERNAL_ID")
    private long maternalId;
    private Long heightInCm;
    
    private String benefCode;
    private String careName;
    private long lmp;
    
    
    private long status=-1;
    
    public long getMaternalServiceId() {
        return maternalServiceId;
    }

    public void setMaternalServiceId(long maternalServiceId) {
        this.maternalServiceId = maternalServiceId;
    }

    public Double getBmiValue() {
        return bmiValue;
    }

    public void setBmiValue(Double bmiValue) {
        this.bmiValue = bmiValue;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public Long getPulse() {
        return pulse;
    }

    public void setPulse(Long pulse) {
        this.pulse = pulse;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getBloodPressureType() {
        return bloodPressureType;
    }

    public void setBloodPressureType(String bloodPressureType) {
        this.bloodPressureType = bloodPressureType;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeeklyWeightGain() {
        return weeklyWeightGain;
    }

    public void setWeeklyWeightGain(Double weeklyWeightGain) {
        this.weeklyWeightGain = weeklyWeightGain;
    }

    public String getAnaemia() {
        return anaemia;
    }

    public void setAnaemia(String anaemia) {
        this.anaemia = anaemia;
    }

    public String getJaundice() {
        return jaundice;
    }

    public void setJaundice(String jaundice) {
        this.jaundice = jaundice;
    }

    public String getOedema() {
        return oedema;
    }

    public void setOedema(String oedema) {
        this.oedema = oedema;
    }

    public String getVomiting() {
        return vomiting;
    }

    public void setVomiting(String vomiting) {
        this.vomiting = vomiting;
    }

    public String getSugarOfUrine() {
        return sugarOfUrine;
    }

    public void setSugarOfUrine(String sugarOfUrine) {
        this.sugarOfUrine = sugarOfUrine;
    }

    public String getProteinOfUrine() {
        return proteinOfUrine;
    }

    public void setProteinOfUrine(String proteinOfUrine) {
        this.proteinOfUrine = proteinOfUrine;
    }

    public String getHeightOfUterus() {
        return heightOfUterus;
    }

    public void setHeightOfUterus(String heightOfUterus) {
        this.heightOfUterus = heightOfUterus;
    }

    public String getFetalMovement() {
        return fetalMovement;
    }

    public void setFetalMovement(String fetalMovement) {
        this.fetalMovement = fetalMovement;
    }

    public String getFetalHeartRate() {
        return fetalHeartRate;
    }

    public void setFetalHeartRate(String fetalHeartRate) {
        this.fetalHeartRate = fetalHeartRate;
    }

    public String getFetalLie() {
        return fetalLie;
    }

    public void setFetalLie(String fetalLie) {
        this.fetalLie = fetalLie;
    }

    public String getFetalPresentation() {
        return fetalPresentation;
    }

    public void setFetalPresentation(String fetalPresentation) {
        this.fetalPresentation = fetalPresentation;
    }

    

    public long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(long interviewId) {
        this.interviewId = interviewId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getMaterenalStatus() {
        return materenalStatus;
    }

    public void setMaterenalStatus(String materenalStatus) {
        this.materenalStatus = materenalStatus;
    }

    public String getVitaminA() {
        return vitaminA;
    }

    public void setVitaminA(String vitaminA) {
        this.vitaminA = vitaminA;
    }

    public String getRiskState() {
        return riskState;
    }

    public void setRiskState(String riskState) {
        this.riskState = riskState;
    }

    public Long getRiskProp() {
        return riskProp;
    }

    public void setRiskProp(Long riskProp) {
        this.riskProp = riskProp;
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

    public long getMaternalCareId() {
        return maternalCareId;
    }

    public void setMaternalCareId(long maternalCareId) {
        this.maternalCareId = maternalCareId;
    }

    public long getMaternalId() {
        return maternalId;
    }

    public void setMaternalId(long maternalId) {
        this.maternalId = maternalId;
    }
    
    public String getCareName() {
		return careName;
	}
    public void setCareName(String careName) {
		this.careName = careName;
	}
    
    public void setBenefCode(String benefCode) {
		this.benefCode = benefCode;
	}
    public String getBenefCode() {
		return benefCode;
	}
    public void setLmp(long lmp) {
		this.lmp = lmp;
	}
    public long getLmp() {
		return lmp;
	}
    public void setBreastProblem(String breastProblem) {
		this.breastProblem = breastProblem;
	}
    public String getBreastProblem() {
		return breastProblem;
	}
    public void setHeightInCm(Long heightInCm) {
		this.heightInCm = heightInCm;
	}
    public Long getHeightInCm() {
		return heightInCm;
	}
    public void setPulseStatus(String pulseStatus) {
		this.pulseStatus = pulseStatus;
	}
    public String getPulseStatus() {
		return pulseStatus;
	}
    public void setTemperatureType(String temperatureType) {
		this.temperatureType = temperatureType;
	}
    public String getTemperatureType() {
		return temperatureType;
	}
    
    public void setStatus(long status) {
		this.status = status;
	}
    public long getStatus() {
		return status;
	}
    
    //TRANS_REF
    private long transRef;

    public void setTransRef(long transRef) {
        this.transRef = transRef;
    }

    public long getTransRef() {
        return transRef;
    }
    public static final String TRANS_REF = "TRANS_REF";
    public static final String MODEL_NAME = "maternal_service"; 
   
    
}
