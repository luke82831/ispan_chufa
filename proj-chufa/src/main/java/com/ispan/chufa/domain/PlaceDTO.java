package com.ispan.chufa.domain;

public class PlaceDTO {
    private String PlaceName;
    private String PlaceAddress;
    private double Longitude;
    private double Latitude;

    // getters and setters
	public String getPlaceName() {
		return PlaceName;
	}
	public void setPlaceName(String placeName) {
		PlaceName = placeName;
	}
	public String getPlaceAddress() {
		return PlaceAddress;
	}
	public void setPlaceAddress(String placeAddress) {
		PlaceAddress = placeAddress;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double longitude) {
		Longitude = longitude;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
    
}
