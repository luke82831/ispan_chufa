package com.ispan.chufa.domain;

import java.time.LocalTime;

import org.springframework.beans.factory.parsing.Location;
import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class EventXPlaceBean {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "event_location_mapping_id")
	    private Long eventLocationMappingId; // PK with auto-increment

	    @ManyToOne
	    @JoinColumn(name = "event_content_id", nullable = false)
	    private EventBean eventContentId; // FK to EventContent

	    @ManyToOne
	    @JoinColumn(name = "PlaceId", nullable = false)
	    private PlaceBean placeId; // FK to Location

	    @Column(name = "location_order", nullable = false)
	    private Integer locationOrder; // Location order

	    @Column(name = "custom_arrival_time")
	    private LocalTime customArrivalTime; // Custom arrival time

	    @Column(name = "stay_duration")
	    private Integer stayDuration; // Stay duration in minutes

	    @Column(name = "location_note")
	    private String locationNote; // Notes for the location

	    // Constructors
	    public EventXPlaceBean() {}

	    public EventXPlaceBean(EventBean eventContent, PlaceBean placeId, Integer locationOrder, 
	                                 LocalTime customArrivalTime, Integer stayDuration, String locationNote) {
	        this.eventContentId = eventContent;
	        this.placeId = placeId;
	        this.locationOrder = locationOrder;
	        this.customArrivalTime = customArrivalTime;
	        this.stayDuration = stayDuration;
	        this.locationNote = locationNote;
	    }

	    // Getters and Setters
	    public Long getEventLocationMappingId() {
	        return eventLocationMappingId;
	    }

	    public void setEventLocationMappingId(Long eventLocationMappingId) {
	        this.eventLocationMappingId = eventLocationMappingId;
	    }

	    public EventBean getEventContentId() {
	        return eventContentId;
	    }

	    public void setEventContentId(EventBean eventContentId) {
	        this.eventContentId = eventContentId;
	    }

	    public PlaceBean getPlaceId() {
	        return placeId;
	    }

	    public void setPlaceId(PlaceBean placeId) {
	        this.placeId = placeId;
	    }

	    public Integer getLocationOrder() {
	        return locationOrder;
	    }

	    public void setLocationOrder(Integer locationOrder) {
	        this.locationOrder = locationOrder;
	    }

	    public LocalTime getCustomArrivalTime() {
	        return customArrivalTime;
	    }

	    public void setCustomArrivalTime(LocalTime customArrivalTime) {
	        this.customArrivalTime = customArrivalTime;
	    }

	    public Integer getStayDuration() {
	        return stayDuration;
	    }

	    public void setStayDuration(Integer stayDuration) {
	        this.stayDuration = stayDuration;
	    }

	    public String getLocationNote() {
	        return locationNote;
	    }

	    public void setLocationNote(String locationNote) {
	        this.locationNote = locationNote;
	    }

	    @Override
	    public String toString() {
	        return "EventLocationMappingBean{" +
	                "eventLocationMappingId=" + eventLocationMappingId +
	                ", eventContentId=" + eventContentId +
	                ", placeId=" + placeId +
	                ", locationOrder=" + locationOrder +
	                ", customArrivalTime=" + customArrivalTime +
	                ", stayDuration=" + stayDuration +
	                ", locationNote='" + locationNote + '\'' +
	                '}';
	    }
	
}
