package com.ispan.chufa.service;

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
        // 1️⃣ 取得 `EventBean`
        EventBean event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("行程不存在: " + eventId));

        // 2️⃣ 更新 `EventBean` 的基本資訊
        event.setStartTime(LocalTime.parse(request.getStartTime()));
        event.setEndTime(LocalTime.parse(request.getEndTime()));
        event.setNotes(request.getNotes());
        eventRepository.save(event);

        // 3️⃣ 取得現有的 `EventXPlaceBean`
        List<EventXPlaceBean> existingEventPlaces = eventXPlaceRepository.findByEvent_EventId(eventId);
        Map<Long, EventXPlaceBean> existingEventPlacesMap = existingEventPlaces.stream()
                .collect(Collectors.toMap(EventXPlaceBean::getEventmappingId, Function.identity()));

        List<EventXPlaceBean> updatedEventPlaces = new ArrayList<>();

        // 4️⃣ 遍歷 `request.getPlaces()`，判斷是「更新」還是「新增」
        for (EventXPlaceRequest placeRequest : request.getPlaces()) {
            EventXPlaceBean eventXPlace;

            if (placeRequest.getEventmappingId() != null && existingEventPlacesMap.containsKey(placeRequest.getEventmappingId())) {
                // ✅ 更新現有的 `EventXPlaceBean`
                eventXPlace = existingEventPlacesMap.get(placeRequest.getEventmappingId());
            } else {
                // ✅ 新增新的 `EventXPlaceBean`
                eventXPlace = new EventXPlaceBean();
                eventXPlace.setEvent(event);
            }

            // 設定 `place`
            PlaceBean place = placeRepository.findById(placeRequest.getPlaceId())
                    .orElseThrow(() -> new RuntimeException("找不到地點: " + placeRequest.getPlaceId()));
            eventXPlace.setPlace(place);

            eventXPlace.setPlaceOrder(placeRequest.getPlaceOrder());
            eventXPlace.setTravelTime(LocalTime.parse(placeRequest.getTravelTime()));
            eventXPlace.setStayDuration(LocalTime.parse(placeRequest.getStayDuration()));
            eventXPlace.setNotes(placeRequest.getNotes() != null ? placeRequest.getNotes().toCharArray() : null);

            updatedEventPlaces.add(eventXPlace);
        }

        // 5️⃣ 只刪除前端「沒傳過來」的 `eventXPlace`
        List<Long> incomingEventmappingIds = request.getPlaces().stream()
                .map(EventXPlaceRequest::getEventmappingId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        eventXPlaceRepository.deleteByEventIdAndNotIn(eventId, incomingEventmappingIds);

        // 6️⃣ 儲存變更
        eventXPlaceRepository.saveAll(updatedEventPlaces);
    }

}


//新增地點到行程
//public EventXPlaceBean addPlaceToEvent(Long eventId, Long placeId) {
//  EventBean event = eventRepository.findById(eventId)
//          .orElseThrow(() -> new RuntimeException("找不到行程: " + eventId));
//  PlaceBean place = placeRepository.findById(placeId)
//          .orElseThrow(() -> new RuntimeException("找不到地點: " + placeId));
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
//		System.out.println("Received EventXPlaceBean: " + eventXPlaceBean);
//		if (eventXPlaceBean.getEvent() == null) {
//			System.out.println("Event is null");
//		} else {
//			System.out.println("Event ID: " + eventXPlaceBean.getEvent().getEventId());
//		}
//
//		if (eventXPlaceBean.getPlace() == null) {
//			System.out.println("Place is null");
//		} else {
//			System.out.println("Place ID: " + eventXPlaceBean.getPlace().getPlaceId());
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
//				() -> new IllegalArgumentException("EventXPlace with ID " + eventmappingId + " not found."));
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
//			throw new IllegalArgumentException("EventXPlace with ID " + eventmappingId + " not found.");
//		}
//	}
//
//}
