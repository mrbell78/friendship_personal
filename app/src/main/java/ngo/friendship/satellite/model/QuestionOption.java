package ngo.friendship.satellite.model;

import java.io.Serializable;

// TODO: Auto-generated Javadoc

/**
 * The Class QuestionOption.
 */
public class QuestionOption  implements Serializable{
	
	/** The id. */
	private int id;
	
	/** The caption. */
	private String caption;
	
	/** The value. */
	private String value;
	
	/** The option name. */
	private String optionName;

	private boolean selected=false;


	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Sets the option name.
	 *
	 * @param optionName the new option name
	 */
	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}
	
	/**
	 * Gets the option name.
	 *
	 * @return the option name
	 */
	public String getOptionName() {
		return optionName;
	}
	
	/**
	 * Sets the caption.
	 *
	 * @param caption the new caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	/**
	 * Gets the caption.
	 *
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
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
}
