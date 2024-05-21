package ngo.friendship.satellite.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.error.ErrorCode;
import ngo.friendship.satellite.error.MhealthException;

// TODO: Auto-generated Javadoc

/**
 * The Class AdjustmentMedicineInfo.
 */
public class AdjustmentMedicineInfo  implements Serializable{
	
	/** The medicine id. */
	private long medicineId;
	
	/** The medicine name. */
	private String medicineName;
	
	/** The adjust quantity. */
	private int adjustQuantity;
	
	private long updatedOn;

	private int inputQty;

	private int currentStockQty;

	public void setCurrentStockQty(int currentStockQty) {
		this.currentStockQty = currentStockQty;
	}

	public int getCurrentStockQty() {
		return currentStockQty;
	}

	public int getInputQty() {
		return inputQty;
	}

	public void setInputQty(int inputQty) {
		this.inputQty = inputQty;
	}

	/**
	 * Gets the medicine id.
	 *
	 * @return the medicine id
	 */
	public long getMedicineId() {
		return medicineId;
	}
	
	/**
	 * Sets the medicine id.
	 *
	 * @param medicineId the new medicine id
	 */
	public void setMedicineId(long medicineId) {
		this.medicineId = medicineId;
	}
	
	/**
	 * Gets the medicine name.
	 *
	 * @return the medicine name
	 */
	public String getMedicineName() {
		return medicineName;
	}
	
	/**
	 * Sets the medicine name.
	 *
	 * @param medicineName the new medicine name
	 */
	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}
	
	/**
	 * Gets the adjust quantity.
	 *
	 * @return the adjust quantity
	 */
	public int getAdjustQuantity() {
		return adjustQuantity;
	}
	
	/**
	 * Sets the adjust quantity.
	 *
	 * @param adjustQuantity the new adjust quantity
	 */
	public void setAdjustQuantity(int adjustQuantity) {
		this.adjustQuantity = adjustQuantity;
	}
	
	public void setUpdatedOn(long updatedOn) {
		this.updatedOn = updatedOn;
	}
	public long getUpdatedOn() {
		return updatedOn;
	}
	
	public JSONObject toJson() throws MhealthException{
		JSONObject jMedObj = new JSONObject();
		try {
			jMedObj.put(Column.MEDICINE_ID,getMedicineId());
			jMedObj.put(KEY.ADJUST_QTY,getAdjustQuantity());
			jMedObj.put(KEY.PRE_RQST_QTY,getCurrentStockQty());
			jMedObj.put(KEY.RQST_QTY,getInputQty());
		} catch (JSONException e) {
			e.printStackTrace();
			throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON_EXCEPTION",e);
		}
		return jMedObj;
	}

}
