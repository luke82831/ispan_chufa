package com.ispan.chufa.domain;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "calendar")
public class CalendarBean {

	@Id
	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "week")
	private String week;

	@Column(name = "isHoliday")
	private boolean isHoliday;

	@Column(name = "description", nullable = true)
	private String description;

	@OneToMany(mappedBy = "calendar")
    private List<EventBean> eventBean;  // 一對多關聯
	
	// Constructors
	public CalendarBean() {
	}

	public CalendarBean(LocalDate date, String week, Boolean isHoliday, String description) {
		this.date = date;
		this.week = week;
		this.isHoliday = isHoliday;
		this.description = description;
	}

	// Getters and Setters
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public boolean isHoliday() {
		return isHoliday;
	}

	public void setHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Calendar [date=" + date + ", week=" + week + ", isHoliday=" + isHoliday +
				", description=" + description + "]";
	}
}
