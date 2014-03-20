package com.sharethis.loopy.sdk;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.sharethis.loopy.sdk.util.AppUtils;

/**
 * @author Jason Polites
 */
public class Geo implements LocationListener  {

	private double lat;
	private double lon;

    public void onStop(Context context) {
        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            lm.removeUpdates(this);
        }
        catch (Throwable e) {
            Logger.w(e);
        }
	}

    public void onStart(Context context) {
        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            lm.requestLocationUpdates(lm.getBestProvider(criteria, true), 0, 0, this);
        }
        catch (Throwable e) {
            Logger.w(e);
        }
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	static boolean hasPermission(Context context) {
        String permission0 = "android.permission.ACCESS_FINE_LOCATION";
        String permission1 = "android.permission.ACCESS_COARSE_LOCATION";
        return AppUtils.getInstance().hasOrPermission(context, permission0, permission1);
	}

    @Override
	public void onLocationChanged(Location location) {
		this.lon = location.getLongitude();
		this.lat = location.getLatitude();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onProviderDisabled(String provider) {}
}
