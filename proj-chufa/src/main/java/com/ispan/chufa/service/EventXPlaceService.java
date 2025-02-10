package com.ispan.chufa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.EventXPlaceBean;
import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.repository.EventRepository;
import com.ispan.chufa.repository.EventXPlaceRepository;
import com.ispan.chufa.repository.PlaceRepository;

@Service
public class EventXPlaceService {

	@Autowired
    private EventXPlaceRepository eventXPlaceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlaceRepository placeRepository;

    //新增地點到行程
    public EventXPlaceBean addPlaceToEvent(Long eventId, Long placeId) {
        EventBean event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("找不到行程: " + eventId));
        PlaceBean place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("找不到地點: " + placeId));

        EventXPlaceBean newRelation = new EventXPlaceBean();
        newRelation.setEvent(event);
        newRelation.setPlace(place);
        newRelation.setPlaceOrder(eventXPlaceRepository.countByEvent(event) + 1); // 設定順序

        return eventXPlaceRepository.save(newRelation);
    }

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
}


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
