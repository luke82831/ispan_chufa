package com.ispan.chufa.dto;

import java.time.LocalDate;
import java.util.List;

import com.ispan.chufa.domain.EventBean;

public class JackScheduleDTO {
    private Long tripId; // 行程id
    private byte[] coverPhoto; // 封面照片（Base64 編碼數據）
    private String tripName; // 行程名稱
    private LocalDate startDate; // 行程開始日期
    private LocalDate endDate; // 行程結束日期
    private List<EventBean> events;
    // private MemberBean user;

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public byte[] getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(byte[] coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<EventBean> getEvents() {
        return events;
    }

    public void setEvents(List<EventBean> events) {
        this.events = events;
    }

    // public MemberBean getUser() {
    // return user;
    // }

    // public void setUser(MemberBean user) {
    // this.user = user;
    // }

}
