package com.ispan.chufa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.EventXPlaceBean;
import com.ispan.chufa.repository.EventXPlaceRepository;

@Service
public class EventXPlaceService {

	@Autowired
	private EventXPlaceRepository eventXPlaceRepository;

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

		return eventXPlaceRepository.save(eventXPlaceBean);
	}

	// 根據 eventmappingId 查詢 EventXPlace 資料
	public EventXPlaceBean findEventXPlaceById(Long eventmappingId) {
		return eventXPlaceRepository.findById(eventmappingId).orElse(null); // 如果找到資料，返回 EventXPlaceBean，否則返回 null
	}

	// 更新 EventXPlace 資料
	public EventXPlaceBean updateEventXPlace(Long eventmappingId, EventXPlaceBean updatedEventXPlace) {
		EventXPlaceBean existingEventXPlace = eventXPlaceRepository.findById(eventmappingId).orElseThrow(
				() -> new IllegalArgumentException("EventXPlace with ID " + eventmappingId + " not found."));

		// 更新非外鍵欄位
		existingEventXPlace.setPlaceOrder(updatedEventXPlace.getPlaceOrder());
		existingEventXPlace.setArrivalTime(updatedEventXPlace.getArrivalTime());
		existingEventXPlace.setStayDuration(updatedEventXPlace.getStayDuration());
		existingEventXPlace.setNotes(updatedEventXPlace.getNotes());

		return eventXPlaceRepository.save(existingEventXPlace);
	}

	// 刪除 EventXPlace 資料
	public void deleteEventXPlaceById(Long eventmappingId) {
		if (eventXPlaceRepository.existsById(eventmappingId)) {
			eventXPlaceRepository.deleteById(eventmappingId);
		} else {
			throw new IllegalArgumentException("EventXPlace with ID " + eventmappingId + " not found.");
		}
	}

}

// if (eventXPlaceBean.getEvent() == null || eventXPlaceBean.getPlace() == null)
// {
// throw new IllegalArgumentException("Event and Place must be provided.");
// }
//
// // 確保 event 和 place 不是 null
// EventBean event =
// eventService.findEventById(eventXPlaceBean.getEvent().getEventId());
// PlaceBean place =
// placeService.findPlaceById(eventXPlaceBean.getPlace().getPlaceId());
//
// if (event == null || place == null) {
// throw new IllegalArgumentException("Event or Place not found.");
// }
//
// // 如果找到相應的實例，則將它們設置到 EventXPlaceBean 中
// eventXPlaceBean.setEvent(event);
// eventXPlaceBean.setPlace(place);
