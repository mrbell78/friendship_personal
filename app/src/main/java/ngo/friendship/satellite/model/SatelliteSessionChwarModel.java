package ngo.friendship.satellite.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SatelliteSessionChwarModel {

    int SAT_SESSION_CHW_ID;
    long SAT_SESSION_ID;
    long USER_ID;
    long LOCATION_ID;

    public int getSAT_SESSION_CHW_ID() {
        return SAT_SESSION_CHW_ID;
    }

    public void setSAT_SESSION_CHW_ID(int SAT_SESSION_CHW_ID) {
        this.SAT_SESSION_CHW_ID = SAT_SESSION_CHW_ID;
    }

    public long getSAT_SESSION_ID() {
        return SAT_SESSION_ID;
    }

    public void setSAT_SESSION_ID(long SAT_SESSION_ID) {
        this.SAT_SESSION_ID = SAT_SESSION_ID;
    }

    public long getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(long USER_ID) {
        this.USER_ID = USER_ID;
    }

    public long getLOCATION_ID() {
        return LOCATION_ID;
    }

    public void setLOCATION_ID(long LOCATION_ID) {
        this.LOCATION_ID = LOCATION_ID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SAT_SESSION_ID", SAT_SESSION_ID);
            jsonObject.put("USER_ID", USER_ID);
            jsonObject.put("LOCATION_ID", LOCATION_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
