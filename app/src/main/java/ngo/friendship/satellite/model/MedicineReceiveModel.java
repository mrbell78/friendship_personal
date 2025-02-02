package ngo.friendship.satellite.model;

import java.util.ArrayList;

public class MedicineReceiveModel {

    private String medicine_receive_date;
    private double total_price;
    private long medicine_received_id;
    private long location_id;
    private long supplier_id;

    private long user_id;
    private String versionNo;

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    ArrayList<MedicineReceivedDetailModel> medicineReceivedDetailList;

    public ArrayList<MedicineReceivedDetailModel> getMedicineReceivedDetailList() {
        return medicineReceivedDetailList;
    }

    public void setMedicineReceivedDetailList(ArrayList<MedicineReceivedDetailModel> medicineReceivedDetailList) {
        this.medicineReceivedDetailList = medicineReceivedDetailList;
    }

    public String getMedicine_receive_date() {
        return medicine_receive_date;
    }

    public void setMedicine_receive_date(String medicine_receive_date) {
        this.medicine_receive_date = medicine_receive_date;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public long getMedicine_received_id() {
        return medicine_received_id;
    }

    public void setMedicine_received_id(long medicine_received_id) {
        this.medicine_received_id = medicine_received_id;
    }

    public long getLocation_id() {
        return location_id;
    }

    public void setLocation_id(long location_id) {
        this.location_id = location_id;
    }

    public long getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(long supplier_id) {
        this.supplier_id = supplier_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }
}
