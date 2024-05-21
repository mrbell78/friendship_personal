package ngo.friendship.satellite.constants;
// TODO: Auto-generated Javadoc

/**
 * Requisition status.
 *
 * @author Kayum Hossan
 */
public enum RequisitionStatus {

	/** The Initiated. */
	Initiated("Initiated"),

	/** The Accepted. */
	Accepted("Accepted"),

	/** The Rejected. */
	Rejected("Rejected"),

	/** The Completed. */
	Completed("Completed");

	/** The name. */
	private final String name;

	/**
	 * Instantiates a new requisition status.
	 *
	 * @param name the name
	 */
    RequisitionStatus(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){
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
