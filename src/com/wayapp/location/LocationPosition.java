
package com.wayapp.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

/**
 * @author raubreak
 *
 */
public class LocationPosition {

	Context activity = null;

	public LocationPosition(Context activity) {

		this.activity = activity;

	}

	/*
	 * Funciones geolocalización
	 */
	/**
	 * @return
	 */
	public Location getLocation() {

		LocationManager locationmanager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationmanager.getBestProvider(criteria, true);
		Location location = null;
		if (provider != null) {
			location = locationmanager.getLastKnownLocation(provider);

			if (location != null) {
				/* La Latitud la tenemos en location.getLatitude() */
				/* La Longitud la tenemos en location.getLongitude() */
			}
		} else {
			/* La longitud y la latiud es 0 */
		}
		return location;
	}

	// DIRECCION EN STRING
	public String getAddress(Context cnt, Location loc) {
		String address = "";
		Geocoder geoCoder = new Geocoder(cnt, Locale.getDefault());
		try {
			if (loc != null) {
				List<Address> addresses = geoCoder.getFromLocation(loc
						.getLatitude(), loc.getLongitude(), 1);

				if (addresses.size() > 0) {
					for (int i = 0; i < addresses.get(0)
							.getMaxAddressLineIndex(); i++) {
						address += addresses.get(0).getAddressLine(i) + " ";
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return address;
	}

}
