package com.ispan.chufa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.EventXPlaceBean;
import com.ispan.chufa.service.EventXPlaceService;



@RestController
@RequestMapping("/api")
public class EventXPlaceController {

	@Autowired
    private EventXPlaceService eventXPlaceService;
	
	@PostMapping("/eventXPlace")
	public ResponseEntity<EventXPlaceBean> addPlaceToEvent(
            @RequestParam Long eventId,
            @RequestParam Long placeId) {

	    EventXPlaceBean savedRelation = eventXPlaceService.addPlaceToEvent(eventId, placeId);

	    if (savedRelation == null || savedRelation.getEventmappingId() == null) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(null); 
	    }
        return ResponseEntity.ok(savedRelation);
    }
    
	//取得某個 eventId 的所有 placeId
    @GetMapping("/eventXPlace/{eventId}")
    public ResponseEntity<List<String>> getPlacesByEvent(@PathVariable Long eventId) {
        List<String> placeIds = eventXPlaceService.getPlacesByEvent(eventId);
        return ResponseEntity.ok(placeIds);
    }

    //移除 eventId 內的某個 placeId
    @DeleteMapping("/eventXPlace/{eventId}/{placeId}")
    public ResponseEntity<String> removePlaceFromEvent(@PathVariable Long eventId, @PathVariable Long placeId) {
        eventXPlaceService.removePlaceFromEvent(eventId, placeId);
        return ResponseEntity.ok("地點 " + placeId + " 已從行程 " + eventId + " 移除");
    }
}
	
//	
//	
//	
//    @GetMapping("/eventXPlace/{eventmappingId}")
//    public ResponseEntity<EventXPlaceBean> getEventXPlaceById(@PathVariable Long eventmappingId) {
//        EventXPlaceBean eventXPlace = eventXPlaceService.findEventXPlaceById(eventmappingId);  // 查詢 EventXPlace 資料
//        if (eventXPlace != null) {
//            return new ResponseEntity<>(eventXPlace, HttpStatus.OK);  // 資料存在，返回 200 和資料
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 資料不存在，返回 404
//        }
//    }
//    
//    // PUT: 更新 EventXPlace 資料
//    @PutMapping("/eventXPlace/{eventmappingId}")
//    public ResponseEntity<?> updateEventXPlace(@PathVariable Long eventmappingId, @RequestBody EventXPlaceBean updatedEventXPlace) {
//        try {
//            // 查詢現有的 EventXPlace 資料
//            EventXPlaceBean existingEventXPlace = eventXPlaceService.findEventXPlaceById(eventmappingId);
//            if (existingEventXPlace == null) {
//                return new ResponseEntity<>("EventXPlace with ID " + eventmappingId + " does not exist.", HttpStatus.NOT_FOUND);
//            }
//
//            // 更新欄位 (不更新 FK 欄位)
//            existingEventXPlace.setPlaceOrder(updatedEventXPlace.getPlaceOrder());
//            existingEventXPlace.setTravelTime(updatedEventXPlace.getTravelTime());
//            existingEventXPlace.setStayDuration(updatedEventXPlace.getStayDuration());
//            existingEventXPlace.setNotes(updatedEventXPlace.getNotes());
//
//            // 儲存更新後的資料
//            EventXPlaceBean savedEventXPlace = eventXPlaceService.saveEventXPlace(existingEventXPlace);
//            return new ResponseEntity<>(savedEventXPlace, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    
//    
//    
//    // DELETE: 根據 eventmappingId 刪除 EventXPlace 資料
//    @DeleteMapping("/eventXPlace/{eventmappingId}")
//    public ResponseEntity<?> deleteEventXPlace(@PathVariable Long eventmappingId) {
//        try {
//            eventXPlaceService.deleteEventXPlaceById(eventmappingId);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 刪除成功，返回 204
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 資料不存在，返回 404
//        }
//    }
//	
//}
