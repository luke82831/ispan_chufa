package com.ispan.chufa.domain;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "calendar")
public class CalendarBean {

    @Id
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "week")
    private String week;

    // 將欄位名稱改為 holiday，但透過 @Column 保持 DB 欄位名稱為 isHoliday
    @Column(name = "isHoliday")
    private boolean holiday;

    @Column(name = "description", nullable = true)
    private String description;

    // Constructors
    public CalendarBean() {
    }

    public CalendarBean(LocalDate date, String week, boolean holiday, String description) {
        this.date = date;
        this.week = week;
        this.holiday = holiday;
        this.description = description;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    @JsonProperty("isHoliday")
    public boolean isHoliday() {
        return holiday;
    }

    @JsonProperty("isHoliday")
    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CalendarBean [date=" + date + ", week=" + week + ", holiday=" + holiday
                + ", description=" + description + "]";
    }
}
