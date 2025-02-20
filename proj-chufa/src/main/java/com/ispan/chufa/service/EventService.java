package com.ispan.chufa.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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
    
    
    public List<EventBean> getEventsByTripId(Long tripId) {
        return eventRepository.findBySchedule_TripId(tripId);
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
        event.setNotes(null);

        eventRepository.save(event);
        System.out.println("✅ Event created for date: " + date);
    }
    
    
 // 取得目前 Schedule 已經有的 Event 日期
    public List<LocalDate> getExistingEventDatesBySchedule(ScheduleBean schedule) {
        return eventRepository.findBySchedule(schedule)
                .stream()
                .map(EventBean::getDate)
                .collect(Collectors.toList());
    }

    // 只新增缺少的事件
    public void createMissingEvents(ScheduleBean schedule, List<LocalDate> existingEventDates) {
        LocalDate startDate = schedule.getStartDate();
        LocalDate endDate = schedule.getEndDate();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (!existingEventDates.contains(date)) {
                createEvent(schedule, date);
            }
        }
    }


    
    //新增日期
//    public EventBean updateEvent(Long eventId, EventUpdateRequest request) {
//        Optional<EventBean> existingEventOpt = eventRepository.findById(eventId);
//        if (existingEventOpt.isPresent()) {
//            EventBean existingEvent = existingEventOpt.get();
//
//            // **更新 Schedule（Trip）**
//            if (request.getTripId() != null) {
//                ScheduleBean schedule = scheduleRepository.findById(request.getTripId())
//                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + request.getTripId()));
//                existingEvent.setSchedule(schedule); // 設定新的關聯行程
//            }
//
//            // **更新其他欄位**
//            if (request.getCalendar() != null) {
//                CalendarBean calendar = calendarRepository.findById(request.getCalendar())
//                    .orElseThrow(() -> new EntityNotFoundException("Calendar not found with id: " + request.getCalendar()));
//                existingEvent.setCalendar(calendar);
//            }
//
//            if (request.getNotes() != null) {
//                existingEvent.setNotes(request.getNotes());
//            }
//
//            existingEvent.setStartTime(LocalTime.of(8, 0)); // 預設 08:00 出發
//
//            return eventRepository.save(existingEvent);
//        } else {
//            throw new EntityNotFoundException("Event not found with id: " + eventId);
//        }
//    }


    
//    public EventBean getEventById(Long eventId) {
//        return eventRepository.findById(eventId).orElse(null);  // 返回 EventBean 或 null
//    }
    
    
    
    
    
    
    
    
}
