package ngo.friendship.satellite.model;

import java.io.Serializable;

public class BeneficiaryMigration  implements Serializable{
    private String prevBenefCode;
    private String newBenefCode;
    private String migrationAction;
	public String getPrevBenefCode() {
		return prevBenefCode;
	}
	public void setPrevBenefCode(String prevBenefCode) {
		this.prevBenefCode = prevBenefCode;
	}
	public String getNewBenefCode() {
		return newBenefCode;
	}
	public void setNewBenefCode(String newBenefCode) {
		this.newBenefCode = newBenefCode;
	}
	public String getMigrationAction() {
		return migrationAction;
	}
	public void setMigrationAction(String migrationAction) {
		this.migrationAction = migrationAction;
	} 
    
    
}
