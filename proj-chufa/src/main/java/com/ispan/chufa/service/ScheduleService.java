package com.ispan.chufa.service;

import java.util.List;
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
 // 取得所有行程
    public List<ScheduleBean> findAllSchedules() {
        return scheduleRepository.findAll();
    }
    
    // 查詢行程資料
    public Optional<ScheduleBean> findScheduleById(Long tripId) {
        return scheduleRepository.findById(tripId);
    }       
    
 // 更新行程資料
    public ScheduleBean updateSchedule(Long tripId, ScheduleBean updatedSchedule) {
        // 檢查資料是否存在
        Optional<ScheduleBean> existingScheduleOpt = scheduleRepository.findById(tripId);
        if (existingScheduleOpt.isEmpty()) {
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
    
    // 根據 userId 查詢行程資料
    public List<ScheduleBean> findSchedulesByUserId(Long userId) {
        return scheduleRepository.findByUser_Userid(userId);
    }
    
    /** 🔹 更新行程標題 */
    public ScheduleBean updateScheduleTitle(Long tripId, String newTitle) {
        ScheduleBean schedule = scheduleRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("找不到行程 ID: " + tripId));

        schedule.setTripName(newTitle); // 更新標題
        return scheduleRepository.save(schedule); // 儲存到資料庫
    }

    
}
