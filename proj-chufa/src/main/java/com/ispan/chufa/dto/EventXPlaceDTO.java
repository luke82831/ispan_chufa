package com.ispan.chufa.dto;

import java.time.LocalTime;

public class EventXPlaceDTO {
    private Long eventId;
    private Long placeId;
    private Integer placeOrder;
    private LocalTime travelTime;
    private LocalTime stayDuration;
    private String notes;

    // Constructors
    public EventXPlaceDTO() {}

    public EventXPlaceDTO(Long eventId, Long placeId, Integer placeOrder, LocalTime travelTime,
                          LocalTime stayDuration, String notes) {
        this.eventId = eventId;
        this.placeId = placeId;
        this.placeOrder = placeOrder;
        this.travelTime = travelTime;
        this.stayDuration = stayDuration;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public Integer getPlaceOrder() {
        return placeOrder;
    }

    public void setPlaceOrder(Integer placeOrder) {
        this.placeOrder = placeOrder;
    }

    public LocalTime getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(LocalTime travelTime) {
        this.travelTime = travelTime;
    }

    public LocalTime getStayDuration() {
        return stayDuration;
    }

    public void setStayDuration(LocalTime stayDuration) {
        this.stayDuration = stayDuration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}