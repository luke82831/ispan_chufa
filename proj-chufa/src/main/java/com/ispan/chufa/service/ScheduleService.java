package com.ispan.chufa.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.ScheduleBean;
import com.ispan.chufa.repository.ScheduleRepository;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    
 // 新增行程資料
    public ScheduleBean saveSchedule(ScheduleBean schedule) {
    	if (schedule.getEndDate() == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        return scheduleRepository.save(schedule);
    }
    
    // 查詢行程資料
    public Optional<ScheduleBean> findScheduleById(Long tripId) {
        return scheduleRepository.findById(tripId);
    }
    
    
    
 // 更新行程資料
    public ScheduleBean updateSchedule(Long tripId, ScheduleBean updatedSchedule) {
        // 檢查資料是否存在
        Optional<ScheduleBean> existingScheduleOpt = scheduleRepository.findById(tripId);
        if (!existingScheduleOpt.isPresent()) {
            throw new IllegalArgumentException("Schedule with ID " + tripId + " does not exist.");
        }

        ScheduleBean existingSchedule = existingScheduleOpt.get();
        // 更新現有的行程資料
        existingSchedule.setCoverPhoto(updatedSchedule.getCoverPhoto());
        existingSchedule.setTripName(updatedSchedule.getTripName());
        existingSchedule.setStartDate(updatedSchedule.getStartDate());
        existingSchedule.setEndDate(updatedSchedule.getEndDate());
        existingSchedule.setUser(updatedSchedule.getUser());

        return scheduleRepository.save(existingSchedule);
    }
    
    
 // 根據 tripId 刪除行程資料
    public void deleteSchedule(Long tripId) {
        // 確保在刪除之前資料存在
        if (!scheduleRepository.existsById(tripId)) {
            throw new IllegalArgumentException("Schedule with ID " + tripId + " does not exist.");
        }
        scheduleRepository.deleteById(tripId);
    }

    
}


//package com.ispan.chufa.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.ispan.chufa.domain.ScheduleBean;
//import com.ispan.chufa.repository.ScheduleRepository;
//
//@Service
//public class ScheduleService {
//
//    @Autowired
//    private ScheduleRepository scheduleRepository;
//
//    public ScheduleBean saveSchedule(ScheduleBean schedule) {
//        // 簡單的資料驗證
//        if (schedule.getEndDate() == null) {
//            // 可以選擇拋出一個自訂的例外
//            throw new IllegalArgumentException("End date cannot be null");
//        }
//        
//        try {
//            return scheduleRepository.save(schedule);
//        } catch (Exception e) {
//            // 可以添加更具體的異常處理
//            throw new RuntimeException("Failed to save schedule", e);
//        }
//    }
//
//    // 可選：添加更多業務邏輯方法，如查詢、更新等
//}
//
