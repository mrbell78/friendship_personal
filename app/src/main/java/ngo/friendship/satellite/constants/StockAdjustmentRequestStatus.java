package ngo.friendship.satellite.constants;
// TODO: Auto-generated Javadoc

/**
 * Stock adjustment  status.
 *
 * @author Kayum Hossan
 */
public enum StockAdjustmentRequestStatus {
	
	/** The open. */
	OPEN("Open"),
	
	RECOMMENDED("Recommended"),
	
	/** The approved. */
	APPROVED("Approved"),
	
	/** The rejected. */
	REJECTED("Rejected");

	/** The name. */
	private final String name;       

	/**
	 * Instantiates a new stock adjustment request status.
	 *
	 * @param s the s
	 */
    StockAdjustmentRequestStatus(String s) {
		name = s;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){
		return name;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * Equals name.
	 *
	 * @param otherName the other name
	 * @return true, if successful
	 */
	public boolean equalsName(String otherName){
        return otherName != null && name.equals(otherName);
    }
}
