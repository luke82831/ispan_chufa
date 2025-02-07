package com.ispan.chufa.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.ScheduleBean;

public interface EventRepository extends JpaRepository<EventBean, Long> {
    List<EventBean> findByScheduleAndCalendar_Date(ScheduleBean schedule, LocalDate date);
    }
