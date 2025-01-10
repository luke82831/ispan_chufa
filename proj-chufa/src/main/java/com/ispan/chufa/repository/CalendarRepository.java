package com.ispan.chufa.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.CalendarBean;


public interface CalendarRepository extends JpaRepository<CalendarBean, LocalDate>{

	List<CalendarBean> findByIsHoliday(boolean b);

	
}
