package com.ispan.chufa.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.repository.PlaceRepository;
import com.ispan.chufa.service.PlaceService;

@RestController
@RequestMapping("/api")
public class MapController {

    @Autowired
    private PlaceRepository placeRepository;
    
    @Autowired
    private PlaceService placeService; 

    // 查詢地點是否存在
    @PostMapping("/checkPlace")
    public ResponseEntity<?> checkPlaceByAddress(@RequestBody Map<String, String> request) {
        String googlemapPlaceId = request.get("placeId");
        System.out.println("Received googlemap placeid: " + googlemapPlaceId);  // 輸出收到的地址

        PlaceBean existingPlace = placeService.findPlaceByGooglemapPlaceId(googlemapPlaceId);
//        System.out.println(existingPlace.getGooglemapPlaceId());

        if (existingPlace != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "地點已存在資料庫");
            response.put("placeInfo", existingPlace);  
            return ResponseEntity.ok(response); // 返回地點資訊
        } else {
            // 直接返回 Map，Spring 會將它轉換為 JSON 格式
            Map<String, String> response = new HashMap<>();
            response.put("message", "地點不存在");
            return ResponseEntity.ok(response);        
            }
    }
    
    @PostMapping("/savePlace")
    public ResponseEntity<?> savePlace(@RequestBody PlaceBean placeBean) {
        PlaceBean place = new PlaceBean();
        place.setGooglemapPlaceId(placeBean.getGooglemapPlaceId());
        place.setPlaceType(placeBean.getPlaceType());
        place.setPlaceName(placeBean.getPlaceName());
        place.setCity(placeBean.getCity());
        place.setRegion(placeBean.getRegion());
        place.setPlaceAddress(placeBean.getPlaceAddress());
        place.setLongitude(placeBean.getLongitude());
        place.setLatitude(placeBean.getLatitude());
        place.setPhotos(placeBean.getPhotos());  // 這裡會自動轉換成 JSON 字串並儲存        
        place.setPlacePhone(placeBean.getPlacePhone());
        place.setBusinessHours(placeBean.getBusinessHours());
        place.setPlaceInfo(placeBean.getPlaceInfo());
        place.setRating(placeBean.getRating());
        place.setWebsite(placeBean.getWebsite());
        place.setBookingUrl(placeBean.getBookingUrl());
        place.setPriceLevel(placeBean.getPriceLevel());
        place.setPlaceName(placeBean.getPlaceName());
        place.setAccommodationType(placeBean.getAccommodationType());
        place.setReservation(placeBean.isReservation());
        place.setClosed(placeBean.getClosed());
        placeRepository.save(place);

        // 準備回應的 JSON 物件
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Place saved successfully");

        return ResponseEntity.ok(response); // 回傳 JSON 格式
    }
}
