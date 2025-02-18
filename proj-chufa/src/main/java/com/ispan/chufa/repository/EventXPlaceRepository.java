package com.ispan.chufa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.EventXPlaceBean;
import com.ispan.chufa.domain.PlaceBean;

import jakarta.transaction.Transactional;

@Repository
public interface EventXPlaceRepository extends JpaRepository<EventXPlaceBean, Long> {
   
    // 依 eventId 查詢所有 placeId
    List<EventXPlaceBean> findByEvent_EventId(Long eventId);

    // 依 eventId 與 placeId 查詢
    Optional<EventXPlaceBean> findByEvent_EventIdAndPlace_PlaceId(Long eventId, Long placeId);

    // 計算該 eventId 目前的地點數量
    int countByEvent(EventBean event);
    
    @Query("SELECT ep.place.placeId FROM EventXPlaceBean ep WHERE ep.event.eventId = :eventId")
    List<String> findPlaceIdsByEventId(@Param("eventId") Long eventId);
    
    // 根據 placeBean 刪除所有資料
    void deleteByPlace(PlaceBean place); 
    
    @Modifying
    @Transactional
    @Query("DELETE FROM EventXPlaceBean ep WHERE ep.event.eventId = :eventId")
    void deleteByEventId(@Param("eventId") Long eventId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM EventXPlaceBean e WHERE e.event.eventId = :eventId AND e.eventmappingId NOT IN (:eventmappingIds)")
    void deleteByEventIdAndNotIn(@Param("eventId") Long eventId, @Param("eventmappingIds") List<Long> eventmappingIds);
   
    // 刪除 `eventId` 內不在 `incomingEventmappingIds` 內的地點
    @Modifying
    @Transactional
    @Query("DELETE FROM EventXPlaceBean e WHERE e.event.eventId = :eventId AND e.eventmappingId NOT IN :ids")
    void deleteByEvent_EventIdAndNotIn(@Param("eventId") Long eventId, @Param("ids") List<Long> ids);

    // 如果 `incomingEventmappingIds` 為空，則刪除該 `eventId` 內所有地點
    @Modifying
    @Transactional
    @Query("DELETE FROM EventXPlaceBean e WHERE e.event.eventId = :eventId")
    void deleteByEvent_EventId(@Param("eventId") Long eventId);
}