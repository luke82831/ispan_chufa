package com.ispan.chufa.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.ScheduleBean;
import com.ispan.chufa.service.MemberService;
import com.ispan.chufa.service.ScheduleService;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

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
        System.out.println("Received coverPhoto: " + coverPhoto.getOriginalFilename());
        System.out.println("userID: " + userId);
        
        

        try {
            MemberBean user = memberService.getUserById(userId);

            if (user == null) {
                System.err.println("User with ID " + userId + " not found.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String filePath = saveFile(coverPhoto);

            ScheduleBean schedule = new ScheduleBean();
            schedule.setTripName(tripName);
            schedule.setStartDate(startDate);
            schedule.setEndDate(endDate);
            schedule.setCoverPhoto(filePath);
            schedule.setUser(user);

            ScheduleBean savedSchedule = scheduleService.saveSchedule(schedule);
            return new ResponseEntity<>(savedSchedule, HttpStatus.CREATED);
        } catch (IOException e) {
            System.err.println("File upload failed: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.err.println("Error creating schedule: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
    
    private String saveFile(MultipartFile file) throws IOException {
        Path uploadDir = Paths.get("upload-directory");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Path path = uploadDir.resolve(fileName);

        Files.write(path, file.getBytes());

        return path.toString();
    }

    // -------PostMan測試程式

//    // POST: 創建行程資料
//    @PostMapping("/schedule")
//    public ResponseEntity<ScheduleBean> createSchedule(@RequestBody ScheduleBean schedule) {
//        System.out.println("Received schedule with end date: " + schedule.getEndDate());
//        ScheduleBean savedSchedule = scheduleService.saveSchedule(schedule);
//        return new ResponseEntity<>(savedSchedule, HttpStatus.CREATED);
//    }

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
    public ResponseEntity<?> updateSchedule(@PathVariable("tripId") Long tripId,
            @RequestBody ScheduleBean updatedSchedule) {
        try {
            ScheduleBean updated = scheduleService.updateSchedule(tripId, updatedSchedule);
            return new ResponseEntity<>(updated, HttpStatus.OK); // 更新成功，返回 200 和更新後的資料
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 資料不存在，返回 404 和錯誤訊息
        }
    }

    // DELETE: 根據 tripId 刪除行程資料
    @DeleteMapping("/schedule/{tripId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable("tripId") Long tripId) {
        try {
            scheduleService.deleteSchedule(tripId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 刪除成功，返回 204
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 資料不存在，返回 404 和錯誤訊息
        }
    }
}
