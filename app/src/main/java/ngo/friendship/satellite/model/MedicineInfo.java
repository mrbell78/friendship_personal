package ngo.friendship.satellite.model;

import java.io.Serializable;

import ngo.friendship.satellite.utility.TextUtility;

// TODO: Auto-generated Javadoc

/**
 * The Class MedicineInfo.
 */
public class MedicineInfo implements Serializable{

	/** The required quantity. */
	private String requiredQuantity="0";
	private long qtyDispatch=0;
	private long qtyReceived=0;
	private Double priceDispatch=0.0;
	private Double price=0.0;
	/** The exp date. */
	private String expDate;
	
	/** The input date. */
	private String inputDate;
	
	/** The medicine taking rule. */
	private String medicineTakingRule;

	/** The medicine taking rule. */
	private String medicineTakingRuleLabel;
	/** The medicine taking rule. */
	private String smsFormatmedicineTakingRule;
	
	/** The total price. */
	private double totalPrice;
	
	/** The dose duration. */
	private String doseDuration ="0";
	
	/** The advice for medicine. */
	private String adviceForMedicine;
	/** The advice for medicine. */
	private String smsFormatadviceForMedicine;
	/** The advice for medicine. */
	private String smsFormat;
	
	//	private String manufacturer;
	//	private String medCode;
	/** The available quantity. */
	private int availableQuantity;

	/** The sold quantity. */
	private String soldQuantity="0";

	/** The current stock quantity. */
	private int currentStockQuantity;
	
	/** The minimum stock quantity. */
	private int minimumStockQuantity;
	//Column(name = "MEDICINE_ID")
	/** The med id. */
	private long medId;

	//Column(name = "BRAND_NAME")
	/** The brand name. */
	private String brandName;

	//Column(name = "GENERIC_NAME")
	/** The generic name. */
	private String genericName;

	//Column(name = "UNIT_PURCHASE_PRICE")
	/** The unit purchase price. */
	private double unitPurchasePrice;

	//Column(name = "UNIT_SALES_PRICE")
	/** The unit sales price. */
	private double unitSalesPrice;

	//Column(name = "STRENGTH")
	/** The strength. */
	private float strength;

	//Column(name = "MEASURE_UNIT") enum('MG','ML')
	/** The measure unit. */
	private String measureUnit;

	//Column(name = "MANUF_ID")
	/** The manuf id. */
	private int manufId;


	/** The medicine type. */
	private String medicineType;
	
	

	private long state;
	private long boxSize;
    private String accessType;
    private String unitType;
    
    private boolean selected;
    
    private long updateTime;

	private String days;
	private String takingTime;
	private String doses;

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getTakingTime() {
		return takingTime;
	}

	public void setTakingTime(String takingTime) {
		this.takingTime = takingTime;
	}

	public String getDoses() {
		return doses;
	}

	public void setDoses(String doses) {
		this.doses = doses;
	}

	public void setState(long state) {
		this.state = state;
	}
    public long getState() {
		return state;
	}
    
    public void setPrice(Double price) {
		this.price = price;
	}
    public Double getPrice() {
		return price;
	}
    

	public long getBoxSize() {
		return boxSize;
	}

	public void setBoxSize(long boxSize) {
		this.boxSize = boxSize;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	/**
	 * Sets the advice for medicine.
	 *
	 * @param adviceForMedicine the new advice for medicine
	 */
	public void setAdviceForMedicine(String adviceForMedicine) {
		this.adviceForMedicine = adviceForMedicine;
	}
	
	/**
	 * Gets the advice for medicine.
	 *
	 * @return the advice for medicine
	 */
	public String getAdviceForMedicine() {
		return adviceForMedicine;
	}
	
	/**
	 * Sets the dose duration.
	 *
	 * @param doseDuration the new dose duration
	 */
	public void setDoseDuration(String doseDuration) {
		this.doseDuration = doseDuration;
	}
	
	
	public void setQtyReceived(long qtyReceived) {
		this.qtyReceived = qtyReceived;
	}
	public long getQtyReceived() {
		return qtyReceived;
	}
	
	/**
	 * Gets the dose duration.
	 *
	 * @return the dose duration
	 */
	public String getDoseDuration() {
		return doseDuration;
	}
	
	/**
	 * Sets the sold quantity.
	 *
	 * @param soldQuantity the new sold quantity
	 */
	public void setSoldQuantity(String soldQuantity) {
		this.soldQuantity = soldQuantity;
	}
	
	/**
	 * Gets the sold quantity.
	 *
	 * @return the sold quantity
	 */
	public String getSoldQuantity() {
		return soldQuantity;
	}

	/**
	 * Sets the current stock quantity.
	 *
	 * @param currentStockQuantity the new current stock quantity
	 */
	public void setCurrentStockQuantity(int currentStockQuantity) {
		this.currentStockQuantity = currentStockQuantity;
	}
	
	/**
	 * Gets the current stock quantity.
	 *
	 * @return the current stock quantity
	 */
	public int getCurrentStockQuantity() {
		return currentStockQuantity;
	}

	/**
	 * Sets the minimum stock quantity.
	 *
	 * @param minimumStockQuantity the new minimum stock quantity
	 */
	public void setMinimumStockQuantity(int minimumStockQuantity) {
		this.minimumStockQuantity = minimumStockQuantity;
	}
	
	/**
	 * Gets the minimum stock quantity.
	 *
	 * @return the minimum stock quantity
	 */
	public int getMinimumStockQuantity() {
		return minimumStockQuantity;
	}

	/**
	 * Sets the medicine type.
	 *
	 * @param medicineType the new medicine type
	 */
	public void setMedicineType(String medicineType) {
		this.medicineType = medicineType;
	}
	
	/**
	 * Gets the medicine type.
	 *
	 * @return the medicine type
	 */
	public String getMedicineType() {
		return medicineType;
	}

	/**
	 * Gets the brand name.
	 *
	 * @return the brand name
	 */
	public String getBrandName() {
		return brandName;
	}
	
	/**
	 * Sets the brand name.
	 *
	 * @param brandName the new brand name
	 */
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	/**
	 * Gets the generic name.
	 *
	 * @return the generic name
	 */
	public String getGenericName() {
		return genericName;
	}
	
	/**
	 * Sets the generic name.
	 *
	 * @param genericName the new generic name
	 */
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}

	/**
	 * Gets the unit purchase price.
	 *
	 * @return the unit purchase price
	 */
	public double getUnitPurchasePrice() {
		return unitPurchasePrice;
	}
	
	/**
	 * Sets the unit purchase price.
	 *
	 * @param unitPurchasePrice the new unit purchase price
	 */
	public void setUnitPurchasePrice(double unitPurchasePrice) {
		this.unitPurchasePrice = unitPurchasePrice;
	}

	/**
	 * Gets the unit sales price.
	 *
	 * @return the unit sales price
	 */
	public double getUnitSalesPrice() {
		return unitSalesPrice;
	}
	
	/**
	 * Sets the unit sales price.
	 *
	 * @param unitSalesPrice the new unit sales price
	 */
	public void setUnitSalesPrice(double unitSalesPrice) {
		this.unitSalesPrice = unitSalesPrice;
	}

	/**
	 * Gets the strength.
	 *
	 * @return the strength
	 */
	public float getStrength() {
		return strength;
	}
	
	/**
	 * Sets the strength.
	 *
	 * @param strength the new strength
	 */
	public void setStrength(float strength) {
		this.strength = strength;
	}

	/**
	 * Gets the measure unit.
	 *
	 * @return the measure unit
	 */
	public String getMeasureUnit() {
		return measureUnit;
	}
	
	/**
	 * Sets the measure unit.
	 *
	 * @param measureUnit the new measure unit
	 */
	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

	/**
	 * Gets the manuf id.
	 *
	 * @return the manuf id
	 */
	public int getManufId() {
		return manufId;
	}
	
	/**
	 * Sets the manuf id.
	 *
	 * @param manufId the new manuf id
	 */
	public void setManufId(int manufId) {
		this.manufId = manufId;
	}


	/**
	 * Sets the medicine taking rule.
	 *
	 * @param medicineTakingRule the new medicine taking rule
	 */
	public void setMedicineTakingRule(String medicineTakingRule) {
		this.medicineTakingRule = medicineTakingRule;
	}
	
	/**
	 * Gets the medicine taking rule.
	 *
	 * @return the medicine taking rule
	 */
	public String getMedicineTakingRule() {
		return medicineTakingRule;
	}

	/**
	 * Sets the total price.
	 *
	 * @param totalPrice the new total price
	 */
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	/**
	 * Gets the total price.
	 *
	 * @return the total price
	 */
	public double getTotalPrice() {
		return totalPrice;
	}

	/**
	 * Sets the exp date.
	 *
	 * @param expDate the new exp date
	 */
	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}
	
	/**
	 * Gets the exp date.
	 *
	 * @return the exp date
	 */
	public String getExpDate() {
		return expDate;
	}

	/**
	 * Sets the input date.
	 *
	 * @param inputDate the new input date
	 */
	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}
	
	/**
	 * Gets the input date.
	 *
	 * @return the input date
	 */
	public String getInputDate() {
		return inputDate;
	}

	/**
	 * Sets the med id.
	 *
	 * @param medId the new med id
	 */
	public void setMedId(long medId) {
		this.medId = medId;
	}
	
	/**
	 * Gets the med id.
	 *
	 * @return the med id
	 */
	public long getMedId() {
		return medId;
	}

	/**
	 * Sets the required quantity.
	 *
	 * @param requiredQuantity the new required quantity
	 */
	public void setRequiredQuantity(String requiredQuantity) {
		this.requiredQuantity = requiredQuantity;
	}
	
	/**
	 * Gets the required quantity.
	 *
	 * @return the required quantity
	 */
	public String getRequiredQuantity() {
		return requiredQuantity;
	}

	public void setPriceDispatch(Double priceDispatch) {
		this.priceDispatch = priceDispatch;
	}
	
	public long getQtyDispatch() {
		return qtyDispatch;
	}
	
	public void setQtyDispatch(long qtyDispatch) {
		this.qtyDispatch = qtyDispatch;
	}
	public Double getPriceDispatch() {
		return priceDispatch;
	}
	
	/**
	 * Sets the available quantity.
	 *
	 * @param availableQuantity the new available quantity
	 */
	public void setAvailableQuantity(int availableQuantity) {
		this.availableQuantity = availableQuantity;
	}
	
	/**
	 * Gets the available quantity.
	 *
	 * @return the available quantity
	 */
	public int getAvailableQuantity() {
		return availableQuantity;
	}

	public String getSmsFormatmedicineTakingRule() {
		return smsFormatmedicineTakingRule;
	}
	public void setSmsFormatmedicineTakingRule(
			String smsFormatmedicineTakingRule) {
		this.smsFormatmedicineTakingRule = smsFormatmedicineTakingRule;
	}
	public String getSmsFormatadviceForMedicine() {
		return smsFormatadviceForMedicine;
	}
	public void setSmsFormatadviceForMedicine(String smsFormatadviceForMedicine) {
		this.smsFormatadviceForMedicine = smsFormatadviceForMedicine;
	}
	public String getSmsFormat() {
		return smsFormat;
	}
	public void setSmsFormat(String smsFormat) {
		this.smsFormat = smsFormat;
	}
	
	public void setMedicineTakingRuleLabel(String medicineTakingRuleLabel) {
		this.medicineTakingRuleLabel = medicineTakingRuleLabel;
	}
	public String getMedicineTakingRuleLabel() {
		return medicineTakingRuleLabel;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	public boolean isSelected() {
		return this.selected;
	}
	
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
	public long getUpdateTime() {
		return updateTime;
	}
	
    @Override
    public String toString() {
    	StringBuilder  builder =new StringBuilder();
    	

    	if(medicineType!=null && medicineType.trim().length()>0){
    		builder.append(medicineType);
    		builder.append(": ");
    	}
    	
    	
    	if(brandName!=null && brandName.trim().length()>0){
    		builder.append(brandName);
    		builder.append(" ");
    	}else if (genericName!=null && genericName.trim().length()>0){
    		builder.append(genericName);
    		builder.append(" ");
    	}
    	
    	if(strength>0){
    		builder.append("(");
    		builder.append(strength%1>0? TextUtility.format("%.2f",strength):(int)strength);
    		if(measureUnit!=null && measureUnit.trim().length()>0){
    			builder.append(measureUnit);
    		}
    		builder.append(")");
    	}else if(measureUnit!=null && measureUnit.trim().length()>0){
    		builder.append("(");
    		builder.append(measureUnit);
    		builder.append(")");
    	}
    	
    	return builder.toString();
    }
}
