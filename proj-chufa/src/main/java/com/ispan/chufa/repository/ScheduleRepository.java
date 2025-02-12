package com.ispan.chufa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.ScheduleBean;

public interface ScheduleRepository extends JpaRepository<ScheduleBean, Long> {
    List<ScheduleBean> findByUser_Userid(Long userId);
}
