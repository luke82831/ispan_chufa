package com.ispan.chufa.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.CalendarBean;
import com.ispan.chufa.service.CalendarService;

@RestController
@RequestMapping("/api")
public class CalendarController {
    
    @Autowired
    private CalendarService calendarService;

    // POST: 批次匯入行事曆資料 (預期接收 JSON 陣列)
    @PostMapping("/calendars/import")
    public ResponseEntity<List<CalendarBean>> importCalendars(@RequestBody List<CalendarBean> calendars) {
        System.out.println("📥 收到 JSON: " + calendars);
        List<CalendarBean> savedCalendars = calendarService.saveAllCalendars(calendars);
        return new ResponseEntity<>(savedCalendars, HttpStatus.CREATED);
    }

    // POST: 創建單筆行事曆資料
    @PostMapping("/calendar")
    public ResponseEntity<CalendarBean> createCalendar(@RequestBody CalendarBean calendar) {
        CalendarBean savedCalendar = calendarService.saveCalendar(calendar);
        return new ResponseEntity<>(savedCalendar, HttpStatus.CREATED);
    }

    // GET: 根據日期查詢行事曆資料
    @GetMapping("/calendar/{date}")
    public ResponseEntity<CalendarBean> getCalendarByDate(@PathVariable LocalDate date) {
        System.out.println("Received date: " + date);
        Optional<CalendarBean> calendar = calendarService.findCalendarByDate(date);
        if (calendar.isPresent()) {
            return new ResponseEntity<>(calendar.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }    
    
    // GET: 取得所有行事曆資料
    @GetMapping("/calendars")
    public ResponseEntity<List<CalendarBean>> getAllCalendars() {
        List<CalendarBean> calendars = calendarService.findAllCalendars();
        return new ResponseEntity<>(calendars, HttpStatus.OK);
    }
}
