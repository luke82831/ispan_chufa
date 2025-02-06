package com.ispan.chufa.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.CalendarBean;
import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.ScheduleBean;
import com.ispan.chufa.repository.CalendarRepository;
import com.ispan.chufa.repository.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private CalendarRepository calendarRepository;

    public EventBean saveEvent(EventBean event) {
        return eventRepository.save(event);
    }
    
    
 // 根據 eventId 查詢 EventBean
    public EventBean findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElse(null); // 返回對應的 EventBean，若找不到返回 null
    }
    
 // 根據 tripId 刪除行程資料
    public void deleteEventById(Long eventId) {
    	if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event with ID " + eventId + " does not exist.");
        }
    	eventRepository.deleteById(eventId);
    }
    
    public List<EventBean> findEventsByTripAndDate(ScheduleBean schedule, LocalDate date) {
        System.out.println("✅ 查詢行程：" + schedule.getTripId() + "，日期：" + date);
        return eventRepository.findByScheduleAndCalendar_Date(schedule, date);
    }

    public void createEventFromSchedule(ScheduleBean schedule) {
        createEvent(schedule, schedule.getStartDate());
        createEvent(schedule, schedule.getEndDate());
    }

    private void createEvent(ScheduleBean schedule, LocalDate date) {
        // **確保該日期的 `CalendarBean` 存在**
        CalendarBean calendar = calendarRepository.findByDate(date)
                .orElseGet(() -> {
                    CalendarBean newCalendar = new CalendarBean();
                    newCalendar.setDate(date);
                    return calendarRepository.save(newCalendar);
                });

        // **建立 `event`**
        EventBean event = new EventBean();
        event.setSchedule(schedule);
        event.setCalendar(calendar);
        event.setStartTime(LocalTime.of(8, 0)); // **預設 08:00 出發**
        event.setNotes("行程開始");

        eventRepository.save(event);
        System.out.println("✅ Event created for date: " + date);
    }
    
    
//    public EventBean getEventById(Long eventId) {
//        return eventRepository.findById(eventId).orElse(null);  // 返回 EventBean 或 null
//    }
    
    
    
    
    
    
    
    
}
