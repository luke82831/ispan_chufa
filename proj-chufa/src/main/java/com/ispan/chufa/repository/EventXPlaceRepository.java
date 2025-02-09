package com.ispan.chufa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.EventBean;
import com.ispan.chufa.domain.EventXPlaceBean;
import com.ispan.chufa.domain.PlaceBean;

@Repository
public interface EventXPlaceRepository extends JpaRepository<EventXPlaceBean, Long> {
    // 根據 placeBean 刪除所有資料
    void deleteByPlace(PlaceBean place);  // 修改方法名稱為 deleteByPlace
    
 // 依 eventId 查詢所有 placeId
    List<EventXPlaceBean> findByEvent_EventId(Long eventId);

    // 依 eventId 與 placeId 查詢
    Optional<EventXPlaceBean> findByEvent_EventIdAndPlace_PlaceId(Long eventId, String placeId);

    // 計算該 eventId 目前的地點數量
    int countByEvent(EventBean event);
}