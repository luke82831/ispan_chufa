package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.EventXPlaceBean;

@Repository
public interface EventXPlaceRepository extends JpaRepository<EventXPlaceBean, Long> {
    
}