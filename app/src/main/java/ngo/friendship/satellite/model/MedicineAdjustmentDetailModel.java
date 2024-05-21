package ngo.friendship.satellite.model;

public class MedicineAdjustmentDetailModel {

    private long medicine_adjust_dtl_id;
    private long medicine_adjust_id;
    private long medicine_id;
    private double adjust_qty;
    private double pre_rqst_qty;
    private double rqst_qty;
    private double rcmnd_qty;
    private double approved_qty;


    public double getRqst_qty() {
        return rqst_qty;
    }

    public void setRqst_qty(double rqst_qty) {
        this.rqst_qty = rqst_qty;
    }

    public long getMedicine_adjust_dtl_id() {
        return medicine_adjust_dtl_id;
    }

    public void setMedicine_adjust_dtl_id(long medicine_adjust_dtl_id) {
        this.medicine_adjust_dtl_id = medicine_adjust_dtl_id;
    }

    public long getMedicine_adjust_id() {
        return medicine_adjust_id;
    }

    public void setMedicine_adjust_id(long medicine_adjust_id) {
        this.medicine_adjust_id = medicine_adjust_id;
    }

    public long getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(long medicine_id) {
        this.medicine_id = medicine_id;
    }

    public double getAdjust_qty() {
        return adjust_qty;
    }

    public void setAdjust_qty(double adjust_qty) {
        this.adjust_qty = adjust_qty;
    }

    public double getPre_rqst_qty() {
        return pre_rqst_qty;
    }

    public void setPre_rqst_qty(double pre_rqst_qty) {
        this.pre_rqst_qty = pre_rqst_qty;
    }

    public double getRcmnd_qty() {
        return rcmnd_qty;
    }

    public void setRcmnd_qty(double rcmnd_qty) {
        this.rcmnd_qty = rcmnd_qty;
    }

    public double getApproved_qty() {
        return approved_qty;
    }

    public void setApproved_qty(double approved_qty) {
        this.approved_qty = approved_qty;
    }
}
