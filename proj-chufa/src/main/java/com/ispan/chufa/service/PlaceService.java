package com.ispan.chufa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.repository.PlaceRepository;



@Service
public class PlaceService {
	@Autowired
    private PlaceRepository placeRepository;

	public PlaceBean findPlaceById(int placeId) {
        return placeRepository.findById(placeId).orElse(null); // 返回對應的 PlaceBean，若找不到返回 null
    }
	
    public PlaceBean saveCalendar(PlaceBean place) {
        return placeRepository.save(place);
    }
}
