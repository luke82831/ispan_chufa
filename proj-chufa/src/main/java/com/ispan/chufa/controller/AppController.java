package com.ispan.chufa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.CalendarBean;
import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.EventXPlaceBean;
import com.ispan.chufa.domain.ScheduleBean;
import com.ispan.chufa.service.CalendarService;
import com.ispan.chufa.service.EventService;
import com.ispan.chufa.service.EventXPlaceService;
import com.ispan.chufa.service.ScheduleService;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private EventService eventService;
    
    @Autowired
    private EventXPlaceService eventXPlaceService;

////    輸入陣列日期(成功)
//    @PostMapping("/calendar")
//    public ResponseEntity<?> createCalendars(@RequestBody List<CalendarBean> calendars) {
//        List<CalendarBean> savedCalendars = calendarService.saveAllCalendars(calendars);
//        return new ResponseEntity<>(savedCalendars, HttpStatus.CREATED);
//    }
//    
    
    // POST: 創建行事曆資料 可輸入單一日期的程式碼
    @PostMapping("/calendar")
    public ResponseEntity<CalendarBean> createCalendar(@RequestBody CalendarBean calendar) {
        CalendarBean savedCalendar = calendarService.saveCalendar(calendar);
        return new ResponseEntity<>(savedCalendar, HttpStatus.CREATED);
    }

    // POST: 創建行程資料
    @PostMapping("/schedule")
    public ResponseEntity<ScheduleBean> createSchedule(@RequestBody ScheduleBean schedule) {
    	 System.out.println("Received schedule with end date: " + schedule.getEndDate());
    	ScheduleBean savedSchedule = scheduleService.saveSchedule(schedule);
        return new ResponseEntity<>(savedSchedule, HttpStatus.CREATED);
    }

    // POST: 創建行程內容資料
    @PostMapping("/event")
    public ResponseEntity<EventBean> createEvent(@RequestBody EventBean event) {
        EventBean savedEvent = eventService.saveEvent(event);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }
    
    @PostMapping("/eventXPlace")
    public ResponseEntity<EventXPlaceBean> createEventXPlace(@RequestBody EventXPlaceBean eventXPlaceBean) {
        EventXPlaceBean savedEventXPlace = eventXPlaceService.saveEventXPlace(eventXPlaceBean);
        return new ResponseEntity<>(savedEventXPlace, HttpStatus.CREATED);
    }
    
    
    
    
}
