package com.ispan.chufa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.repository.PlaceRepository;

@RestController
@RequestMapping("/api")
public class PlaceController {

    @Autowired
    private PlaceRepository placeRepository;

    @PostMapping("/savePlace")
    public ResponseEntity<?> savePlace(@RequestBody PlaceBean placeBean) {
    	PlaceBean place = new PlaceBean();
        place.setPlaceName(placeBean.getPlaceName());
        place.setPlaceAddress(placeBean.getPlaceAddress());
        place.setLongitude(placeBean.getLongitude());
        place.setLatitude(placeBean.getLatitude());
        place.setPlacePhone(placeBean.getPlacePhone());
        place.setBusinessHours(placeBean.getBusinessHours());
        place.setWebsite(placeBean.getWebsite());
        place.setRating(placeBean.getRating());
        place.setPhotos(placeBean.getPhotos());
        placeRepository.save(place);
//        System.out.println("收到的地點資料: " + placeBean);
        System.out.println("地點電話：" + placeBean.getPlacePhone());
        System.out.println("地點營業時間：" + placeBean.getBusinessHours());
        System.out.println("地點網站：" + placeBean.getWebsite());
        System.out.println("地點評分：" + placeBean.getRating());
        System.out.println("地點圖片：" + placeBean.getPhotos());

        return ResponseEntity.ok("Place saved successfully");
    }
}
