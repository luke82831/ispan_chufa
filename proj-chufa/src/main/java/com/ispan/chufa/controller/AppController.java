//package com.ispan.chufa.controller;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ispan.chufa.domain.CalendarBean;
//import com.ispan.chufa.domain.EventBean;
//import com.ispan.chufa.domain.EventXPlaceBean;
//import com.ispan.chufa.domain.ScheduleBean;
//import com.ispan.chufa.service.CalendarService;
//import com.ispan.chufa.service.EventService;
//import com.ispan.chufa.service.EventXPlaceService;
//import com.ispan.chufa.service.ScheduleService;
//
//@RestController
//@RequestMapping("/api")
//public class AppController {
//
//    @Autowired
//    private CalendarService calendarService;
//
//    @Autowired
//    private ScheduleService scheduleService;
//
//    @Autowired
//    private EventService eventService;
//    
//    @Autowired
//    private EventXPlaceService eventXPlaceService;
//
//////    輸入陣列日期(成功)
////    @PostMapping("/calendar")
////    public ResponseEntity<?> createCalendars(@RequestBody List<CalendarBean> calendars) {
////        List<CalendarBean> savedCalendars = calendarService.saveAllCalendars(calendars);
////        return new ResponseEntity<>(savedCalendars, HttpStatus.CREATED);
////    }
////    
//    
//    // POST: 創建行事曆資料 可輸入單一日期的程式碼
//    @PostMapping("/calendar")
//    public ResponseEntity<CalendarBean> createCalendar(@RequestBody CalendarBean calendar) {
//        CalendarBean savedCalendar = calendarService.saveCalendar(calendar);
//        return new ResponseEntity<>(savedCalendar, HttpStatus.CREATED);
//    }
//    
//    // GET: 前端輸入date請求資料
//    @GetMapping("/calendar/{date}")
//    public ResponseEntity<CalendarBean> getCalendarByDate(@PathVariable("date") LocalDate date) {
//        System.out.println("Received date: " + date);  // 打印日期來檢查
//        Optional<CalendarBean> calendar = calendarService.findCalendarByDate(date);
//        if (calendar.isPresent()) {
//            return new ResponseEntity<>(calendar.get(), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//    
//    
//    
//    
//
//    // POST: 創建行程資料
//    @PostMapping("/schedule")
//    public ResponseEntity<ScheduleBean> createSchedule(@RequestBody ScheduleBean schedule) {
//    	 System.out.println("Received schedule with end date: " + schedule.getEndDate());
//    	ScheduleBean savedSchedule = scheduleService.saveSchedule(schedule);
//        return new ResponseEntity<>(savedSchedule, HttpStatus.CREATED);
//    }
//    
// // GET: 前端輸入tripId查詢資料
//    @GetMapping("/schedule/{tripId}")
//    public ResponseEntity<ScheduleBean> getScheduleByTripId(@PathVariable("tripId") Long tripId) {
//        Optional<ScheduleBean> schedule = scheduleService.findScheduleById(tripId);
//        if (schedule.isPresent()) {
//            return new ResponseEntity<>(schedule.get(), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//    
//    
// // PUT: 更新行程資料
//    @PutMapping("/schedule/{tripId}")
//    public ResponseEntity<?> updateSchedule(@PathVariable("tripId") Long tripId, @RequestBody ScheduleBean updatedSchedule) {
//        try {
//            ScheduleBean updated = scheduleService.updateSchedule(tripId, updatedSchedule);
//            return new ResponseEntity<>(updated, HttpStatus.OK);  // 更新成功，返回 200 和更新後的資料
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);  // 資料不存在，返回 404 和錯誤訊息
//        }
//    }
//
//    
//    
// // DELETE: 根據 tripId 刪除行程資料
//    @DeleteMapping("/schedule/{tripId}")
//    public ResponseEntity<?> deleteSchedule(@PathVariable("tripId") Long tripId) {
//        try {
//            scheduleService.deleteSchedule(tripId);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // 刪除成功，返回 204
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);  // 資料不存在，返回 404 和錯誤訊息
//        }
//    }
//    
//    
//    
//    
//    
//    
//    
//
//    // POST: 創建行程內容資料
//    @PostMapping("/event")
//    public ResponseEntity<EventBean> createEvent(@RequestBody EventBean event) {
//        EventBean savedEvent = eventService.saveEvent(event);
//        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
//    }
//    
// // GET: 根據 event_id 查詢 Event 資料
//    @GetMapping("/event/{eventId}")
//    public ResponseEntity<EventBean> getEventById(@PathVariable("eventId") Long eventId) {
//        EventBean event = eventService.findEventById(eventId);  // 透過服務查詢 Event 資料
//        if (event != null) {
//            return new ResponseEntity<>(event, HttpStatus.OK);  // 資料存在，返回 200 和資料
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 資料不存在，返回 404
//        }
//    }
//    
//    // PUT: 更新行程內容資料
//    @PutMapping("/event/{eventId}")
//    public ResponseEntity<?> updateEvent(@PathVariable("eventId") Long eventId, @RequestBody EventBean updatedEvent) {
//        try {
//            EventBean existingEvent = eventService.findEventById(eventId);
//            if (existingEvent == null) {
//                return new ResponseEntity<>("Event with ID " + eventId + " does not exist.", HttpStatus.NOT_FOUND);
//            }
//
//            // Update fields
//            existingEvent.setStartDate(updatedEvent.getStartDate());
//            existingEvent.setEndDate(updatedEvent.getEndDate());
//            existingEvent.setNotes(updatedEvent.getNotes());
//            existingEvent.setSchedule(updatedEvent.getSchedule());
//            existingEvent.setCalendar(updatedEvent.getCalendar());
//
//            EventBean savedEvent = eventService.saveEvent(existingEvent);
//            return new ResponseEntity<>(savedEvent, HttpStatus.OK); // 更新成功，返回 200 和更新後的資料
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 資料不存在，返回 404 和錯誤訊息
//        }
//    }
//    
//    
// // DELETE: 根據 eventId 刪除 Event 資料
//    @DeleteMapping("/event/{eventId}")
//    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId) {
//        try {
//            eventService.deleteEventById(eventId);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 刪除成功，返回 204
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 資料不存在，返回 404
//        }
//    }
//    
//    
//    
//    
//    
//    
//    @PostMapping("/eventXPlace")
//    public ResponseEntity<EventXPlaceBean> createEventXPlace(@RequestBody EventXPlaceBean eventXPlaceBean) {
//        EventXPlaceBean savedEventXPlace = eventXPlaceService.saveEventXPlace(eventXPlaceBean);
//        return new ResponseEntity<>(savedEventXPlace, HttpStatus.CREATED);
//    }
//    
//    @GetMapping("/eventXPlace/{eventmappingId}")
//    public ResponseEntity<EventXPlaceBean> getEventXPlaceById(@PathVariable("eventmappingId") Long eventmappingId) {
//        EventXPlaceBean eventXPlace = eventXPlaceService.findEventXPlaceById(eventmappingId);  // 查詢 EventXPlace 資料
//        if (eventXPlace != null) {
//            return new ResponseEntity<>(eventXPlace, HttpStatus.OK);  // 資料存在，返回 200 和資料
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 資料不存在，返回 404
//        }
//    }
//    
// // PUT: 更新 EventXPlace 資料
//    @PutMapping("/eventXPlace/{eventmappingId}")
//    public ResponseEntity<?> updateEventXPlace(@PathVariable("eventmappingId") Long eventmappingId, @RequestBody EventXPlaceBean updatedEventXPlace) {
//        try {
//            // 查詢現有的 EventXPlace 資料
//            EventXPlaceBean existingEventXPlace = eventXPlaceService.findEventXPlaceById(eventmappingId);
//            if (existingEventXPlace == null) {
//                return new ResponseEntity<>("EventXPlace with ID " + eventmappingId + " does not exist.", HttpStatus.NOT_FOUND);
//            }
//
//            // 更新欄位 (不更新 FK 欄位)
//            existingEventXPlace.setPlaceOrder(updatedEventXPlace.getPlaceOrder());
//            existingEventXPlace.setArrivalTime(updatedEventXPlace.getArrivalTime());
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
//    public ResponseEntity<?> deleteEventXPlace(@PathVariable("eventmappingId") Long eventmappingId) {
//        try {
//            eventXPlaceService.deleteEventXPlaceById(eventmappingId);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 刪除成功，返回 204
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 資料不存在，返回 404
//        }
//    }
//}
