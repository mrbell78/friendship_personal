package ngo.friendship.satellite.model;

import java.io.Serializable;
import java.util.ArrayList;


// TODO: Auto-generated Javadoc

/**
 * The Class RequisitionInfo.
 */
public class RequisitionInfo implements Serializable{
	
	/** The requisition no. */
	private long requisitionNo;
	
	/** The requisition id. */
	private long requisitionId;
	
	/** The medicine list. */
	private ArrayList<MedicineInfo> medicineList;
	
	/** The requisition date. */
	private String requisitionDate;
	
	/** The requisition medicine price. */
	private double requisitionMedicinePrice;
	
	/** The receive medicine price. */
	private double receiveMedicinePrice;
	
	/** The requisition status. */
	private String requisitionStatus;
	
	/** The complete date. */
	private String completeDate;
	
	/** The receive date. */
	private String receiveDate;
	
	/** The received id. */
	private long receivedId;
	
	/**
	 * Sets the received id.
	 *
	 * @param receivedId the new received id
	 */

	private String requisitionAcceptedDetail = "{}";

	public void setReceivedId(long receivedId) {
		this.receivedId = receivedId;
	}
	
	/**
	 * Gets the received id.
	 *
	 * @return the received id
	 */
	public long getReceivedId() {
		return receivedId;
	}
	
	/**
	 * Sets the receive date.
	 *
	 * @param receiveDate the new receive date
	 */
	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}
	
	/**
	 * Gets the receive date.
	 *
	 * @return the receive date
	 */
	public String getReceiveDate() {
		return receiveDate;
	}
	
	/** The requisition medicine list. */
	private ArrayList<RequisitionMedicineInfo> requisitionMedicineList;
	
	/**
	 * Sets the requisition medicine list.
	 *
	 * @param requisitionMedicineList the new requisition medicine list
	 */
	public void setRequisitionMedicineList(
			ArrayList<RequisitionMedicineInfo> requisitionMedicineList) {
		this.requisitionMedicineList = requisitionMedicineList;
	}
	
	/**
	 * Gets the requisition medicine list.
	 *
	 * @return the requisition medicine list
	 */
	public ArrayList<RequisitionMedicineInfo> getRequisitionMedicineList() {return requisitionMedicineList;}
	
	/**
	 * Sets the complete date.
	 *
	 * @param completeDate the new complete date
	 */
	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}
	
	/**
	 * Gets the complete date.
	 *
	 * @return the complete date
	 */
	public String getCompleteDate() {
		return completeDate;
	}
	
	/**
	 * Gets the requisition date.
	 *
	 * @return the requisition date
	 */
	public String getRequisitionDate() {
		return requisitionDate;
	}
	
	/**
	 * Sets the requisition date.
	 *
	 * @param requisitionDate the new requisition date
	 */
	public void setRequisitionDate(String requisitionDate) {
		this.requisitionDate = requisitionDate;
	}
	
	/**
	 * Gets the requisition medicine price.
	 *
	 * @return the requisition medicine price
	 */
	public double getRequisitionMedicinePrice() {
		return requisitionMedicinePrice;
	}
	
	/**
	 * Sets the requisition medicine price.
	 *
	 * @param requisitionMedicinePrice the new requisition medicine price
	 */
	public void setRequisitionMedicinePrice(double requisitionMedicinePrice) {
		this.requisitionMedicinePrice = requisitionMedicinePrice;
	}
	
	/**
	 * Gets the receive medicine price.
	 *
	 * @return the receive medicine price
	 */
	public double getReceiveMedicinePrice() {
		return receiveMedicinePrice;
	}
	
	/**
	 * Sets the receive medicine price.
	 *
	 * @param receiveMedicinePrice the new receive medicine price
	 */
	public void setReceiveMedicinePrice(double receiveMedicinePrice) {
		this.receiveMedicinePrice = receiveMedicinePrice;
	}
	
	/**
	 * Gets the requisition status.
	 *
	 * @return the requisition status
	 */
	public String getRequisitionStatus() {
		return requisitionStatus;
	}
	
	/**
	 * Sets the requisition status.
	 *
	 * @param requisitionStatus the new requisition status
	 */
	public void setRequisitionStatus(String requisitionStatus) {
		this.requisitionStatus = requisitionStatus;
	}
	
	/**
	 * Sets the requisition id.
	 *
	 * @param requisitionId the new requisition id
	 */
	public void setRequisitionId(long requisitionId) {
		this.requisitionId = requisitionId;
	}
	
	/**
	 * Gets the requisition id.
	 *
	 * @return the requisition id
	 */
	public long getRequisitionId() {
		return requisitionId;
	}
	
	/**
	 * Sets the medicine list.
	 *
	 * @param medicineList the new medicine list
	 */
	public void setMedicineList(ArrayList<MedicineInfo> medicineList) {
		this.medicineList = medicineList;
	}
	
	/**
	 * Gets the medicine list.
	 *
	 * @return the medicine list
	 */
	public ArrayList<MedicineInfo> getMedicineList() {
		return medicineList;
	}
	
	/**
	 * Sets the requisition no.
	 *
	 * @param requisitionNo the new requisition no
	 */
	public void setRequisitionNo(long requisitionNo) {
		this.requisitionNo = requisitionNo;
	}
	
	/**
	 * Gets the requisition no.
	 *
	 * @return the requisition no
	 */
	public long getRequisitionNo() {
		return requisitionNo;
	}


	public String getRequisitionAcceptedDetail() {
		return requisitionAcceptedDetail;
	}

	public void setRequisitionAcceptedDetail(String requisitionAcceptedDetail) {
		this.requisitionAcceptedDetail = requisitionAcceptedDetail;
	}
}
