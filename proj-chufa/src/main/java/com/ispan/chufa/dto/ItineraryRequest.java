package com.ispan.chufa.dto;

import java.time.LocalDate;
import java.util.List;

public class ItineraryRequest {
    private Long eventId;
    private String date;      // 🔹 這是前端傳過來的日期，格式應該為 `yyyy-MM-dd`
    private String startTime;
    private String endTime;
    private String notes;
    private List<EventXPlaceRequest> places;

    // 🔹 轉換 `date` 為 `LocalDate`
    public LocalDate getLocalDate() {
        return LocalDate.parse(date);  // 🔥 確保格式正確
    }

    // Getters & Setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getDate() {
        if (this.date == null) {
            return null;
        }
        try {
            LocalDate parsedDate = LocalDate.parse(this.date);
            return parsedDate.toString(); // ✅ 轉換為 `yyyy-MM-dd`
        } catch (Exception e) {
            throw new RuntimeException("日期格式錯誤: " + this.date);
        }
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<EventXPlaceRequest> getPlaces() {
        return places;
    }

    public void setPlaces(List<EventXPlaceRequest> places) {
        this.places = places;
    }
}
