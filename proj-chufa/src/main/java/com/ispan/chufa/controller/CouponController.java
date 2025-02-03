package com.ispan.chufa.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.chufa.domain.CouponBean;
import com.ispan.chufa.repository.CouponRepository;
import com.ispan.chufa.service.CouponService;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponRepository couponRepository;
       
    @Autowired
    private CouponService couponService;

    // 新增優惠券，支援圖片上傳
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<CouponBean> createCoupon(@RequestPart("coupon") CouponBean couponBean,
                                                   @RequestPart(value = "picture", required = false) MultipartFile picture) {
        try {
            if (picture != null && !picture.isEmpty()) {
                couponBean.setPicture(picture.getBytes());
            }
            CouponBean savedCoupon = couponRepository.save(couponBean);
            return ResponseEntity.ok(savedCoupon);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 查詢所有優惠券
    @GetMapping
    public ResponseEntity<List<CouponBean>> getAllCoupons() {
        List<CouponBean> coupons = couponRepository.findAll();
        return ResponseEntity.ok(coupons);
    }

    // 查詢特定優惠券
    @GetMapping("/{id}")
    public ResponseEntity<CouponBean> getCouponById(@PathVariable Long id) {
        Optional<CouponBean> coupon = couponRepository.findById(id);
        return coupon.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 修改優惠券，支援圖片上傳
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<CouponBean> updateCoupon(@PathVariable Long id,
                                                   @RequestPart("coupon") CouponBean updatedCoupon,
                                                   @RequestPart(value = "picture", required = false) MultipartFile picture) {
        Optional<CouponBean> optionalCoupon = couponRepository.findById(id);

        if (optionalCoupon.isPresent()) {
            CouponBean coupon = optionalCoupon.get();
            try {
                coupon.setCouponCode(updatedCoupon.getCouponCode());
                coupon.setRemaining(updatedCoupon.getRemaining());
                coupon.setTitle(updatedCoupon.getTitle());
                coupon.setSubtitle(updatedCoupon.getSubtitle());
                coupon.setContent(updatedCoupon.getContent());
                coupon.setState(updatedCoupon.getState());
                coupon.setWeb(updatedCoupon.getWeb());
                coupon.setstartTime(updatedCoupon.getstartTime());
                coupon.setendTime(updatedCoupon.getendTime());

                if (picture != null && !picture.isEmpty()) {
                    coupon.setPicture(picture.getBytes());
                }

                CouponBean savedCoupon = couponRepository.save(coupon);
                return ResponseEntity.ok(savedCoupon);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // 刪除優惠券
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCoupon(@PathVariable Long id) {
        try {
            couponService.deleteCoupon(id);
            
            // 成功刪除後回傳 JSON 格式訊息
            Map<String, String> response = new HashMap<>();
            response.put("message", "Coupon with id " + id + " has been successfully deleted.");
            
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (RuntimeException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Coupon with id " + id + " not found.");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
