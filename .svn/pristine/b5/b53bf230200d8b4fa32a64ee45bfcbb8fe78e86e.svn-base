/*
 * Author: Kayum Hossan
 * Description: Data structure to hold referral center infortaion
 * Created Date: 09-March-2014
 * Last Modified: 09-March-2014
 * */
package ngo.friendship.satellite.model;

import java.io.Serializable;


// TODO: Auto-generated Javadoc

/**
 * The Class ReferralCenterInfo.
 */
public class ReferralCenterInfo implements Serializable {

	/** The id. */
	private long ID;
	
	/** The caption name. */
	private String captionName;
	
	/** The description. */
	private String description;
	
	/** The contact name. */
	private String contactName;
	
	/** The contact number. */
	private String contactNumber;
	
	/** The distance. */
	private int distance;
	
	/** The time need. */
	private int timeNeed;
	
	/** The transport way. */
	private String transportWay;
	
	/** The address. */
	private String address;
	
	/** The state. */
	private int state;
	
	/** The daily ccs capacity. */
	private int dailyCCSCapacity;
	

	

	private String contactEmail;
	private Double latitude;
	private Double longitude;
	private Long locationId;
	private String refCenterCode ;
	private String refCenterName;
	private String refCenterType;
    private long refCenterCatId;
	
	/**
	 * Sets the daily ccs capacity.
	 *
	 * @param dailyCCSCapacity the new daily ccs capacity
	 */
	public void setDailyCCSCapacity(int dailyCCSCapacity) {
		this.dailyCCSCapacity = dailyCCSCapacity;
	}
	
	/**
	 * Gets the daily ccs capacity.
	 *
	 * @return the daily ccs capacity
	 */
	public int getDailyCCSCapacity() {
		return dailyCCSCapacity;
	}
	
	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	
	/** The is checked. */
	private boolean isChecked;
	
	/**
	 * Checks if is checked.
	 *
	 * @return true, if is checked
	 */
	public boolean isChecked() {
		return isChecked;
	}
	
	/**
	 * Sets the checked.
	 *
	 * @param isChecked the new checked
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getID() {
		return ID;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param iD the new id
	 */
	public void setID(long iD) {
		ID = iD;
	}
	
	/**
	 * Gets the caption name.
	 *
	 * @return the caption name
	 */
	public String getCaptionName() {
		return captionName;
	}
	
	/**
	 * Sets the caption name.
	 *
	 * @param captionName the new caption name
	 */
	public void setCaptionName(String captionName) {
		this.captionName = captionName;
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	/**
	 * Gets the contact name.
	 *
	 * @return the contact name
	 */
	public String getContactName() {
		return contactName;
	}
	
	/**
	 * Sets the contact name.
	 *
	 * @param contactName the new contact name
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	/**
	 * Gets the contact number.
	 *
	 * @return the contact number
	 */
	public String getContactNumber() {
		return contactNumber;
	}
	
	/**
	 * Sets the contact number.
	 *
	 * @param contactNumber the new contact number
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	
	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}
	
	/**
	 * Sets the distance.
	 *
	 * @param distance the new distance
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	/**
	 * Gets the time need.
	 *
	 * @return the time need
	 */
	public int getTimeNeed() {
		return timeNeed;
	}
	
	/**
	 * Sets the time need.
	 *
	 * @param timeNeed the new time need
	 */
	public void setTimeNeed(int timeNeed) {
		this.timeNeed = timeNeed;
	}
	
	/**
	 * Gets the transport way.
	 *
	 * @return the transport way
	 */
	public String getTransportWay() {
		return transportWay;
	}
	
	/**
	 * Sets the transport way.
	 *
	 * @param transportWay the new transport way
	 */
	public void setTransportWay(String transportWay) {
		this.transportWay = transportWay;
	}
	
	
	
    @Override
    public String toString() {
		String st = "";
		if (distance>0){
			 st=captionName+" - "+distance+"KM";
		}else{
			st=captionName;
		}
		if(transportWay!=null){
			st=st+" - "+transportWay;
		}
    	return  st;
    }

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getRefCenterCode() {
		return refCenterCode;
	}

	public void setRefCenterCode(String refCenterCode) {
		this.refCenterCode = refCenterCode;
	}

	public String getRefCenterName() {
		return refCenterName;
	}

	public void setRefCenterName(String refCenterName) {
		this.refCenterName = refCenterName;
	}

	public String getRefCenterType() {
		return refCenterType;
	}

	public void setRefCenterType(String refCenterType) {
		this.refCenterType = refCenterType;
	}

	public void setRefCenterCatId(long refCenterCatId) {
		this.refCenterCatId = refCenterCatId;
	}
	
	public long getRefCenterCatId() {
		return refCenterCatId;
	}
	
    
}
