package com.ispan.chufa.domain;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "event")
public class EventBean {

	@Id
	@Column(name = "event_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventId; // 行程內容id (PK)

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime; // 行程開始日期

	@Column(name = "end_time", nullable = false)
	private LocalTime endTime; // 行程結束日期

	@Column(name = "notes")
	private String notes; // 行程筆記

	@ManyToOne
	@JoinColumn(name = "FK_schedule", referencedColumnName = "trip_id", nullable = false)
	private ScheduleBean schedule; // 多對一關聯 (行程內容 -> 行程)

	@ManyToOne
	@JoinColumn(name = "FK_calendar", referencedColumnName = "date", nullable = false)
	private CalendarBean calendar; // 多對一關聯 (行程內容 -> 行事曆)
	
    @OneToMany(mappedBy = "event")
    private List<EventXPlaceBean> eventXPlaceBeans;  // 一對多關聯

	// Constructors, getters, and setters

	public EventBean() {
	}

	public EventBean(LocalTime startTime, LocalTime endTime, String notes) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.notes = notes;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public ScheduleBean getSchedule() {
		return schedule;
	}

	public void setSchedule(ScheduleBean schedule) {
		this.schedule = schedule;
	}

	public CalendarBean getCalendar() {
		return calendar;
	}

	public void setCalendar(CalendarBean calendar) {
		this.calendar = calendar;
	}

	public List<EventXPlaceBean> getEventXPlaceBeans() {
		return eventXPlaceBeans;
	}

	public void setEventXPlaceBeans(List<EventXPlaceBean> eventXPlaceBeans) {
		this.eventXPlaceBeans = eventXPlaceBeans;
	}

	@Override
	public String toString() {
		return "EventBean [eventId=" + eventId + ", startTime=" + startTime + ", endTime=" + endTime + ", notes="
				+ notes + ", schedule=" + schedule + ", calendar=" + calendar + ", eventXPlaceBeans=" + eventXPlaceBeans
				+ "]";
	}

}
