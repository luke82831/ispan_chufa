package com.ispan.chufa.repository;

import com.ispan.chufa.domain.ScheduleBean;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<ScheduleBean, Long> {
}
