package com.ispan.chufa.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "place")
public class PlaceBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long placeId;
	private String placeType;
	private String placeName;
	private String city;
	private String region;
	private String placeAddress;
	private double longitude; // 精確的經度
	private double latitude; // 精確的緯度

	@ElementCollection
	private List<String> photos; // 使用 List 儲存圖片 URL
	private String placePhone; // 使用 String 類型來處理電話號碼
	@Lob
	private char[] businessHours;

	private String placeInfo;
	private Double rating;
	private String website;
	private String bookingUrl;
	private BigDecimal price; // 使用 BigDecimal 處理價格
	private String accommodationType; // 旅宿類型
	private boolean reservation; // 只有在餐廳類型時使用

	@ManyToMany(mappedBy = "places") // 多對多，對應 MemberBean 的 places
	private List<MemberBean> members;

	public List<MemberBean> getMembers() {
		return members;
	}

	public void setMembers(List<MemberBean> members) {
		this.members = members;
	}

	@ManyToMany
	@JoinTable(name = "PlaceWithPosts", // 中介表名稱
			joinColumns = @JoinColumn(name = "fk_Place_Id", foreignKey = @ForeignKey(name = "placeId")), // PlaceBean關聯的外鍵
			inverseJoinColumns = @JoinColumn(name = "fk_Post_Id", foreignKey = @ForeignKey(name = "postid")) // PostBean關聯的外鍵
	)
	@JsonIgnoreProperties("places") // 避免貼文的 places 被序列化
	private Set<PostBean> posts = new HashSet<>();

	// getter and setter
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
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

	public String getPlacePhone() {
		return placePhone;
	}

	public void setPlacePhone(String placePhone) {
		this.placePhone = placePhone;
	}

	public char[] getBusinessHours() {
		return businessHours;
	}

	public void setBusinessHours(char[] businessHours) {
		this.businessHours = businessHours;
	}

	public String getPlaceInfo() {
		return placeInfo;
	}

	public void setPlaceInfo(String placeInfo) {
		this.placeInfo = placeInfo;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
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

	public boolean isReservation() {
		return reservation;
	}

	public void setReservation(boolean reservation) {
		this.reservation = reservation;
	}

	public Set<PostBean> getPosts() {
		return posts;
	}

	public void setPosts(Set<PostBean> posts) {
		this.posts = posts;
	}

}
