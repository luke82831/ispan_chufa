package com.ispan.chufa.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.CouponBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.MyCouponBean;
import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.repository.EventXPlaceRepository;
import com.ispan.chufa.repository.PlaceRepository;
import com.ispan.chufa.repository.PostRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private EventXPlaceRepository eventXPlaceRepository;


    // 創建一個 Place 並關聯多個 Post
    public PlaceBean createPlaceWithPosts(PlaceBean place, Set<Long> postIds) {
        Set<PostBean> posts = new HashSet<>(postRepository.findAllById(postIds));
        place.setPosts(posts);
        return placeRepository.save(place);
    }

    // 根據 ID 查詢 Place 和其相關聯的 Post
    public PlaceBean getPlaceById(Long id) {
        return placeRepository.findById(id).orElse(null);
    }
    
    
 // 更新指定 ID 的 Place
    public PlaceBean updatePlace(Long id, PlaceBean placeBean) {
        return placeRepository.findById(id).map(place -> {
            place.setPlaceName(placeBean.getPlaceName());
            place.setPlaceAddress(placeBean.getPlaceAddress());
            place.setCity(placeBean.getCity());
            place.setRegion(placeBean.getRegion());
            place.setPlaceType(placeBean.getPlaceType());
            place.setPlaceName(placeBean.getPlaceName());
            place.setCity(placeBean.getCity());
            place.setRegion(placeBean.getRegion());
            place.setPlaceAddress(placeBean.getPlaceAddress());
            place.setLongitude(placeBean.getLongitude());
            place.setLatitude(placeBean.getLatitude());
            place.setPhotos(placeBean.getPhotos());
            place.setPlacePhone(placeBean.getPlacePhone());
            place.setBusinessHours(placeBean.getBusinessHours());
            place.setPlaceInfo(placeBean.getPlaceInfo());
            place.setRating(placeBean.getRating());
            place.setWebsite(placeBean.getWebsite());
            place.setBookingUrl(placeBean.getBookingUrl());
            place.setPrice(placeBean.getPrice());
            place.setPlaceName(placeBean.getPlaceName());
            place.setAccommodationType(placeBean.getAccommodationType());
            place.setReservation(placeBean.isReservation());
            return placeRepository.save(place);
        }).orElse(null);  // 若找不到，回傳 null
    }

    // 刪除指定 ID 的 Place
    @Transactional
    public boolean deletePlace(Long placeId) {
        // 確保該 Place 存在
        Optional<PlaceBean> placeOpt = placeRepository.findById(placeId);
        if (!placeOpt.isPresent()) {
            return false; // 若找不到該 Place
        }

        PlaceBean place = placeOpt.get();

        // 解除 Place 和 Member 之間的多對多關聯
        for (MemberBean member : place.getMembers()) {
            member.getPlaces().remove(place);  // 將 Place 從 Member 的 places 列表中移除        
        }
        
        // 刪除與 Place 相關的所有 EventXPlace 資料
        eventXPlaceRepository.deleteByPlace(place);
        
        // 解除 Place 和 Coupon 之間的關聯
        for (CouponBean coupon : place.getCoupons()) {
            // 解除 MyCouponBean 和 CouponBean 的關聯
            for (MyCouponBean myCoupon : coupon.getMyCoupons()) {
                myCoupon.setCouponBean(null); // 解除與 CouponBean 的關聯
            }
            coupon.setPlace(null);  // 解除 Place 和 Coupon 的關聯
        }
        
        // 解除 Place 和 Post 之間的多對多關聯
        for (PostBean post : place.getPosts()) {
            post.getPlaces().remove(place);  // 從 Post 的 places 列表中移除該 Place
        }

        // 刪除 Place
        placeRepository.deleteById(placeId);

        return true;  // 成功刪除
    }
}
