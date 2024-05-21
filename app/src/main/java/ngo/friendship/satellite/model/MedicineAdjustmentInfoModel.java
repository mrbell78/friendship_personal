package ngo.friendship.satellite.model;

import java.util.ArrayList;

public class MedicineAdjustmentInfoModel {

    private long medicine_adjust_id;
    private long user_id;
    private String request_date;

    private long request_number;

    private long location_id;

    public long getLocation_id() {
        return location_id;
    }

    public void setLocation_id(long location_id) {
        this.location_id = location_id;
    }

    public long getRequest_number() {
        return request_number;
    }

    public void setRequest_number(long request_number) {
        this.request_number = request_number;
    }

    private String recommended_by;
    private String recommende_on;
    private long approved_by;
    private String approved_on;
    private String fcm_fetch_on;
    private String state;

    private String recordDate;
    private String versionNo;

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    private ArrayList<MedicineAdjustmentDetailModel> medicineAdjustmentDetailModels;


    public long getMedicine_adjust_id() {
        return medicine_adjust_id;
    }

    public void setMedicine_adjust_id(long medicine_adjust_id) {
        this.medicine_adjust_id = medicine_adjust_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getRecommended_by() {
        return recommended_by;
    }

    public void setRecommended_by(String recommended_by) {
        this.recommended_by = recommended_by;
    }

    public String getRecommende_on() {
        return recommende_on;
    }

    public void setRecommende_on(String recommende_on) {
        this.recommende_on = recommende_on;
    }

    public long getApproved_by() {
        return approved_by;
    }

    public void setApproved_by(long approved_by) {
        this.approved_by = approved_by;
    }

    public String getApproved_on() {
        return approved_on;
    }

    public void setApproved_on(String approved_on) {
        this.approved_on = approved_on;
    }

    public String getFcm_fetch_on() {
        return fcm_fetch_on;
    }

    public void setFcm_fetch_on(String fcm_fetch_on) {
        this.fcm_fetch_on = fcm_fetch_on;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<MedicineAdjustmentDetailModel> getAdjustmentDetailModels() {
        return medicineAdjustmentDetailModels;
    }

    public void setAdjustmentDetailModels(ArrayList<MedicineAdjustmentDetailModel> medicineAdjustmentDetailModels) {
        this.medicineAdjustmentDetailModels = medicineAdjustmentDetailModels;
    }
}
