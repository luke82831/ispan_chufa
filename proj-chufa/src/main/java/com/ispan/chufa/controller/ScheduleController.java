package com.ispan.chufa.controller;

import java.util.Optional;

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

import com.ispan.chufa.domain.ScheduleBean;
import com.ispan.chufa.service.ScheduleService;


@RestController
@RequestMapping("/api")
public class ScheduleController {
	
	@Autowired
    private ScheduleService scheduleService;
	
	
	// POST: 創建行程資料
    @PostMapping("/schedule")
    public ResponseEntity<ScheduleBean> createSchedule(@RequestBody ScheduleBean schedule) {
    	 System.out.println("Received schedule with end date: " + schedule.getEndDate());
    	ScheduleBean savedSchedule = scheduleService.saveSchedule(schedule);
        return new ResponseEntity<>(savedSchedule, HttpStatus.CREATED);
    }
    
    // GET: 前端輸入tripId查詢資料
    @GetMapping("/schedule/{tripId}")
    public ResponseEntity<ScheduleBean> getScheduleByTripId(@PathVariable("tripId") Long tripId) {
        Optional<ScheduleBean> schedule = scheduleService.findScheduleById(tripId);
        if (schedule.isPresent()) {
            return new ResponseEntity<>(schedule.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    
    // PUT: 更新行程資料
    @PutMapping("/schedule/{tripId}")
    public ResponseEntity<?> updateSchedule(@PathVariable("tripId") Long tripId, @RequestBody ScheduleBean updatedSchedule) {
        try {
            ScheduleBean updated = scheduleService.updateSchedule(tripId, updatedSchedule);
            return new ResponseEntity<>(updated, HttpStatus.OK);  // 更新成功，返回 200 和更新後的資料
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);  // 資料不存在，返回 404 和錯誤訊息
        }
    }

    
    
    // DELETE: 根據 tripId 刪除行程資料
    @DeleteMapping("/schedule/{tripId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable("tripId") Long tripId) {
        try {
            scheduleService.deleteSchedule(tripId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // 刪除成功，返回 204
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);  // 資料不存在，返回 404 和錯誤訊息
        }
    }
}
