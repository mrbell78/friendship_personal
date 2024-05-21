package ngo.friendship.satellite.model.maternal;

import java.io.Serializable;

import ngo.friendship.satellite.model.Beneficiary;


// Table(name = "maternal_baby_info")
public class MaternalBabyInfo extends Beneficiary  implements Serializable {
	
    //Column(name = "MATERNAL_BABY_ID")
    private long maternalBabyId;
    //Column(name = "BABY_STATE")
    private String babyState;
    //Column(name = "GENDER")
   // private String gender;
    //Column(name = "CHILD_BENEF_ID")
    private long maternalId;
    
    private String childBenefCode;
    
    private boolean alive=false;
    
    private long serial;

    public void setMaternalId(long maternalId) {
		this.maternalId = maternalId;
	}
    public long getMaternalId() {
		return maternalId;
	}
    
    public long getMaternalBabyId() {
        return maternalBabyId;
    }

    public void setMaternalBabyId(long maternalBabyId) {
        this.maternalBabyId = maternalBabyId;
    }

    public String getBabyState() {
        return babyState;
    }

    public void setBabyState(String babyState) {
        this.babyState = babyState;
    }

    public void setSerial(long serial) {
		this.serial = serial;
	}
    public long getSerial() {
		return serial;
	}
    public void setAlive(boolean alive) {
		this.alive = alive;
	}
    public boolean isAlive() {
		return alive;
	}

    public void setChildBenefCode(String childBenefCode) {
		this.childBenefCode = childBenefCode;
	}
    public String getChildBenefCode() {
		return childBenefCode;
	}

    //TRANS_REF
    private long transRef;

    public void setTransRef(long transRef) {
        this.transRef = transRef;
    }

    public long getTransRef() {
        return transRef;
    }
    
    public static final String MODEL_NAME="maternal_baby_info";
   
    
}
