package com.ispan.chufa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.service.EventService;



@RestController
@RequestMapping("/api")
public class EventController {

	@Autowired
    private EventService eventService;
	
	
	// POST: 創建行程內容資料
    @PostMapping("/event")
    public ResponseEntity<EventBean> createEvent(@RequestBody EventBean event) {
        EventBean savedEvent = eventService.saveEvent(event);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }
    
    // GET: 根據 event_id 查詢 Event 資料
    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventBean> getEventById(@PathVariable("eventId") Long eventId) {
        EventBean event = eventService.findEventById(eventId);  // 透過服務查詢 Event 資料
        if (event != null) {
            return new ResponseEntity<>(event, HttpStatus.OK);  // 資料存在，返回 200 和資料
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 資料不存在，返回 404
        }
    }
    
    // PUT: 更新行程內容資料
    @PutMapping("/event/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") Long eventId, @RequestBody EventBean updatedEvent) {
        try {
            EventBean existingEvent = eventService.findEventById(eventId);
            if (existingEvent == null) {
                return new ResponseEntity<>("Event with ID " + eventId + " does not exist.", HttpStatus.NOT_FOUND);
            }

            // Update fields
            existingEvent.setStartTime(updatedEvent.getStartTime());
            existingEvent.setEndTime(updatedEvent.getEndTime());
            existingEvent.setNotes(updatedEvent.getNotes());
            existingEvent.setSchedule(updatedEvent.getSchedule());
            existingEvent.setCalendar(updatedEvent.getCalendar());

            EventBean savedEvent = eventService.saveEvent(existingEvent);
            return new ResponseEntity<>(savedEvent, HttpStatus.OK); // 更新成功，返回 200 和更新後的資料
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 資料不存在，返回 404 和錯誤訊息
        }
    }
    
    
    // DELETE: 根據 eventId 刪除 Event 資料
    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId) {
        try {
            eventService.deleteEventById(eventId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 刪除成功，返回 204
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 資料不存在，返回 404
        }
    }
}
