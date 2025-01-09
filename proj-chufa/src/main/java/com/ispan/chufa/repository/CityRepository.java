package com.ispan.chufa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.CityBean;


public interface CityRepository extends JpaRepository<CityBean, Long> {
    List<CityBean> findByCityName(String cityName);

}
