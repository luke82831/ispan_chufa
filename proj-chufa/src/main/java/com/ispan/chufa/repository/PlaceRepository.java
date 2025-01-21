package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.PlaceBean;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceBean, Long> {

	PlaceBean findByPlaceAddress(String placeAddress);

}