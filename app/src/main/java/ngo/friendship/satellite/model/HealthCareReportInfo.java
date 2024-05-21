package ngo.friendship.satellite.model;

import java.io.Serializable;

// TODO: Auto-generated Javadoc

/**
 * The Class HealthCareReportInfo.
 */
public class HealthCareReportInfo implements Serializable{
	
	/** The health care title. */
	private String healthCareTitle;
	
	/** The current month quantity. */
	private int currentMonthQuantity;
	
	/** The last month quantity. */
	private int lastMonthQuantity;
	
	/**
	 * Gets the health care title.
	 *
	 * @return the health care title
	 */
	public String getHealthCareTitle() {
		return healthCareTitle;
	}
	
	/**
	 * Sets the health care title.
	 *
	 * @param healthCareTitle the new health care title
	 */
	public void setHealthCareTitle(String healthCareTitle) {
		this.healthCareTitle = healthCareTitle;
	}
	
	/**
	 * Gets the current month quantity.
	 *
	 * @return the current month quantity
	 */
	public int getCurrentMonthQuantity() {
		return currentMonthQuantity;
	}
	
	/**
	 * Sets the current month quantity.
	 *
	 * @param currentMonthQuantity the new current month quantity
	 */
	public void setCurrentMonthQuantity(int currentMonthQuantity) {
		this.currentMonthQuantity = currentMonthQuantity;
	}
	
	/**
	 * Gets the last month quantity.
	 *
	 * @return the last month quantity
	 */
	public int getLastMonthQuantity() {
		return lastMonthQuantity;
	}
	
	/**
	 * Sets the last month quantity.
	 *
	 * @param lastMonthQuantity the new last month quantity
	 */
	public void setLastMonthQuantity(int lastMonthQuantity) {
		this.lastMonthQuantity = lastMonthQuantity;
	}
}
