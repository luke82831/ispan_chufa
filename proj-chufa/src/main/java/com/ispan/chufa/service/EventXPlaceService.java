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
import com.ispan.chufa.domain.ScheduleBean;
import com.ispan.chufa.dto.EventXPlaceRequest;
import com.ispan.chufa.dto.ItineraryRequest;
import com.ispan.chufa.repository.CalendarRepository;
import com.ispan.chufa.repository.EventRepository;
import com.ispan.chufa.repository.EventXPlaceRepository;
import com.ispan.chufa.repository.PlaceRepository;
import com.ispan.chufa.repository.ScheduleRepository;

import jakarta.transaction.Transactional;

@Service
public class EventXPlaceService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CalendarRepository calendarRepository;
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
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
        // 取得 `EventBean`
        EventBean event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("行程不存在: " + eventId));
        
        // 更新 `Event` 的 `startTime` 和 `endTime`
        event.setStartTime(LocalTime.parse(request.getStartTime()));
        event.setEndTime(LocalTime.parse(request.getEndTime()));
        eventRepository.save(event);  // **儲存更新後的 Event**

        // 取得現有的 `EventXPlaceBean`
        List<EventXPlaceBean> existingEventPlaces = eventXPlaceRepository.findByEvent_EventId(eventId);
        Map<Long, EventXPlaceBean> existingEventPlacesMap = existingEventPlaces.stream()
                .collect(Collectors.toMap(EventXPlaceBean::getEventmappingId, Function.identity()));

        List<EventXPlaceBean> updatedEventPlaces = new ArrayList<>();

        // 檢查哪些 `eventmappingId` 仍然存在
        List<Long> incomingEventmappingIds = request.getPlaces().stream()
                .map(EventXPlaceRequest::getEventmappingId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // **先刪除**（不在 `request` 內的 `eventXPlace`）
        eventXPlaceRepository.deleteByEventIdAndNotIn(eventId, incomingEventmappingIds);

        // **再更新** 或 **新增**
        for (EventXPlaceRequest placeRequest : request.getPlaces()) {
            EventXPlaceBean eventXPlace;

            if (placeRequest.getEventmappingId() != null && existingEventPlacesMap.containsKey(placeRequest.getEventmappingId())) {
                // ✅ 更新 `EventXPlaceBean`
                eventXPlace = existingEventPlacesMap.get(placeRequest.getEventmappingId());
            } else {
                // ✅ 新增 `EventXPlaceBean`
                eventXPlace = new EventXPlaceBean();
                eventXPlace.setEvent(event);
            }

            // 設定 `place`
            PlaceBean place = placeRepository.findById(placeRequest.getPlaceId())
                    .orElseThrow(() -> new RuntimeException("找不到地點:" + placeRequest.getPlaceId()));
            eventXPlace.setPlace(place);

            eventXPlace.setPlaceOrder(placeRequest.getPlaceOrder());
            eventXPlace.setTravelTime(LocalTime.parse(placeRequest.getTravelTime()));
            eventXPlace.setStayDuration(LocalTime.parse(placeRequest.getStayDuration()));
            eventXPlace.setNotes(placeRequest.getNotes() != null ? placeRequest.getNotes().toCharArray() : null);

            updatedEventPlaces.add(eventXPlace);
        }

        // **最後才執行批量更新**
        eventXPlaceRepository.saveAll(updatedEventPlaces);
    }
    
    @Transactional
    public void updateOrCreateMultipleEventXPlaces(Long tripId, List<ItineraryRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new RuntimeException("⚠️ 請求的行程資料為空");
        }

        for (ItineraryRequest request : requests) {
            Long eventId = request.getEventId();

            // 如果 eventId 是 null，則根據 tripId + date 查找對應的 eventId
            if (eventId == null) {
                System.out.println("🔍 eventId 為 null，嘗試查找 tripId=" + tripId + " 的日期 " + request.getDate());

                ScheduleBean schedule = scheduleRepository.findById(tripId)
                        .orElseThrow(() -> new RuntimeException("❌ 找不到 tripId=" + tripId));

                EventBean existingEvent = eventRepository.findByScheduleAndCalendar_Date(schedule, LocalDate.parse(request.getDate()))
                        .stream().findFirst().orElse(null);

                if (existingEvent != null) {
                    eventId = existingEvent.getEventId();
                    System.out.println("✅ 找到對應 eventId=" + eventId + "（tripId=" + tripId + "，date=" + request.getDate() + "）");
                } else {
                    // 🔹 如果 event 不存在，則新增一筆 event
                    System.out.println("⚠️ event 不存在，建立新的 event (tripId=" + tripId + "，date=" + request.getDate() + ")");
                    EventBean newEvent = new EventBean();
                    newEvent.setSchedule(schedule);
                    newEvent.setCalendar(calendarRepository.findByDate(LocalDate.parse(request.getDate()))
                            .orElseThrow(() -> new RuntimeException("❌ 找不到對應的 Calendar 日期")));

                    newEvent.setStartTime(LocalTime.parse(request.getStartTime()));
                    newEvent.setEndTime(LocalTime.parse(request.getEndTime()));
                    newEvent.setNotes(request.getNotes());

                    eventRepository.save(newEvent);
                    eventId = newEvent.getEventId();
                    System.out.println("✅ 新建 eventId=" + eventId);
                }
            }

            // 🔹 確保 eventId 存在後，繼續更新行程地點
            System.out.println("📝 更新 eventId=" + eventId + " 的行程地點...");
            updateEventXPlaces(eventId, request);
        }
    }

    
    
//    @Transactional
//    public void updateOrCreateEventXPlaces(Long tripId, ItineraryRequest request) {
//        LocalDate requestDate = LocalDate.parse(request.getDate());
//
//        // 🔹 先查找 `ScheduleBean`
//        ScheduleBean schedule = scheduleRepository.findById(tripId)
//                .orElseThrow(() -> new RuntimeException("❌ 找不到行程 (tripId=" + tripId + ")"));
//
//        // 🔹 查找當天的 `EventBean`，如果 `eventId` 為 null 則新增
//        EventBean event;
//        if (request.getEventId() != null) {
//            event = eventRepository.findById(request.getEventId())
//                    .orElseThrow(() -> new RuntimeException("❌ 找不到對應的 eventId: " + request.getEventId()));
//        } else {
//            event = new EventBean();
//            event.setSchedule(schedule);
//            event.setCalendar(calendarRepository.findByDate(requestDate)
//                    .orElseThrow(() -> new RuntimeException("❌ 找不到行事曆日期: " + request.getDate())));
//        }
//
//        // 🔹 更新行程基本資訊
//        event.setStartTime(LocalTime.parse(request.getStartTime()));
//        event.setEndTime(LocalTime.parse(request.getEndTime()));
//        event.setNotes(request.getNotes());
//        event = eventRepository.save(event); // **確保 `event` 被存到資料庫**
//
//        Long eventId = event.getEventId();
//
//        // 🔹 查找 `EventXPlaceBean`
//        List<EventXPlaceBean> existingPlaces = eventXPlaceRepository.findByEvent_EventId(eventId);
//        Map<Long, EventXPlaceBean> existingPlacesMap = existingPlaces.stream()
//                .filter(e -> e.getEventmappingId() != null)
//                .collect(Collectors.toMap(EventXPlaceBean::getEventmappingId, Function.identity()));
//
//        List<Long> incomingEventmappingIds = request.getPlaces().stream()
//                .map(EventXPlaceRequest::getEventmappingId)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        List<EventXPlaceBean> updatedPlaces = new ArrayList<>();
//
//        // 🔹 遍歷 `places`，更新或新增
//        for (EventXPlaceRequest placeRequest : request.getPlaces()) {
//            EventXPlaceBean eventXPlace;
//
//            if (placeRequest.getEventmappingId() != null && existingPlacesMap.containsKey(placeRequest.getEventmappingId())) {
//                // ✅ 更新 `EventXPlaceBean`
//                eventXPlace = existingPlacesMap.get(placeRequest.getEventmappingId());
//            } else {
//                // ✅ 新增 `EventXPlaceBean`
//                eventXPlace = new EventXPlaceBean();
//                eventXPlace.setEvent(event);
//            }
//
//            PlaceBean place = placeRepository.findById(placeRequest.getPlaceId())
//                    .orElseThrow(() -> new RuntimeException("❌ 找不到地點 (placeId=" + placeRequest.getPlaceId() + ")"));
//
//            eventXPlace.setPlace(place);
//            eventXPlace.setPlaceOrder(placeRequest.getPlaceOrder());
//            eventXPlace.setTravelTime(LocalTime.parse(placeRequest.getTravelTime()));
//            eventXPlace.setStayDuration(LocalTime.parse(placeRequest.getStayDuration()));
//
//            updatedPlaces.add(eventXPlace);
//        }
//
//        // 🔹 批量更新/新增
//        eventXPlaceRepository.saveAll(updatedPlaces);
//
//        // 🔹 刪除不在 `incomingEventmappingIds` 內的舊地點
//        if (!incomingEventmappingIds.isEmpty()) {
//            eventXPlaceRepository.deleteByEvent_EventIdAndNotIn(eventId, incomingEventmappingIds);
//        } else {
//            eventXPlaceRepository.deleteByEvent_EventId(eventId);
//        }
//    }

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
