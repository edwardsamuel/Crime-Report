package com.ganesia.crimereport.models;

import android.location.Address;
import android.text.TextUtils;

import java.util.Locale;

import com.ganesia.crimereport.activities.GeocodingActivity;

public class CustomAddress extends Address {

	public CustomAddress(Locale locale) {
		super(locale);
	}

	public CustomAddress(Address address) {
		super(address.getLocale());
		
		for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
			super.setAddressLine(i, address.getAddressLine(i));
		}
		
		super.setFeatureName(address.getFeatureName());
		super.setAdminArea(address.getAdminArea());
		super.setSubAdminArea(address.getSubAdminArea());
		super.setLocality(address.getLocality());
		super.setSubLocality(address.getSubLocality());
		super.setThoroughfare(address.getThoroughfare());
		super.setSubThoroughfare(address.getSubThoroughfare());
		super.setPostalCode(address.getPostalCode());
		super.setCountryCode(address.getCountryCode());
		super.setCountryName(address.getCountryName());
		super.setLatitude(address.getLatitude());
		super.setLongitude(address.getLongitude());
		super.setPhone(address.getPhone());
		super.setUrl(address.getUrl());
		super.setExtras(address.getExtras());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (getFeatureName() != null) {
			sb.append(getFeatureName());
		}
		if (getThoroughfare() != null
				&& !getThoroughfare().equals(getFeatureName())) {
			sb.append(", " + getThoroughfare());
		}
		if (getSubThoroughfare() != null
				&& !getSubThoroughfare().equals(getFeatureName())) {
			sb.append(", " + getSubThoroughfare());
		}
		if (getAdminArea() != null && !getAdminArea().equals(getFeatureName())) {
			sb.append(", " + getAdminArea());
		}
		if (getSubAdminArea() != null
				&& !getSubAdminArea().equals(getFeatureName())) {
			sb.append(", " + getSubAdminArea());
		}
		if (getLocality() != null && !getLocality().equals(getFeatureName())) {
			sb.append(", " + getLocality());
		}
		if (TextUtils.isEmpty(sb.toString())) {
			int maxLines = (GeocodingActivity.MAX_RESULTS > getMaxAddressLineIndex()) ? getMaxAddressLineIndex()
					: GeocodingActivity.MAX_RESULTS;
			sb.append(getAddressLine(0));
			for (int i = 1; i < maxLines; i++) {
				if (getAddressLine(i) != null) {
					sb.append(", " + getAddressLine(i));
				}
			}
		}
		return sb.toString();
	}

	public String getStringAddress(boolean multiline) {
		int maxLines = (GeocodingActivity.MAX_RESULTS > getMaxAddressLineIndex()) ? getMaxAddressLineIndex()
				: GeocodingActivity.MAX_RESULTS;

		if (maxLines >= 0) {

			String result = getAddressLine(0);
			this.getAdminArea();
			if (multiline) {
				for (int i = 1; i < maxLines; i++) {
					if (i == 1) {
						result += "\n";
						if (getAddressLine(i) != null) {
							result += getAddressLine(i);
						}
					} else if (i == 2) {
						result += "\n";
						if (getAddressLine(i) != null) {
							result += getAddressLine(i);
						}
					} else {
						if (getAddressLine(i) != null) {
							result += ", " + getAddressLine(i);
						}
					}
				}
			} else {
				for (int i = 1; i < maxLines; i++) {
					if (getAddressLine(i) != null) {
						result += ", " + getAddressLine(i);
					}
				}
			}

			return result;
		} else {
			return null;
		}
	}
}