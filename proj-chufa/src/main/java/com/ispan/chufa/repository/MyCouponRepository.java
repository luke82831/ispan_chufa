package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.CouponBean;
import com.ispan.chufa.domain.MyCouponBean;

public interface MyCouponRepository extends JpaRepository<MyCouponBean, Long> {

    // 根據 couponBean 刪除所有資料
    void deleteByCouponBean(CouponBean couponBean);
}