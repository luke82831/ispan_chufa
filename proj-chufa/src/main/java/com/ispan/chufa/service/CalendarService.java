package com.ispan.chufa.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.CalendarBean;
import com.ispan.chufa.repository.CalendarRepository;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    // 批次儲存行事曆資料
    public List<CalendarBean> saveAllCalendars(List<CalendarBean> calendars) {
        return calendarRepository.saveAll(calendars);
    }

    // 儲存單筆行事曆資料
    public CalendarBean saveCalendar(CalendarBean calendar) {
        return calendarRepository.save(calendar);
    }

    // 根據日期查詢行事曆資料
    public Optional<CalendarBean> findCalendarByDate(LocalDate date) {
        return calendarRepository.findById(date);
    }
    
    // 取得所有行事曆資料
    public List<CalendarBean> findAllCalendars() {
        return calendarRepository.findAll();
    }
}
