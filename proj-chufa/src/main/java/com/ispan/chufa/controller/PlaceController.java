package com.ispan.chufa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.domain.PlaceDTO;
import com.ispan.chufa.repository.PlaceRepository;

@RestController
@RequestMapping("/api")
public class PlaceController {

    @Autowired
    private PlaceRepository placeRepository;

    @PostMapping("/savePlace")
    public ResponseEntity<?> savePlace(@RequestBody PlaceDTO placeDTO) {
    	PlaceBean place = new PlaceBean();
        place.setPlaceName(placeDTO.getPlaceName());
        place.setPlaceAddress(placeDTO.getPlaceAddress());
        place.setLongitude(placeDTO.getLongitude());
        place.setLatitude(placeDTO.getLatitude());
        placeRepository.save(place);

        return ResponseEntity.ok("Place saved successfully");
    }
}
