package ngo.friendship.satellite.model;

import java.io.Serializable;

// TODO: Auto-generated Javadoc

/**
 * The Class ImmunizationInfo.
 */
public class ImmunizationInfo  implements Serializable{
	
	/** The id. */
	private long id;
	
	/** The taken date str. */
	private String takenDateStr;
	
	/** The taken date in millis. */
	private long takenDateInMillis;
	
	/** The type. */
	private String type;
	
	/** The name. */
	private String name;
	
	/** The desc from org. */
	private String descFromOrg;
	
	/** The desc from gov. */
	private String descFromGov;
	
	/** The start index. */
	private int startIndex;
	
	/** The number of dose. */
	private int numberOfDose;
	
    private long sortOrder;
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the desc from org.
	 *
	 * @return the desc from org
	 */
	public String getDescFromOrg() {
		return descFromOrg;
	}
	
	/**
	 * Sets the desc from org.
	 *
	 * @param descFromOrg the new desc from org
	 */
	public void setDescFromOrg(String descFromOrg) {
		this.descFromOrg = descFromOrg;
	}
	
	/**
	 * Gets the desc from gov.
	 *
	 * @return the desc from gov
	 */
	public String getDescFromGov() {
		return descFromGov;
	}
	
	/**
	 * Sets the desc from gov.
	 *
	 * @param descFromGov the new desc from gov
	 */
	public void setDescFromGov(String descFromGov) {
		this.descFromGov = descFromGov;
	}
	
	/**
	 * Gets the start index.
	 *
	 * @return the start index
	 */
	public int getStartIndex() {
		return startIndex;
	}
	
	/**
	 * Sets the start index.
	 *
	 * @param startIndex the new start index
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	/**
	 * Gets the number of dose.
	 *
	 * @return the number of dose
	 */
	public int getNumberOfDose() {
		return numberOfDose;
	}
	
	/**
	 * Sets the number of dose.
	 *
	 * @param numberOfDose the new number of dose
	 */
	public void setNumberOfDose(int numberOfDose) {
		this.numberOfDose = numberOfDose;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}
	

	/**
	 * Gets the taken date str.
 *
 * @return the taken date str
 */
	public String getTakenDateStr() {
		return takenDateStr;
	}
	
	/**
	 * Sets the taken date str.
	 *
	 * @param takenDateStr the new taken date str
	 */
	public void setTakenDateStr(String takenDateStr) {
		this.takenDateStr = takenDateStr;
	}
	
	/**
	 * Gets the taken date in millis.
	 *
	 * @return the taken date in millis
	 */
	public long getTakenDateInMillis() {
		return takenDateInMillis;
	}
	
	/**
	 * Sets the taken date in millis.
	 *
	 * @param takenDateInMillis the new taken date in millis
	 */
	public void setTakenDateInMillis(long takenDateInMillis) {
		this.takenDateInMillis = takenDateInMillis;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	public void setSortOrder(long sortOrder) {
		this.sortOrder = sortOrder;
	}
	public long getSortOrder() {
		return sortOrder;
	}
	
	
}
