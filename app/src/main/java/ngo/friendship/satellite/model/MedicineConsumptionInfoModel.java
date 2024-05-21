package ngo.friendship.satellite.model;

import java.util.ArrayList;

public class MedicineConsumptionInfoModel {

    private long interviewId;
    private long benefCode;
    private long medConsumpId;
    private long locationId;
    private String consumptionDate;
    private long userId;
    private long totalPrice;
    private String remarks;
    private String dataSent;
    private String versionNo;

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    ArrayList<ConsumptionDetailsModel> consumptionDetails;


    public long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(long interviewId) {
        this.interviewId = interviewId;
    }

    public long getBenefCode() {
        return benefCode;
    }

    public void setBenefCode(long benefCode) {
        this.benefCode = benefCode;
    }

    public long getMedConsumpId() {
        return medConsumpId;
    }

    public void setMedConsumpId(long medConsumpId) {
        this.medConsumpId = medConsumpId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(String consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDataSent() {
        return dataSent;
    }

    public void setDataSent(String dataSent) {
        this.dataSent = dataSent;
    }

    public ArrayList<ConsumptionDetailsModel> getConsumptionDetails() {
        return consumptionDetails;
    }

    public void setConsumptionDetails(ArrayList<ConsumptionDetailsModel> consumptionDetails) {
        this.consumptionDetails = consumptionDetails;
    }
}
