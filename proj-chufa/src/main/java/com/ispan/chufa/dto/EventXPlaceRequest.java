package com.ispan.chufa.dto;

public class EventXPlaceRequest {
    private Long eventmappingId; 
    private Long placeId;
    private Integer placeOrder;
    private String travelTime;
    private String stayDuration;
    private String notes;
    
    // Getter & Setter
    
	public Long getEventmappingId() {
		return eventmappingId;
	}
	public void setEventmappingId(Long eventmappingId) {
		this.eventmappingId = eventmappingId;
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
	public String getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}
	public String getStayDuration() {
		return stayDuration;
	}
	public void setStayDuration(String stayDuration) {
		this.stayDuration = stayDuration;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

}
