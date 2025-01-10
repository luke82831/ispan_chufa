package com.ispan.chufa.domain;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class EventBean {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "event_content_id")
	    private Long eventContentId; // 行程內容id (PK)

	    @ManyToOne
	    @JoinColumn(name = "schedule_id", nullable = false)
	    private ScheduleBean schedule; // 行程id (FK, 多對一)

	    @ManyToOne
	    @JoinColumn(name = "calendar_date", nullable = false)
	    private CalendarBean calendar; // 行事曆_日期 (FK, 多對一)

	    @Column(name = "start_date", nullable = false)
	    private LocalDate startDate; // 行程開始日期

	    @Column(name = "end_date", nullable = false)
	    private LocalDate endDate; // 行程結束日期

	    @Column(name = "notes")
	    private String notes; // 行程筆記

	    // Constructors, getters, and setters

	    public EventBean() {
	    }

	    public EventBean(ScheduleBean schedule, CalendarBean calendar, LocalDate startDate, LocalDate endDate, String notes) {
	        this.schedule = schedule;
	        this.calendar = calendar;
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
	        return "EventContent [eventContentId=" + eventContentId + ", schedule=" + schedule +
	               ", calendar=" + calendar + ", startDate=" + startDate + 
	               ", endDate=" + endDate + ", notes=" + notes + "]";
	    }
}
