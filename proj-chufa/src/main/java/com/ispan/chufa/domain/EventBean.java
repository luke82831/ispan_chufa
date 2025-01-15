package com.ispan.chufa.domain;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "event")
public class EventBean {

	@Id
	@Column(name = "event_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventId; // 行程內容id (PK)

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate; // 行程開始日期

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate; // 行程結束日期

	@Column(name = "notes")
	private String notes; // 行程筆記

	@ManyToOne
	@JoinColumn(name = "FK_schedule", referencedColumnName = "trip_id", nullable = false)
	private ScheduleBean schedule; // 多對一關聯 (行程內容 -> 行程)

	@ManyToOne
	@JoinColumn(name = "FK_calendar", referencedColumnName = "date", nullable = false)
	private CalendarBean calendar; // 多對一關聯 (行程內容 -> 行事曆)

	@ManyToMany(mappedBy = "event")
	private List<EventXPlaceBean> eventXPlaceBeans;

	
	// Join Column

	// Constructors, getters, and setters

	public EventBean() {
	}

	public EventBean(LocalDate startDate, LocalDate endDate, String notes) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.notes = notes;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
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
	
	public ScheduleBean getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleBean schedule) {
        this.schedule = schedule;
    }

    // calendar 的 Getter 和 Setter
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
        return "EventBean [eventId=" + eventId + 
                ", startDate=" + startDate + 
                ", endDate=" + endDate + 
                ", notes=" + notes + 
                ", schedule=" + (schedule != null ? schedule.toString() : "null") + 
                ", calendar=" + (calendar != null ? calendar.toString() : "null") + "]";
    }

}
