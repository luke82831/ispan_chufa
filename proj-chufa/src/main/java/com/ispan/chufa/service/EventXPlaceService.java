package com.ispan.chufa.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.EventXPlaceBean;
import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.repository.EventXPlaceRepository;

@Service
public class EventXPlaceService {

    @Autowired
    private EventXPlaceRepository eventXPlaceRepository;
    
//    @Autowired
//    private EventService eventService; // 注入 EventService
//
//    @Autowired
//    private PlaceService placeService; // 注入 PlaceService
    

    // 儲存 EventXPlace 資料
    public EventXPlaceBean saveEventXPlace(EventXPlaceBean eventXPlaceBean) {
    	
    	 System.out.println("Received EventXPlaceBean: " + eventXPlaceBean);
    	    if (eventXPlaceBean.getEvent() == null) {
    	        System.out.println("Event is null");
    	    } else {
    	        System.out.println("Event ID: " + eventXPlaceBean.getEvent().getEventId());
    	    }

    	    if (eventXPlaceBean.getPlace() == null) {
    	        System.out.println("Place is null");
    	    } else {
    	        System.out.println("Place ID: " + eventXPlaceBean.getPlace().getPlaceId());
    	    }
    	
    	
    	
    	
    	
//        if (eventXPlaceBean.getEvent() == null || eventXPlaceBean.getPlace() == null) {
//            throw new IllegalArgumentException("Event and Place must be provided.");
//        }
//
//        // 確保 event 和 place 不是 null
//        EventBean event = eventService.findEventById(eventXPlaceBean.getEvent().getEventId());
//        PlaceBean place = placeService.findPlaceById(eventXPlaceBean.getPlace().getPlaceId());
//        
//        if (event == null || place == null) {
//            throw new IllegalArgumentException("Event or Place not found.");
//        }
//
//        // 如果找到相應的實例，則將它們設置到 EventXPlaceBean 中
//        eventXPlaceBean.setEvent(event);
//        eventXPlaceBean.setPlace(place);

        return eventXPlaceRepository.save(eventXPlaceBean);
    }
}