package com.ispan.chufa.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
		
		

		 public CalendarBean() {
		    	
		    }
		    public CalendarBean(
		    		LocalDate date, String week, Boolean isHoliday, String description
					) {
		    	this.date = date;
		        this.week = week;
		        this.isHoliday = isHoliday;
		        this.description = description;
		    }
		    
		    // Getters and Setters...
		    
		    
		    // date 的 Getter 和 Setter
		    public LocalDate getDate() {
		        return date;
		    }

		    public void setDate(LocalDate formattedDate) {
		        this.date = formattedDate;
		    }

		    // week 的 Getter 和 Setter
		    public String getWeek() {
		        return week;
		    }

		    public void setWeek(String week) {
		        this.week = week;
		    }

		    // isHoliday 的 Getter 和 Setter
		    public boolean isIsHoliday() {
		        return isHoliday;
		    }

		    public void setIsHoliday(boolean isHoliday) {
		        this.isHoliday = isHoliday;
		    }

		    // description 的 Getter 和 Setter
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

