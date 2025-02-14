package com.ispan.chufa.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.ScheduleBean;

public interface EventRepository extends JpaRepository<EventBean, Long> {
	
	@EntityGraph(attributePaths = {"eventXPlaceBeans.place"}) // ✅ 預載入 `place`
	@Query("SELECT e FROM EventBean e WHERE e.schedule = :schedule AND e.calendar.date = :date")
	List<EventBean> findByScheduleAndCalendar_Date(@Param("schedule") ScheduleBean schedule, @Param("date") LocalDate date);

    List<EventBean> findBySchedule(ScheduleBean schedule);
}
