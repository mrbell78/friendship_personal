package ngo.friendship.satellite.model.maternal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.utility.Utility;
//Table(name = "maternal_delivery")

public class MaternalDelivery  implements Serializable{

	
	       // Column(name = "MATERNAL_DELIVERY_ID")
			private long maternalDeliveryId;
			// Column(name = "MATERNAL_ID")
			private long maternalId;
			// Column(name = "MOTHER_CONDITION")
			private String motherCondition;
			// Column(name = "DELIVERY_DATE")
			private Long deliveryDate;
			// Column(name = "DELIVERY_TIME")
			private String deliveryTime;
			// Column(name = "DELIVERY_PLACE")
			private String deliveryPlace;
			// Column(name = "DELIVERED_BY_CSBA")
			private String deliveredByCsba;
			// Column(name = "DELIVERY_TYPE")
			private String deliveryType;
			// Column(name = "PERSON_DELIVERED")
			private String personDelivered;
			// Column(name = "NO_OF_BABY")
			private Long noOfBaby;
			// Column(name = "DELIVERY_CARE_INTERVIEW_ID")
			private Long deliveryCareInterviewId;

		    private ArrayList<UserScheduleInfo> userScheduleInfos;

			private String benefCode;
			private long lmp;
	

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

	private HashMap<String,MaternalBabyInfo> babyInfos;

	public long getMaternalDeliveryId() {
		return maternalDeliveryId;
	}

	public void setMaternalDeliveryId(long maternalDeliveryId) {
		this.maternalDeliveryId = maternalDeliveryId;
	}

	public long getMaternalId() {
		return maternalId;
	}

	public void setMaternalId(long maternalId) {
		this.maternalId = maternalId;
	}

	public String getMotherCondition() {
		return motherCondition;
	}

	public void setMotherCondition(String motherCondition) {
		this.motherCondition = motherCondition;
	}

	public Long getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Long deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getDeliveryPlace() {
		return deliveryPlace;
	}

	public void setDeliveryPlace(String deliveryPlace) {
		this.deliveryPlace = deliveryPlace;
	}

	public String getDeliveredByCsba() {
		return deliveredByCsba;
	}

	public void setDeliveredByCsba(String deliveredByCsba) {
		this.deliveredByCsba = deliveredByCsba;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getPersonDelivered() {
		return personDelivered;
	}

	public void setPersonDelivered(String personDelivered) {
		this.personDelivered = personDelivered;
	}

	public Long getNoOfBaby() {
		return noOfBaby;
	}

	public void setNoOfBaby(Long noOfBaby) {
		this.noOfBaby = noOfBaby;
	}

	public Long getDeliveryCareInterviewId() {
		return deliveryCareInterviewId;
	}

	public void setDeliveryCareInterviewId(Long deliveryCareInterviewId) {
		this.deliveryCareInterviewId = deliveryCareInterviewId;
	}

	public void setBabyInfos(HashMap<String, MaternalBabyInfo> babyInfos) {
		this.babyInfos = babyInfos;
	}
	public HashMap<String, MaternalBabyInfo> getBabyInfos() {
		return babyInfos;
	}
    public void setUserScheduleInfos(
			ArrayList<UserScheduleInfo> userScheduleInfos) {
		this.userScheduleInfos = userScheduleInfos;
	}
    public ArrayList<UserScheduleInfo> getUserScheduleInfos() {
		return userScheduleInfos;
	}
	
    
    //TRANS_REF
    private long transRef;

    public void setTransRef(long transRef) {
        this.transRef = transRef;
    }

    public long getTransRef() {
        return transRef;
    }
   
	public static final String MODEL_NAME="maternal_delivery";
	

	
	public  long getPostDeliveryDurationInDay(){
		if (deliveryDate > 0) {
			return Utility.getNumberOfDaysBetweenDate(deliveryDate,Calendar.getInstance().getTimeInMillis());
		}
		return -1;
	}
	public  long getPostDeliveryDurationInWeek(){
		if (deliveryDate > 0) {
			return Utility.getNumberOfWeeksBetweenDate(deliveryDate,Calendar.getInstance().getTimeInMillis());
		}
		return -1;
	}
	public  long getPostDeliveryDurationInHour(){
		try{
			if (deliveryDate > 0 && deliveryTime.length()>0) {
				long hh=Utility.parseLong(deliveryTime.split(":")[0]);
				hh=hh*60*60*1000;
				
				long mm=Utility.parseLong(deliveryTime.split(":")[1]);
				mm=mm*60*1000;
				
			    deliveryDate=deliveryDate+(hh+mm);
				
				
				return Utility.getNumberOfHoureBetweenDate(deliveryDate,Calendar.getInstance().getTimeInMillis());
			}
		}catch(Exception exception){
			
		}
		return -1;
		
	}
	  
	
	
			

}
