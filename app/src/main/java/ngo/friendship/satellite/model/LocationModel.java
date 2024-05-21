package ngo.friendship.satellite.model;

/**
 * Created by Yeasin Ali on 4/12/2023.
 * friendship.ngo
 * yeasinali@friendship.ngo
 */
public class LocationModel {

    public static final String MODEL_NAME = "location";

    public static final String LOCATION_ID = "LOCATION_ID";
    public static final String LOCATION_CODE = "LOCATION_CODE";
    public static final String LOCATION_NAME = "LOCATION_NAME";
    public static final String LOCATION_TYPE_ID = "LOCATION_TYPE_ID";
    public static final String COVERAGE_AREA_KM = "COVERAGE_AREA_KM";
    public static final String PARENT_LOCATION_ID = "PARENT_LOCATION_ID";
    public static final String STATE = "STATE";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    //Column(name = "LOCATION_ID")
    private long locationId;
    //Column(name = "LOCATION_CODE")
    private String locationCode;
    //Column(name = "LOCATION_NAME")
    private String locationName;
    //Column(name = "LOCATION_TYPE_ID")
    private int locationTypeId;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    //Column(name = "COVERAGE_AREA_KM")
    private Double coverageAreaKm;
    //Column(name = "NEARBY_LOCATION_ID")
    private Integer nearbyLocationId;
    //Column(name = "PARENT_LOCATION_ID")
    private Integer parentLocationId;

    private boolean selected;


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getLocationTypeId() {
        return locationTypeId;
    }

    public void setLocationTypeId(int locationTypeId) {
        this.locationTypeId = locationTypeId;
    }

    public Double getCoverageAreaKm() {
        return coverageAreaKm;
    }

    public void setCoverageAreaKm(Double coverageAreaKm) {
        this.coverageAreaKm = coverageAreaKm;
    }

    public Integer getNearbyLocationId() {
        return nearbyLocationId;
    }

    public void setNearbyLocationId(Integer nearbyLocationId) {
        this.nearbyLocationId = nearbyLocationId;
    }

    public Integer getParentLocationId() {
        return parentLocationId;
    }

    public void setParentLocationId(Integer parentLocationId) {
        this.parentLocationId = parentLocationId;
    }
}
