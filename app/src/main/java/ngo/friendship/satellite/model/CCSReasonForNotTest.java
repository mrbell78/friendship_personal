package ngo.friendship.satellite.model;

import java.io.Serializable;

// TODO: Auto-generated Javadoc

/**
 * Class to hold the reason for not to do the test information
 * <br>
 * Created Date: 9th Sept 2014
 * <br>
 * Last update: 9th Sept 2014
 * <br>.
 *
 * @author Kayum Hossan
 */

public class CCSReasonForNotTest  implements Serializable{
	
	/** The reason id. */
	private long reasonId;
	
	/** The reason name. */
	private String reasonName;
	
	/** The reason caption. */
	private String reasonCaption;
	
	/**
	 * Gets the reason id.
	 *
	 * @return the reason id
	 */
	public long getReasonId() {
		return reasonId;
	}
	
	/**
	 * Sets the reason id.
	 *
	 * @param reasonId the new reason id
	 */
	public void setReasonId(long reasonId) {
		this.reasonId = reasonId;
	}
	
	/**
	 * Gets the reason name.
	 *
	 * @return the reason name
	 */
	public String getReasonName() {
		return reasonName;
	}
	
	/**
	 * Sets the reason name.
	 *
	 * @param reasonName the new reason name
	 */
	public void setReasonName(String reasonName) {
		this.reasonName = reasonName;
	}
	
	/**
	 * Gets the reason caption.
	 *
	 * @return the reason caption
	 */
	public String getReasonCaption() {
		return reasonCaption;
	}
	
	/**
	 * Sets the reason caption.
	 *
	 * @param reasonCaption the new reason caption
	 */
	public void setReasonCaption(String reasonCaption) {
		this.reasonCaption = reasonCaption;
	}
	
	
}
