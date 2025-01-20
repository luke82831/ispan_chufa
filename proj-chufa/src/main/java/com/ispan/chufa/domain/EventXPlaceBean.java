package com.ispan.chufa.domain;

import java.time.LocalTime;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "eventXplace")
public class EventXPlaceBean {

	@Id
	@Column(name = "Eventmapping_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventmappingId;

	@Column(name = "place_order")
	private Integer placeOrder;

	@Column(name = "arrival_time")
	private LocalTime arrivalTime;

	@Column(name = "stay_duration")
	private Integer stayDuration;

	@Column(name = "notes")
	@Lob
	private char[] notes;
	
	@ManyToOne
	@JsonIgnoreProperties("eventXPlaceBeans")
	@JoinColumn(name = "fk_event_id", referencedColumnName = "event_id", nullable = false)
	private EventBean event; // FK_Event_行程內容id，多對多(行程內容VS地點)

	@ManyToOne
	@JsonIgnoreProperties("eventXPlaceBeans")
	@JoinColumn(name = "fk_place_id", referencedColumnName = "placeId", nullable = false)
	private PlaceBean place; // FK_地點_地點id，多對多(行程內容VS地點)

	
	// Constructors, getters, setters, and toString()
	
	public EventXPlaceBean() {
	}

	public EventXPlaceBean(EventBean event, PlaceBean place, Integer placeOrder, LocalTime arrivalTime,
			Integer stayDuration, char[] notes) {
		this.event = event;
		this.place = place;
		this.placeOrder = placeOrder;
		this.arrivalTime = arrivalTime;
		this.stayDuration = stayDuration;
		this.notes = notes;
	}

	public Long getMappingId() {
		return eventmappingId;
	}

	public void setMappingId(Long mappingId) {
		this.eventmappingId = mappingId;
	}

	public EventBean getEvent() {
		return event;
	}

	public void setEvent(EventBean event) {
		this.event = event;
	}

	public PlaceBean getPlace() {
		return place;
	}

	public void setPlace(PlaceBean place) {
		this.place = place;
	}

	public Integer getPlaceOrder() {
		return placeOrder;
	}

	public void setPlaceOrder(Integer placeOrder) {
		this.placeOrder = placeOrder;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Integer getStayDuration() {
		return stayDuration;
	}

	public void setStayDuration(Integer stayDuration) {
		this.stayDuration = stayDuration;
	}

	public char[] getNotes() {
		return notes;
	}

	public void setNotes(char[] notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return "EventXPlaceBean [eventmappingId=" + eventmappingId + ", event=" + event + ", place=" + place
				+ ", placeOrder=" + placeOrder + ", arrivalTime=" + arrivalTime + ", stayDuration=" + stayDuration
				+ ", notes=" + Arrays.toString(notes) + "]";
	}
}
