package com.ispan.chufa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.CouponBean;
import com.ispan.chufa.domain.PlaceBean;

@Repository
public interface CouponRepository extends JpaRepository<CouponBean, Long> {
    // 根據 placeBean 刪除所有資料
    void deleteByPlace(PlaceBean place);
}
