package com.ispan.chufa.domain;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "place")
public class PlaceBean {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long placeId;
    private String placeType;
    private String placeName;
    private String placeAddress;
    private double longitude; // 精確的經度
    private double latitude; // 精確的緯度

    @ElementCollection
    private List<String> photos; // 使用 List 儲存圖片 URL
    private String placePhone; // 使用 String 類型來處理電話號碼
    private String businessHours;
    private String placeInfo;
    private Double rating;
    private String website;
    private String bookingUrl;
    private BigDecimal price; // 使用 BigDecimal 處理價格
    private String accommodationType; // 旅宿類型
    private String mealTime; // 用餐時間
    private String reservation; // 只有在餐廳類型時使用
    
    @OneToMany(mappedBy = "place")
    private List<PlacePostBean> placePosts;  // 這裡是一對多關聯

    public List<PlacePostBean> getPlacePosts() {
		return placePosts;
	}
	public void setPlacePosts(List<PlacePostBean> placePosts) {
		this.placePosts = placePosts;
	}
	public long getPlaceId() {
		return placeId;
	}
	public void setPlaceId(long placeId) {
		this.placeId = placeId;
	}
	public String getPlaceType() {
		return placeType;
	}
	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}
	public String getPlaceName() {
		return placeName;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public String getPlaceAddress() {
		return placeAddress;
	}
	public void setPlaceAddress(String placeAddress) {
		this.placeAddress = placeAddress;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public List<String> getPhotos() {
		return photos;
	}
	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public String getPlacePhone() {
		return placePhone;
	}
	public void setPlacePhone(String placePhone) {
		this.placePhone = placePhone;
	}
	public String getBusinessHours() {
		return businessHours;
	}
	public void setBusinessHours(String businessHours) {
		this.businessHours = businessHours;
	}
	public String getPlaceInfo() {
		return placeInfo;
	}
	public void setPlaceInfo(String placeInfo) {
		this.placeInfo = placeInfo;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getBookingUrl() {
		return bookingUrl;
	}
	public void setBookingUrl(String bookingUrl) {
		this.bookingUrl = bookingUrl;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getAccommodationType() {
		return accommodationType;
	}
	public void setAccommodationType(String accommodationType) {
		this.accommodationType = accommodationType;
	}
	public String getMealTime() {
		return mealTime;
	}
	public void setMealTime(String mealTime) {
		this.mealTime = mealTime;
	}
	public String getReservation() {
		return reservation;
	}
	public void setReservation(String reservation) {
		this.reservation = reservation;
	}
}

