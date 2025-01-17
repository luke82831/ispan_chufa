package com.ispan.chufa.controller;

import java.time.LocalDate;
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

	 
//// 輸入陣列日期(成功)
// @PostMapping("/calendar")
// public ResponseEntity<?> createCalendars(@RequestBody List<CalendarBean> calendars) {
//     List<CalendarBean> savedCalendars = calendarService.saveAllCalendars(calendars);
//     return new ResponseEntity<>(savedCalendars, HttpStatus.CREATED);
// }
 
 
	 	// POST: 創建行事曆資料 可輸入單一日期的程式碼
	 	@PostMapping("/calendar")
	 		public ResponseEntity<CalendarBean> createCalendar(@RequestBody CalendarBean calendar) {
	 			CalendarBean savedCalendar = calendarService.saveCalendar(calendar);
	 			return new ResponseEntity<>(savedCalendar, HttpStatus.CREATED);
	 	}
 
	 	// GET: 前端輸入date請求資料
	 	@GetMapping("/calendar/{date}")
	 		public ResponseEntity<CalendarBean> getCalendarByDate(@PathVariable("date") LocalDate date) {
	 			System.out.println("Received date: " + date);  // 打印日期來檢查
	 			Optional<CalendarBean> calendar = calendarService.findCalendarByDate(date);
	 			if (calendar.isPresent()) {
	 				return new ResponseEntity<>(calendar.get(), HttpStatus.OK);
	 				} else {
	 					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 				}
	 			}	
}
