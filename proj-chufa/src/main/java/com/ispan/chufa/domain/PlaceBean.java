package com.ispan.chufa.domain;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "place")
public class PlaceBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long placeId;
	private String googlemapPlaceId;
	private String placeType;
	private String placeName;
	private String city;
	private String region;
	private String placeAddress;
	private double longitude; // 精確的經度
	private double latitude; // 精確的緯度

	@Lob
	private String photos; // 儲存圖片 URL 的 JSON 字串
	private String placePhone; // 使用 String 類型來處理電話號碼
	@Lob
	private char[] businessHours;

	private String placeInfo;
	private Double rating;
	private String website;
	private String bookingUrl;
	private Integer priceLevel; 
	private String accommodationType; // 旅宿類型
	private boolean reservation; // 只有在餐廳類型時使用
	private boolean isClosed;

	//  一對多
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties({"myCoupons", "place"})
	private List<CouponBean> coupons;

    @OneToMany(mappedBy = "place")
    private List<EventXPlaceBean> eventXPlaceBeans;  // 一對多關聯
	
	@ManyToMany(mappedBy = "place") // 多對多，對應 MemberBean 的 places
	private List<MemberBean> members;
	
	@ManyToMany
	@JoinTable(name = "placewithposts", // 中介表名稱
			joinColumns = @JoinColumn(name = "fk_Place_Id", foreignKey = @ForeignKey(name = "placeId")), // PlaceBean關聯的外鍵
			inverseJoinColumns = @JoinColumn(name = "fk_Post_Id", foreignKey = @ForeignKey(name = "postid")) // PostBean關聯的外鍵
	)
	@JsonIgnoreProperties("places") // 避免貼文的 places 被序列化
	private Set<PostBean> posts = new HashSet<>();
	

	// getter and setter
	public Long getPlaceId() {
		return placeId;
	}

	public void setPlaceId(Long placeId) {
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

	// 設置圖片 URL 列表的方法，將 List<String> 轉換為 JSON 字串
    public void setPhotos(List<String> photos) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.photos = objectMapper.writeValueAsString(photos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 轉換 JSON 字串為 List<String> 的方法
    public List<String> getPhotos() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(photos, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

	public Integer getPriceLevel() {
		return priceLevel;
	}

	public void setPriceLevel(Integer priceLevel) {
		this.priceLevel = priceLevel;
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
	
	public boolean getClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public Set<PostBean> getPosts() {
		return posts;
	}

	public void setPosts(Set<PostBean> posts) {
		this.posts = posts;
	}
	
	public List<MemberBean> getMembers() {
		return members;
	}
	
	public void setMembers(List<MemberBean> members) {
		this.members = members;
	}

	public List<CouponBean> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<CouponBean> coupons) {
		this.coupons = coupons;
	}

	public List<EventXPlaceBean> getEventXPlaceBeans() {
		return eventXPlaceBeans;
	}

	public void setEventXPlaceBeans(List<EventXPlaceBean> eventXPlaceBeans) {
		this.eventXPlaceBeans = eventXPlaceBeans;
	}

	public String getGooglemapPlaceId() {
		return googlemapPlaceId;
	}

	public void setGooglemapPlaceId(String googlemapPlaceId) {
		this.googlemapPlaceId = googlemapPlaceId;
	}
	
}
