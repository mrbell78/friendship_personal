package ngo.friendship.satellite.loadname;

// TODO: Auto-generated Javadoc
/**
 * The Class Item.
 */
public class Item implements Comparable<Item>{
	
	/** The name. */
	private String name;
	
	/** The data. */
	private String data;
	
	/** The date. */
	private String date;
	
	/** The path. */
	private String path;
	
	/** The image. */
	private String image;
	
	/**
	 * Instantiates a new item.
	 *
	 * @param n the n
	 * @param d the d
	 * @param dt the dt
	 * @param p the p
	 * @param img the img
	 */
	public Item(String n,String d, String dt, String p, String img)
	{
		name = n;
		data = d;
		date = dt;
		path = p; 
		image = img;
		
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
	 * Gets the data.
	 *
	 * @return the data
	 */
	public String getData()
	{
		return data;
	}
	
	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public String getDate()
	{
		return date;
	}
	
	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}
	
	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Item o) {
		if(this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase()); 
		else 
			throw new IllegalArgumentException();
	}
}
