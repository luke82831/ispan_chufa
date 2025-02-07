package com.ispan.chufa.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.ScheduleBean;
import com.ispan.chufa.repository.EventRepository;
import com.ispan.chufa.repository.ScheduleRepository;
import com.ispan.chufa.service.EventService;
import com.ispan.chufa.service.MemberService;
import com.ispan.chufa.service.ScheduleService;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api")
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MemberService memberService; // 注入 MemberService
    // POST: 創建行程資料

    @PostMapping("/schedule")
    public ResponseEntity<ScheduleBean> createSchedule(
            @RequestParam("tripName") String tripName,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("coverPhoto") MultipartFile coverPhoto,
            @RequestParam("userId") Long userId) {

        System.out.println("Received tripName: " + tripName);
        System.out.println("Received startDate: " + startDate);
        System.out.println("Received endDate: " + endDate);
        System.out.println("Received coverPhoto name: " + coverPhoto.getOriginalFilename());
        System.out.println("userID: " + userId);

        try {
            MemberBean user = memberService.getUserById(userId);
            if (user == null) {
                System.err.println("User with ID " + userId + " not found.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // 轉換 MultipartFile 為 byte[]
            byte[] coverPhotoBytes = coverPhoto.getBytes();

            // **建立行程 (Schedule)**
            ScheduleBean schedule = new ScheduleBean();
            schedule.setTripName(tripName);
            schedule.setStartDate(startDate);
            schedule.setEndDate(endDate);
            schedule.setCoverPhoto(coverPhotoBytes);
            schedule.setUser(user);

            // **儲存行程**
            ScheduleBean savedSchedule = scheduleService.saveSchedule(schedule);

            // **自動建立 event**
            eventService.createEventFromSchedule(savedSchedule);

            return new ResponseEntity<>(savedSchedule, HttpStatus.CREATED);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.err.println("Error creating schedule: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 取得所有行程
    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleBean>> getAllSchedules() {
        List<ScheduleBean> schedules = scheduleService.findAllSchedules();
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    // GET: 前端輸入tripId查詢資料
    @GetMapping("/schedule/{tripId}")
    public ResponseEntity<ScheduleBean> getScheduleByTripId(@PathVariable Long tripId) {
        Optional<ScheduleBean> schedule = scheduleService.findScheduleById(tripId);
        if (schedule.isPresent()) {
            return new ResponseEntity<>(schedule.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT: 更新行程資料
    @PutMapping("/schedule/{tripId}")
    public ResponseEntity<?> updateScheduleEndDate(@PathVariable Long tripId,
            @RequestBody Map<String, String> request) {
        Optional<ScheduleBean> scheduleOptional = scheduleRepository.findById(tripId);

        if (scheduleOptional.isPresent()) {
            ScheduleBean schedule = scheduleOptional.get();
            LocalDate newEndDate = LocalDate.parse(request.get("endDate"));

            // 更新行程結束日期
            schedule.setEndDate(newEndDate);
            scheduleRepository.save(schedule);

            // ✅ 自動新增對應的 Event
            eventService.createEventFromSchedule(schedule);

            return ResponseEntity.ok().body("行程結束日期已更新，並自動新增對應的事件");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("行程不存在");
        }
    }

    // DELETE: 根據 tripId 刪除行程資料
    @DeleteMapping("/schedule/{tripId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long tripId) {
        try {
            scheduleService.deleteSchedule(tripId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 刪除成功，返回 204
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 資料不存在，返回 404 和錯誤訊息
        }
    }
}