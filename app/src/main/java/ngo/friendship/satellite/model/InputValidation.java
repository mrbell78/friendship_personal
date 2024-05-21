package ngo.friendship.satellite.model;

import java.io.Serializable;

// TODO: Auto-generated Javadoc

/**
 * The Class InputValidation.
 */
public class InputValidation implements Serializable{
	
	/** The validation type. */
	private String validationType;
	
	/** The value. */
	private String value;
	
	private String baseDate;
	/**
	 * Sets the validation type.
	 *
	 * @param validationType the new validation type
	 */
	public void setValidationType(String validationType) {
		this.validationType = validationType;
	}
	
	/**
	 * Gets the validation type.
	 *
	 * @return the validation type
	 */
	public String getValidationType() {
		return validationType;
	}
	
	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	public void setBaseDate(String baseDate) {
		this.baseDate = baseDate;
	}
	public String getBaseDate() {
		return baseDate;
	}
	
}
