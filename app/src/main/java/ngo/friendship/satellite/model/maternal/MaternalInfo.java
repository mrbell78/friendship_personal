package ngo.friendship.satellite.model.maternal;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.QuestionAnswer;
import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.utility.Utility;


/**
 *
 * @author Mohammed Jubayer
 * 
 * Created Date: 13/1/2014
 * Last Update: 13/1/2014
 * 
 */


// Table(name = "maternal_info")
public class MaternalInfo extends Beneficiary  implements Serializable{
	// Column(name = "MATERNAL_ID")
    private long maternalId;
    // Column(name = "LMP"
   // private long lmp; on beneficiary class
    // Column(name = "EDD")
    private long edd;
    // Column(name = "PARA")
    private Long para;
    // Column(name = "GRAVIDA")
    private Long gravida;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    // Column(name = "BMI_VALUE")
    private Double bmiValue;
    // Column(name = "BMI")
    private String bmi;
    // Column(name = "REG_INTERVIEW_ID")
    private long regInterviewId;
    // Column(name = "NO_OF_RISK_ITEM")
    private Long noOfRiskItem;
    // Column(name = "HIGH_RISK_INTERVIEW_ID")
    private Long highRiskInterviewId;
    // Column(name = "CREATE_DATE")
    private long createDateMaternal;
    // Column(name = "MATERNAL_STATUS")
    private long maternalStatus;

    // HEIGHT_IN_CM
    private Long heightInCm;
    
    
    private String lastserviceName;
  
    
    private long completeDate;
    private long completeSource;

    public long getMaternalId() {
		return maternalId;
	}
	public void setMaternalId(long maternalId) {
		this.maternalId = maternalId;
	}
	public long getEdd() {
		return edd;
	}
	public void setEdd(long edd) {
		this.edd = edd;
	}
	public Long getPara() {
		return para;
	}
	public void setPara(Long para) {
		this.para = para;
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
	public String getBmi() {
		return bmi;
	}
	public void setBmi(String bmi) {
		this.bmi = bmi;
	}
	public long getRegInterviewId() {
		return regInterviewId;
	}
	public void setRegInterviewId(long regInterviewId) {
		this.regInterviewId = regInterviewId;
		if(userScheduleInfos!=null){
			for (UserScheduleInfo us : userScheduleInfos) {
				us.setInterviewId(regInterviewId);
			}
		}
	}
	public Long getNoOfRiskItem() {
		return noOfRiskItem;
	}
	public void setNoOfRiskItem(Long noOfRiskItem) {
		this.noOfRiskItem = noOfRiskItem;
	}
	public Long getHighRiskInterviewId() {
		return highRiskInterviewId;
	}
	public void setHighRiskInterviewId(Long highRiskInterviewId) {
		this.highRiskInterviewId = highRiskInterviewId;
	}
	
	public long getMaternalStatus() {
		return maternalStatus;
	}
	public void setMaternalStatus(long maternalStatus) {
		this.maternalStatus = maternalStatus;
	}

	public void setLastserviceName(String lastserviceName) {
		this.lastserviceName = lastserviceName;
	}
	public String getLastserviceName() {
		return lastserviceName;
	}

	private ArrayList<MaternalBabyInfo> maternalBabyInfos;
    private HashMap <String , MaternalService> maternalServices;
    private MaternalDelivery maternalDelivery;
    private ArrayList<UserScheduleInfo> userScheduleInfos;
    
    public void setMaternalBabyInfos(
			ArrayList<MaternalBabyInfo> maternalBabyInfos) {
		this.maternalBabyInfos = maternalBabyInfos;
	}
    public ArrayList<MaternalBabyInfo> getMaternalBabyInfos() {
		return maternalBabyInfos;
	}
    public HashMap<String, MaternalService> getMaternalServices() {
        return maternalServices;
    }

    public void setMaternalServices(HashMap<String, MaternalService> maternalServices) {
        this.maternalServices = maternalServices;
    }

    public MaternalDelivery getMaternalDelivery() {
        return maternalDelivery;
    }

    public void setMaternalDelivery(MaternalDelivery maternalDelivery) {
        this.maternalDelivery = maternalDelivery;
    }
  
    public MaternalService getMaternalService(String key) {
        if( maternalServices==null) return null;
        return maternalServices.get(key);
    }
    
    public String getRecentMaternalServiceKey() {
        if( maternalServices==null) return "";
        if(maternalServices.containsKey("PNC4"))return "PNC4";
        if(maternalServices.containsKey("PNC3"))return "PNC3";
        if(maternalServices.containsKey("PNC2"))return "PNC2";
        if(maternalServices.containsKey("PNC1"))return "PNC1";
        if(maternalServices.containsKey("ANC4"))return "ANC4";
        if(maternalServices.containsKey("ANC3"))return "ANC3";
        if(maternalServices.containsKey("ANC2"))return "ANC2";
        if(maternalServices.containsKey("ANC1"))return "ANC1";
        if(maternalServices.containsKey("PHC" ))return "PHC";
        else return "";
    }
    public void setUserScheduleInfos(
			ArrayList<UserScheduleInfo> userScheduleInfos) {
		this.userScheduleInfos = userScheduleInfos;
	}
    public ArrayList<UserScheduleInfo> getUserScheduleInfos() {
		return userScheduleInfos;
	}
    
    public long getCompleteDate() {
		return completeDate;
	}
    public void setCompleteDate(long completeDate) {
		this.completeDate = completeDate;
	}
    
    public void setCompleteSource(long completeSource) {
		this.completeSource = completeSource;
	}
    public long getCompleteSource() {
		return completeSource;
	}
    public void setCreateDateMaternal(long createDateMaternal) {
		this.createDateMaternal = createDateMaternal;
	}
    public long getCreateDateMaternal() {
		return createDateMaternal;
	}
    
    //Beneficiary.getWeightGainPerWeek(q5)
    //Beneficiary.getWeightGainPerWeek(q18)
  	public double getWeightGainPerWeek(String[] params,HashMap<String, QuestionAnswer> questionAnswerMap){
  		
  		if(params.length==1){
  			QuestionAnswer questionAnswer = questionAnswerMap.get(params[0].trim());
  			double currentWeight=0.0;
  			try{
  				currentWeight=Utility.parseDouble(questionAnswer.getAnswerList().get(0)); 
  			}catch(Exception ex){
  				
  			}
  			if(currentWeight > 0 && maternalServices!=null){
  			
  				for (MaternalService ms :  Utility.sortMaternalService(maternalServices)) {
					if(ms.getWeight()>0){
					    long lastWeek = Utility.getNumberOfWeeksBetweenDate(getLmp(),ms.getCreateDate());
					    long currentweek=Utility.getNumberOfWeeksBetweenDate(getLmp(),Calendar.getInstance().getTimeInMillis());
					    long weekDif=0;
						if(lastWeek<12){
							weekDif=currentweek-12;			
						}else{
							weekDif=currentweek-lastWeek;
						}
						double weightDif=0.0;
						weightDif=currentWeight-ms.getWeight();
						if(weekDif>0){
						 return	 Utility.parseDouble(new DecimalFormat("##.##").format(weightDif/weekDif));
						}
						break;
					}
				}
  			    
  			}
  		}
  		return 0;
  	}

  	public Object getMaternalDelivery(String property) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
  			return maternalDelivery.getClass().getMethod(property, null).invoke(maternalDelivery,null);
  	}
  	public Object getMaternalBabyInfos(String number ,String property) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		int position= Integer.parseInt(number);
  		MaternalBabyInfo babyInfo =maternalBabyInfos.get(position-1); 	
  		return babyInfo.getClass().getMethod(property, null).invoke(babyInfo,null);
	}

  	public void setHeightInCm(Long heightInCm) {
		this.heightInCm = heightInCm;
	}
  	public Long getHeightInCm() {
		return heightInCm;
	}
  	
  	
    //TRANS_REF
    private long transRef;

    public void setTransRef(long transRef) {
        this.transRef = transRef;
    }

    public long getTransRef() {
        return transRef;
    }
   
	public static final String MODEL_NAME="maternal_info";
	 
	  

}
