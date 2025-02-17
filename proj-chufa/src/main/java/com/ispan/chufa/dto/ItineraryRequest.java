package com.ispan.chufa.dto;

import java.util.List;

public class ItineraryRequest {
    private Long eventId;
    private String date;
    private String startTime;
    private String endTime;
    private String notes;
    private List<EventXPlaceRequest> places;
    
    // Getter & Setter
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	    
}