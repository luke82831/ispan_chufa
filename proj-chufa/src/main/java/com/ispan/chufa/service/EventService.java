package com.ispan.chufa.service;

import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

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
    
    
    
    
//    public EventBean getEventById(Long eventId) {
//        return eventRepository.findById(eventId).orElse(null);  // 返回 EventBean 或 null
//    }
    
    
    
    
    
    
    
    
}
