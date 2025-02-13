package com.ispan.chufa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.repository.PlaceRepository;
import com.ispan.chufa.service.PlaceService;

@RestController
@RequestMapping("/api")
public class PlaceController {

    @Autowired
    private PlaceRepository placeRepository;
	  
    @Autowired
    private PlaceService placeService;

    // 創建 Place 並關聯多個 Post
    @PostMapping("/place/create")
    public ResponseEntity<PlaceBean> createPlaceWithPosts(@RequestBody PlaceBean place) {
        // 假設前端會提供一個 place 和一個包含 Post ID 的 list
        Set<Long> postIds = place.getPosts().stream()
                .map(PostBean::getPostid)
                .collect(Collectors.toSet());
        PlaceBean createdPlace = placeService.createPlaceWithPosts(place, postIds);
        return ResponseEntity.ok(createdPlace);
    }

    // 查詢指定 ID 的 Place 和其關聯的 Posts
    @GetMapping("/place/{id}")
    public ResponseEntity<PlaceBean> getPlaceById(@PathVariable Long id) {
        PlaceBean place = placeService.getPlaceById(id);
        if (place == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(place);
    }

    
    // 修改指定 ID 的 Place
    @PutMapping("/places/{id}")
    public ResponseEntity<PlaceBean> updatePlace(@PathVariable Long id, @RequestBody PlaceBean placeDetails) {
        PlaceBean updatedPlace = placeService.updatePlace(id, placeDetails);
        if (updatedPlace == null) {
            return ResponseEntity.notFound().build();  // 如果找不到對應的 Place
        }
        return ResponseEntity.ok(updatedPlace);  // 返回更新後的 Place
    }

    // 刪除指定 ID 的 Place
    @DeleteMapping("/places/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        boolean isDeleted = placeService.deletePlace(id);
        if (isDeleted) {
            // 注意：避免重複呼叫 deletePlace(id)
            return ResponseEntity.noContent().build();  // 刪除成功，回傳 204 No Content
        } else {
            return ResponseEntity.notFound().build();  // 找不到對應的資料
        }
    }

    
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
    
    @PostMapping("/place/batch")
    public List<PlaceBean> getMultiplePlaces(@RequestBody Map<String, List<Long>> request) {
        List<Long> placeIds = request.get("placeIds");

        if (placeIds == null || placeIds.isEmpty()) {
            throw new IllegalArgumentException("placeIds 不能為空");
        }

        return placeService.getPlacesByIds(placeIds);
    }
    
 // 取得 Place 分頁數據
    @GetMapping("/places/paged") 
    public ResponseEntity<Page<PlaceBean>> getPlacesPaged(@RequestParam int page, @RequestParam int size) {
    	Page<PlaceBean> places = placeService.getPlacesWithPagination(page, size); 
    	return ResponseEntity.ok(places); }
    
    
}
