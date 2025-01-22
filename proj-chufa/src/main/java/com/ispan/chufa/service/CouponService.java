package com.ispan.chufa.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.CouponBean;
import com.ispan.chufa.repository.CouponRepository;
import com.ispan.chufa.repository.MyCouponRepository;

import jakarta.transaction.Transactional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MyCouponRepository myCouponRepository;

    @Transactional
    public void deleteCoupon(Long couponId) {
        // 取得優惠券資料
        Optional<CouponBean> couponOpt = couponRepository.findById(couponId);
        
        if (couponOpt.isPresent()) {
            CouponBean coupon = couponOpt.get();
            // 先刪除所有與該優惠券相關的使用者優惠券資料
            myCouponRepository.deleteByCouponBean(coupon);

            // 刪除優惠券資料
            couponRepository.deleteById(couponId);
        } else {
            // 異常直接拋出 RuntimeException
            throw new RuntimeException("Coupon with id " + couponId + " not found");
        }
    }
}
