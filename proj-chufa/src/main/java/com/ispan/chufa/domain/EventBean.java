package com.ispan.chufa.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "event")
public class EventBean {

	@Id
	@Column(name = "event_content_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventContentId; // 行程內容id (PK)

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate; // 行程開始日期

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate; // 行程結束日期

	@Column(name = "notes")
	private String notes; // 行程筆記

	// Constructors, getters, and setters

	public EventBean() {
	}

	public EventBean(LocalDate startDate, LocalDate endDate, String notes) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.notes = notes;
	}

	public Long getEventContentId() {
		return eventContentId;
	}

	public void setEventContentId(Long eventContentId) {
		this.eventContentId = eventContentId;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return "EventContent [eventContentId=" + eventContentId + ", startDate=" + startDate +
				", endDate=" + endDate + ", notes=" + notes + "]";
	}
}
