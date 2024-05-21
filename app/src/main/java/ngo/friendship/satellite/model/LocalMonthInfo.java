package ngo.friendship.satellite.model;

import java.io.Serializable;

// TODO: Auto-generated Javadoc

/**
 * The Class LocalMonthInfo.
 */
public class LocalMonthInfo implements Serializable{
	
	/** The name. */
	String name;
	
	/** The number of days. */
	int numberOfDays;
	
	/** The start day. */
	int startDay;
	
	/** The start month index. */
	int startMonthIndex;
	
	/** The end day. */
	int endDay;
	
	/** The end month index. */
	int endMonthIndex;
	
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
	 * Gets the number of days.
	 *
	 * @return the number of days
	 */
	public int getNumberOfDays() {
		return numberOfDays;
	}
	
	/**
	 * Sets the number of days.
	 *
	 * @param numberOfDays the new number of days
	 */
	public void setNumberOfDays(int numberOfDays) {
		this.numberOfDays = numberOfDays;
	}
	
	/**
	 * Gets the start day.
	 *
	 * @return the start day
	 */
	public int getStartDay() {
		return startDay;
	}
	
	/**
	 * Sets the start day.
	 *
	 * @param startDay the new start day
	 */
	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}
	
	/**
	 * Gets the start month index.
	 *
	 * @return the start month index
	 */
	public int getStartMonthIndex() {
		return startMonthIndex;
	}
	
	/**
	 * Sets the start month index.
	 *
	 * @param startMonthIndex the new start month index
	 */
	public void setStartMonthIndex(int startMonthIndex) {
		this.startMonthIndex = startMonthIndex;
	}
	
	/**
	 * Gets the end day.
	 *
	 * @return the end day
	 */
	public int getEndDay() {
		return endDay;
	}
	
	/**
	 * Sets the end day.
	 *
	 * @param endDay the new end day
	 */
	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}
	
	/**
	 * Gets the end month index.
	 *
	 * @return the end month index
	 */
	public int getEndMonthIndex() {
		return endMonthIndex;
	}
	
	/**
	 * Sets the end month index.
	 *
	 * @param endMonthIndex the new end month index
	 */
	public void setEndMonthIndex(int endMonthIndex) {
		this.endMonthIndex = endMonthIndex;
	}
}
