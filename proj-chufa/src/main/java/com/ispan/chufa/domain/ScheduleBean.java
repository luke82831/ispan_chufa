package com.ispan.chufa.domain;

import java.time.LocalDate;
import java.util.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedule")

public class ScheduleBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動生成流水號
    @Column(name = "trip_id")
    private Long tripId;
    
    @Lob
    @Column(name = "cover_photo", columnDefinition = "VARBINARY(MAX)") 
    private byte[] coverPhoto; // 封面照片（Base64 編碼數據）

    @Column(name = "trip_name", nullable = false)
    private String tripName; // 行程名稱

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // 行程開始日期

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate; // 行程結束日期

    @ManyToOne
    @JoinColumn(name = "FK_user", referencedColumnName = "userid", nullable = false)
    private MemberBean user;

    // Constructors
    public ScheduleBean() {
    }

    public ScheduleBean(byte[] coverPhoto, String tripName, LocalDate startDate, LocalDate endDate, MemberBean userid) {
        this.coverPhoto = coverPhoto;
        this.tripName = tripName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = userid;
    }

    // Getters and Setters
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
    
    public String getCoverPhotoBase64() {
        return Base64.getEncoder().encodeToString(this.coverPhoto); // 將 byte[] 轉換成 Base64 字串
    }

    public void setCoverPhotoBase64(String base64String) {
        this.coverPhoto = Base64.getDecoder().decode(base64String); // 將 Base64 字串轉換回 byte[]
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

    public MemberBean getUser() {
        return user;
    }

    public void setUser(MemberBean user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ScheduleBean [tripId=" + tripId + ", coverPhoto=" + (coverPhoto != null ? "Base64 Data" : "null") + ", tripName=" + tripName +
                ", startDate=" + startDate + ", endDate=" + endDate + "]";
    }
}
