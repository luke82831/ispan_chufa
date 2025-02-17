package com.ispan.chufa.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.CalendarBean;


public interface CalendarRepository extends JpaRepository<CalendarBean, LocalDate>{

	List<CalendarBean> findByHoliday(boolean holiday);

	Optional<CalendarBean> findByDate(LocalDate date);
	
}
