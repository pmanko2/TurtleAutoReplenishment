package com.example.turtleautoreplenishment;

import java.util.Observable;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

// singleton class to take care of gps location management
public class CurrentLocation extends Observable implements LocationListener
{

	private Location currentLocation;
	private LocationManager manager;
	private String locationProvider;
	private Criteria providerCriteria;
	private Context context;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	private CurrentLocation(){};
	private static CurrentLocation singleton;
	
	public static CurrentLocation getCurrentLocation()
	{
		if(singleton == null)
		{
			singleton = new CurrentLocation();
		}

		return singleton;
	}
	
	public void init(Context context)
	{
		providerCriteria = new Criteria();
		providerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		currentLocation = null;
		this.context = context;
		
		startUpdates();
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{
		if(isBetterLocation(location))
		{
			this.currentLocation = location;
			
			this.setChanged();
			this.notifyObservers(location);
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	// taken from google developer guide
		protected boolean isBetterLocation(Location location)
		{
			Location currentBestLocation = currentLocation;
			
			// if no location, any location is better
			if(currentBestLocation == null)
			{
				return true;
			}
			
			// Check whether the new location fix is newer or older
		    long timeDelta = location.getTime() - currentBestLocation.getTime();
		    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		    boolean isNewer = timeDelta > 0;

		    // If it's been more than two minutes since the current location, use the new location
		    // because the user has likely moved
		    if (isSignificantlyNewer) {
		        return true;
		    // If the new location is more than two minutes older, it must be worse
		    } else if (isSignificantlyOlder) {
		        return false;
		    }

		    // Check whether the new location fix is more or less accurate
		    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		    boolean isLessAccurate = accuracyDelta > 0;
		    boolean isMoreAccurate = accuracyDelta < 0;
		    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		    // Check if the old and new location are from the same provider
		    boolean isFromSameProvider = isSameProvider(location.getProvider(),
		            currentBestLocation.getProvider());
		    
		    // Determine location quality using a combination of timeliness and accuracy
		    if (isMoreAccurate) {
		        return true;
		    } else if (isNewer && !isLessAccurate) {
		        return true;
		    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
		        return true;
		    }
		    return false;
			
		}
		
		/** Checks whether two providers are the same */
		private boolean isSameProvider(String provider1, String provider2) {
		    if (provider1 == null) {
		      return provider2 == null;
		    }
		    return provider1.equals(provider2);
		}
	
	public void startUpdates()
	{
		locationProvider = manager.getBestProvider(providerCriteria, true);
		
		if(locationProvider != null)
		{
			manager.requestLocationUpdates(locationProvider,0,10,this);
		}
		else
		{
			Toast.makeText(context, "Location Services are Off", Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	public Location getLatestLocation()
	{
		return this.currentLocation;
	}
	
	public LocationManager getLocationManager()
	{
		return this.manager;
	}


}
