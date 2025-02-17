package com.ispan.chufa.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.EventXPlaceBean;
import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.dto.EventXPlaceRequest;
import com.ispan.chufa.dto.ItineraryRequest;
import com.ispan.chufa.repository.EventRepository;
import com.ispan.chufa.repository.EventXPlaceRepository;
import com.ispan.chufa.repository.PlaceRepository;

import jakarta.transaction.Transactional;

@Service
public class EventXPlaceService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private EventXPlaceRepository eventXPlaceRepository;

    //取得某個 eventId 的所有 placeId
    public List<String> getPlacesByEvent(Long eventId) {
        return eventXPlaceRepository.findPlaceIdsByEventId(eventId);
    }

    //從行程中移除地點
    public void removePlaceFromEvent(Long eventId, Long placeId) {
        EventXPlaceBean relation = eventXPlaceRepository.findByEvent_EventIdAndPlace_PlaceId(eventId, placeId)
                .orElseThrow(() -> new RuntimeException("找不到對應的 eventXPlace"));

        eventXPlaceRepository.delete(relation);
    }
    
    @Transactional
    public void updateEventXPlaces(Long eventId, ItineraryRequest request) {
        // 取得 `EventBean`，確保行程存在
        EventBean event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("行程不存在: " + eventId));

        // 透過 `event.calendar.date` 取得行程日期
        LocalDate itineraryDate = event.getCalendar().getDate();
        if (itineraryDate == null) {
            throw new RuntimeException("行程日期為空，無法更新");
        }

        // 更新 `Event` 的 `startTime` 和 `endTime`
        event.setStartTime(LocalTime.parse(request.getStartTime()));
        event.setEndTime(LocalTime.parse(request.getEndTime()));
        eventRepository.save(event);

        // 取得當天的 `EventXPlaceBean`
        List<EventXPlaceBean> existingEventPlaces = eventXPlaceRepository.findByEvent_EventIdAndEvent_Calendar_Date(eventId, itineraryDate);
        Map<Long, EventXPlaceBean> existingEventPlacesMap = existingEventPlaces.stream()
                .collect(Collectors.toMap(EventXPlaceBean::getEventmappingId, Function.identity()));

        List<Long> incomingEventmappingIds = request.getPlaces().stream()
                .map(EventXPlaceRequest::getEventmappingId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 避免 SQL 語法錯誤
        if (incomingEventmappingIds.isEmpty()) {
            eventXPlaceRepository.deleteByEventIdAndEventCalendarDate(eventId, itineraryDate);
        } else {
            eventXPlaceRepository.deleteByEventIdAndEventCalendarDateAndNotIn(eventId, itineraryDate, incomingEventmappingIds);
        }

        List<EventXPlaceBean> updatedEventPlaces = new ArrayList<>();

        for (EventXPlaceRequest placeRequest : request.getPlaces()) {
            EventXPlaceBean eventXPlace;

            if (placeRequest.getEventmappingId() != null && existingEventPlacesMap.containsKey(placeRequest.getEventmappingId())) {
                eventXPlace = existingEventPlacesMap.get(placeRequest.getEventmappingId());
            } else {
                eventXPlace = new EventXPlaceBean();
                eventXPlace.setEvent(event); // `date` 來自 `event.calendar.date`
            }

            PlaceBean place = placeRepository.findById(placeRequest.getPlaceId())
                    .orElseThrow(() -> new RuntimeException("找不到地點:" + placeRequest.getPlaceId()));
            eventXPlace.setPlace(place);

            eventXPlace.setPlaceOrder(placeRequest.getPlaceOrder());
            eventXPlace.setTravelTime(LocalTime.parse(placeRequest.getTravelTime()));
            eventXPlace.setStayDuration(LocalTime.parse(placeRequest.getStayDuration()));
            eventXPlace.setNotes(placeRequest.getNotes() != null ? placeRequest.getNotes().toCharArray() : null);

            updatedEventPlaces.add(eventXPlace);
        }

        eventXPlaceRepository.saveAll(updatedEventPlaces);
    }
    
    @Transactional
    public void updateMultipleEventXPlaces(Long eventId, List<ItineraryRequest> requests) {
        for (ItineraryRequest request : requests) {
            updateEventXPlaces(eventId, request);
        }
    }

}


//新增地點到行程
//public EventXPlaceBean addPlaceToEvent(Long eventId, Long placeId) {
//  EventBean event = eventRepository.findById(eventId)
//          .orElseThrow(() -> new RuntimeException(""找不到行程: "" + eventId));
//  PlaceBean place = placeRepository.findById(placeId)
//          .orElseThrow(() -> new RuntimeException(""找不到地點: "" + placeId));
//
//  EventXPlaceBean newRelation = new EventXPlaceBean();
//  newRelation.setEvent(event);
//  newRelation.setPlace(place);
//  newRelation.setPlaceOrder(eventXPlaceRepository.countByEvent(event) + 1); // 設定順序
//
//  return eventXPlaceRepository.save(newRelation);
//}

//	// 儲存 EventXPlace 資料
//	public EventXPlaceBean saveEventXPlace(EventXPlaceBean eventXPlaceBean) {
//
//		System.out.println(""Received EventXPlaceBean: "" + eventXPlaceBean);
//		if (eventXPlaceBean.getEvent() == null) {
//			System.out.println(""Event is null"");
//		} else {
//			System.out.println(""Event ID: "" + eventXPlaceBean.getEvent().getEventId());
//		}
//
//		if (eventXPlaceBean.getPlace() == null) {
//			System.out.println(""Place is null"");
//		} else {
//			System.out.println(""Place ID: "" + eventXPlaceBean.getPlace().getPlaceId());
//		}
//
//		return eventXPlaceRepository.save(eventXPlaceBean);
//	}
//
//	// 根據 eventmappingId 查詢 EventXPlace 資料
//	public EventXPlaceBean findEventXPlaceById(Long eventmappingId) {
//		return eventXPlaceRepository.findById(eventmappingId).orElse(null); // 如果找到資料，返回 EventXPlaceBean，否則返回 null
//	}
//
//	// 更新 EventXPlace 資料
//	public EventXPlaceBean updateEventXPlace(Long eventmappingId, EventXPlaceBean updatedEventXPlace) {
//		EventXPlaceBean existingEventXPlace = eventXPlaceRepository.findById(eventmappingId).orElseThrow(
//				() -> new IllegalArgumentException(""EventXPlace with ID "" + eventmappingId + "" not found.""));
//
//		// 更新非外鍵欄位
//		existingEventXPlace.setPlaceOrder(updatedEventXPlace.getPlaceOrder());
//		existingEventXPlace.setTravelTime(updatedEventXPlace.getTravelTime());
//		existingEventXPlace.setStayDuration(updatedEventXPlace.getStayDuration());
//		existingEventXPlace.setNotes(updatedEventXPlace.getNotes());
//
//		return eventXPlaceRepository.save(existingEventXPlace);
//	}
//
//	// 刪除 EventXPlace 資料
//	public void deleteEventXPlaceById(Long eventmappingId) {
//		if (eventXPlaceRepository.existsById(eventmappingId)) {
//			eventXPlaceRepository.deleteById(eventmappingId);
//		} else {
//			throw new IllegalArgumentException(""EventXPlace with ID "" + eventmappingId + "" not found."");
//		}
//	}
//
//}
