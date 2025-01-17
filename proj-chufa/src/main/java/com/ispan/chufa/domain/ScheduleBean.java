package com.ispan.chufa.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedule")

public class ScheduleBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動生成流水號
    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "cover_photo")
    private String coverPhoto; // 封面照片（可以存儲圖片URL或者檔案路徑）

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

    public ScheduleBean(String coverPhoto, String tripName, LocalDate startDate, LocalDate endDate, MemberBean userid) {
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

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
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

    public MemberBean getUser() {
        return user;
    }

    public void setUser(MemberBean user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ScheduleBean [tripId=" + tripId + ", coverPhoto=" + coverPhoto + ", tripName=" + tripName +
                ", startDate=" + startDate + ", endDate=" + endDate + "]";
    }
}
