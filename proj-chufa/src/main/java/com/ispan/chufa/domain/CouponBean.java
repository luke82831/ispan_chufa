package com.ispan.chufa.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "coupon")
public class CouponBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(name = "coupon_code", unique = true, nullable = false)
    private String couponCode; // 優惠券代碼，要求唯一且不能為空
    @Column(name = "remaining")
    private Integer remaining;
    @Column(name = "Title")
    private String title;
    @Column(name = "Subtitle")
    private String subtitle;
    @Column(name = "Content")
    private String content;
    @Column(name = "State")
    private Boolean state;
    @Column(name = "Web")
    private String web;
    @Column(name = "Picture")
    private byte[] picture;
    @Column(name = "StartTime")
    private LocalDateTime startTime;
    @Column(name = "EndTime")
    private LocalDateTime endTime;

    @ManyToMany(mappedBy = "couponBean")
    @JsonIgnore
    private List<MyCouponBean> myCoupons;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "placeId")
    @JsonBackReference
    private PlaceBean place;

    // Getters and Setters

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean isState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public LocalDateTime getstartTime() {
        return startTime;
    }

    public void setstartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getendTime() {
        return endTime;
    }

    public void setendTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public List<MyCouponBean> getMyCoupons() {
        return myCoupons;
    }

    public void setMyCoupons(List<MyCouponBean> myCoupons) {
        this.myCoupons = myCoupons;
    }

    public PlaceBean getPlace() {
        return place;
    }

    public void setPlace(PlaceBean place) {
        this.place = place;
    }

    public Boolean getState() {
        return state;
    }

}
