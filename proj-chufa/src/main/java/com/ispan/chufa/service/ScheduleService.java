package com.ispan.chufa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.ScheduleBean;
import com.ispan.chufa.repository.ScheduleRepository;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public ScheduleBean saveSchedule(ScheduleBean schedule) {
    	if (schedule.getEndDate() == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        return scheduleRepository.save(schedule);
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
