package ngo.friendship.satellite.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SatelliteSessionModel {

    long satelliteSessionId;
    long satSessionDate;
    long userId;
    long satSessionLocationId;
    long createDate;
    String dataSent;

    String state;
    String version;
    long update_date;
    long org_id;

    public long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(long org_id) {
        this.org_id = org_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(long update_date) {
        this.update_date = update_date;
    }

    ArrayList<SatelliteSessionChwarModel> satelliteSessionChwarDetailsList;


    public ArrayList<SatelliteSessionChwarModel> getSatelliteSessionChwarDetailsList() {
        return satelliteSessionChwarDetailsList;
    }

    public void setSatelliteSessionChwarDetailsList(ArrayList<SatelliteSessionChwarModel> satelliteSessionChwarDetailsList) {
        this.satelliteSessionChwarDetailsList = satelliteSessionChwarDetailsList;
    }

    public String getDataSent() {
        return dataSent;
    }

    public void setDataSent(String dataSent) {
        this.dataSent = dataSent;
    }

    public long getSatelliteSessionId() {
        return satelliteSessionId;
    }

    public void setSatelliteSessionId(long satelliteSessionId) {
        this.satelliteSessionId = satelliteSessionId;
    }

    public long getSatSessionDate() {
        return satSessionDate;
    }

    public void setSatSessionDate(long satSessionDate) {
        this.satSessionDate = satSessionDate;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getSatSessionLocationId() {
        return satSessionLocationId;
    }

    public void setSatSessionLocationId(long satSessionLocationId) {
        this.satSessionLocationId = satSessionLocationId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("satSessionDate", satSessionDate);
            jsonObject.put("userId", userId);
            jsonObject.put("satSessionLocationId", satSessionLocationId);
            jsonObject.put("createDate", createDate);
            jsonObject.put("dataSent", dataSent);
            jsonObject.put("state", state);
            jsonObject.put("satelliteSessionChwarDetailsList", listToJsonArray(satelliteSessionChwarDetailsList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONArray listToJsonArray(List<SatelliteSessionChwarModel> sessionCharModel) {
        JSONArray jsonArray = new JSONArray();
        for (SatelliteSessionChwarModel singleObject : sessionCharModel) {
            jsonArray.put(singleObject.toJson());
        }
        return jsonArray;
    }

}
