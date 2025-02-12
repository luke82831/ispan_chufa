package com.ispan.chufa.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.PlaceBean;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceBean, Long> {

	PlaceBean findByPlaceAddress(String placeAddress);

	PlaceBean findPlaceByGooglemapPlaceId(String googlemapPlaceId);

	Page<PlaceBean> findAll(Pageable pageable);
	
}