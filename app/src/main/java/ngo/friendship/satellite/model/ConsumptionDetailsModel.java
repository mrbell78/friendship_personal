package ngo.friendship.satellite.model;

public class ConsumptionDetailsModel {

    private long med_consump_dtl_id;
    private long med_consump_id;
    private long med_id;
    private double qty;
    private double price;

    public long getMed_consump_dtl_id() {
        return med_consump_dtl_id;
    }

    public void setMed_consump_dtl_id(long med_consump_dtl_id) {
        this.med_consump_dtl_id = med_consump_dtl_id;
    }

    public long getMed_consump_id() {
        return med_consump_id;
    }

    public void setMed_consump_id(long med_consump_id) {
        this.med_consump_id = med_consump_id;
    }

    public long getMed_id() {
        return med_id;
    }

    public void setMed_id(long med_id) {
        this.med_id = med_id;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
