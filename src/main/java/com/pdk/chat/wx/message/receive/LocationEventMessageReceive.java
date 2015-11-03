package com.pdk.chat.wx.message.receive;

/**
 * Created by kangss on 2015/8/25
 */
public class LocationEventMessageReceive extends EventMessageReceive {
	private String Latitude;
	private String Longitude;
	private String Precision;

	private String locationInfo;


	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		Latitude = latitude;
	}

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		Longitude = longitude;
	}

	public String getPrecision() {
		return Precision;
	}

	public void setPrecision(String precision) {
		Precision = precision;
	}

	public String getLocationInfo() {
		return locationInfo;
	}

	public void setLocationInfo(String locationInfo) {
		this.locationInfo = locationInfo;
	}

	@Override
	public String toString() {
		return "LocationEventMessageReceive{" +
				"Precision='" + Precision + '\'' +
				", Longitude='" + Longitude + '\'' +
				", Latitude='" + Latitude + '\'' +
				"} " + super.toString();
	}
}

