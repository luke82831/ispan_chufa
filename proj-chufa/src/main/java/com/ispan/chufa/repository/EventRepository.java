package com.ispan.chufa.repository;

import com.ispan.chufa.domain.EventBean;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventBean, Long> {
}
