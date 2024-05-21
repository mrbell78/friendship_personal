package ngo.friendship.satellite.views;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.utility.GPSUtility;
import ngo.friendship.satellite.utility.Utility;


public class LocationView  extends QuestionView  implements LocationListener{


	private TextView tvLocation;
	private LocationManager locationManager;
	private Location currentLocation;

	private Context context;
	public LocationView(Context context, Question question) {
		super(context, question);
		this.context=context;
		init();
		addCaptionField();
		addHintField();
		addInputField();
		startLocationSeeking();
	}

	protected void addInputField() {
        
		this.tvLocation = new TextView(context);
		this.tvLocation.setId(R.id.question_location_view);
		this.tvLocation.setPadding(5, 5, 5, 5);
		this.tvLocation.setTextSize(20);
		
		if(question.getUserInput()!=null && question.getUserInput().size()>0){
			setInputData(question.getUserInput().get(0));
		}else{
			setInputData("");
		}
		ll_body.addView(this.tvLocation);
		locationManager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE); 
		Log.e("Location", "TYPE:"+question.getLocationType() +" dealy:"+question.getDelay() +" Accuracy"+question.getAccuracy()+" LocationReqInterval:"+question.getLocationReqInterval());
	}
	
	private void startLocationSeeking() {
		String provider="";
		if("gps".equalsIgnoreCase(question.getLocationType())){
			provider=LocationManager.GPS_PROVIDER;
    	}else if("network".equalsIgnoreCase(question.getLocationType())){
    		provider=LocationManager.NETWORK_PROVIDER;
    	}else{
    		 Criteria criteria = new Criteria();
    		 criteria.setAccuracy(Criteria.ACCURACY_FINE);
    		 criteria.setAltitudeRequired(false);
    		 criteria.setBearingRequired(false);
    		 criteria.setCostAllowed(true);
    		 criteria.setPowerRequirement(Criteria.POWER_LOW);
    		 provider = locationManager.getBestProvider(criteria, true);
    	}
    	locationManager.removeUpdates(this);
		locationManager.requestLocationUpdates(provider,question.getLocationReqInterval(), 10, this);
	}
	
	
	
	public String getInputData() {
	   locationManager.removeUpdates(this);
       return (String)tvLocation.getTag();
	}
	public boolean isValid(boolean isSingleForm) {
		String output=(String)tvLocation.getTag();
		double latitude  = Utility.parseDouble(output.split("##")[0]);
		double longitude  = Utility.parseDouble(output.split("##")[1]);
        return !(question.isRequired() && (longitude == 0.0 || latitude == 0.0));
    }
	public void setInputData(String data) {
	   String[] datas=data.split("##");
	   if(datas.length==2){
		   tvLocation.setText(datas[0]+"\n"+datas[1]);
		   tvLocation.setTag(datas[0]+"##"+datas[1]);
	   }else{
		   tvLocation.setText("0.00\n0.00");
		   tvLocation.setTag("0.00##0.00");
	   }
	}

	public ArrayList<String> getInputDataList() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public View getInputView() {
		// TODO Auto-generated method stub
		return tvLocation;
	}
  
	@Override
	public void replaceBody(Object data) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onLocationChanged(Location location) {
		if(GPSUtility.isBetterLocation(location, currentLocation, question.getDelay(), question.getAccuracy())){
			currentLocation=location;
		}
		if(currentLocation!=null){
			String output=currentLocation.getLatitude()+"##"+currentLocation.getLongitude();
			tvLocation.setTag(output);
			tvLocation.setText(output.replace("##", "\n")+"\n"+currentLocation.getProvider() +" "+currentLocation.getAccuracy());
		}
		startLocationSeeking();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	

}
