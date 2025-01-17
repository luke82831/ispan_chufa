package com.ispan.chufa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.repository.PlaceRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MapController {

    @Autowired
    private PlaceRepository placeRepository;

    @PostMapping("/savePlace")
    public ResponseEntity<?> savePlace(@RequestBody PlaceBean placeBean) {
        PlaceBean place = new PlaceBean();
        place.setPlaceType(placeBean.getPlaceType());
        place.setPlaceName(placeBean.getPlaceName());
        place.setCity(placeBean.getCity());
        place.setRegion(placeBean.getRegion());
        place.setPlaceAddress(placeBean.getPlaceAddress());
        place.setLongitude(placeBean.getLongitude());
        place.setLatitude(placeBean.getLatitude());
        place.setPhotos(placeBean.getPhotos());
        place.setPlacePhone(placeBean.getPlacePhone());
        place.setBusinessHours(placeBean.getBusinessHours());
        place.setPlaceInfo(placeBean.getPlaceInfo());
        place.setRating(placeBean.getRating());
        place.setWebsite(placeBean.getWebsite());
        place.setBookingUrl(placeBean.getBookingUrl());
        place.setPrice(placeBean.getPrice());
        place.setPlaceName(placeBean.getPlaceName());
        place.setAccommodationType(placeBean.getAccommodationType());
        place.setReservation(placeBean.isReservation());
        placeRepository.save(place);

        // 準備回應的 JSON 物件
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Place saved successfully");

        return ResponseEntity.ok(response); // 回傳 JSON 格式
    }
}
