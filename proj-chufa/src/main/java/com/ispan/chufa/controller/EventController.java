package com.ispan.chufa.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.ispan.chufa.domain.CalendarBean;
import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.ScheduleBean;
import com.ispan.chufa.repository.CalendarRepository;
import com.ispan.chufa.repository.ScheduleRepository;
import com.ispan.chufa.service.EventService;


@RestController
@RequestMapping("/api")
public class EventController {
    
	@Autowired
    private EventService eventService;
	
	@Autowired
    private ScheduleRepository scheduleRepository;
	
	@Autowired
    private CalendarRepository calendarRepository;

	
	// POST: 創建行程內容資料
    @PostMapping("/event")
    public ResponseEntity<EventBean> createEvent(@RequestBody EventBean event) {
        EventBean savedEvent = eventService.saveEvent(event);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }
    
    // GET: 根據 event_id 查詢 Event 資料
//    @GetMapping("/event/{eventId}")
//    public ResponseEntity<EventBean> getEventById(@PathVariable Long eventId) {
//        EventBean event = eventService.findEventById(eventId);  // 透過服務查詢 Event 資料
//        if (event != null) {
//            return new ResponseEntity<>(event, HttpStatus.OK);  // 資料存在，返回 200 和資料
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 資料不存在，返回 404
//        }
//    }
    
    
    // GET: 根據 event_id 查詢 Event 資料
    @GetMapping("/event/{tripId}/date/{date}")
    public ResponseEntity<List<EventBean>> getEventsByTripAndDate(
            @PathVariable Long tripId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        System.out.println("🛠 後端收到的 date：" + date);

        // 1️⃣ 查詢 Schedule (行程)
        ScheduleBean schedule = scheduleRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("找不到行程 (Trip ID: " + tripId + ")"));

        // 2️⃣ 查詢 Calendar (行事曆) 是否存在
        CalendarBean calendar = calendarRepository.findByDate(date)
                .orElseGet(() -> { 
                    System.out.println("⚠️ 無對應 Calendar，建立新 Calendar...");
                    CalendarBean newCalendar = new CalendarBean();
                    newCalendar.setDate(date);
                    return calendarRepository.save(newCalendar); // ⚠️ 必須回傳 CalendarBean
                });

        // 3️⃣ 查詢該日期的 events
        List<EventBean> events = eventService.findEventsByTripAndDate(schedule, date);

        if (events.isEmpty()) {
            System.out.println("⚠️ 無行程，建立新 Event...");

            // 4️⃣ 自動建立一個 Event
            EventBean newEvent = new EventBean();
            newEvent.setCalendar(calendar); // ✅ 設定 CalendarBean
            newEvent.setSchedule(schedule); // ✅ 設定 ScheduleBean
            newEvent.setStartTime(LocalTime.of(8, 0)); // 預設開始時間
            newEvent.setEndTime(null); // 預設結束時間
            newEvent = eventService.saveEvent(newEvent); // 儲存事件

            events.add(newEvent);
        }

        System.out.println("📡 回傳 JSON: " + events);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
     
    // PUT: 更新行程內容資料
    @PutMapping("/event/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId, @RequestBody EventBean updatedEvent) {
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
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        try {
            eventService.deleteEventById(eventId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 刪除成功，返回 204
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 資料不存在，返回 404
        }
    }
}
