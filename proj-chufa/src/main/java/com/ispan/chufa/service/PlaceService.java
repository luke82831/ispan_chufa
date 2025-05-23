package com.ispan.chufa.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    
    //根據 placeIds 取得對應的地點資料
    public List<PlaceBean> getPlacesByIds(List<Long> placeIds) {
        return placeRepository.findAllById(placeIds);
    }
        
    // 更新指定 ID 的 Place
    public PlaceBean updatePlace(Long id, PlaceBean placeBean) {
        return placeRepository.findById(id).map(place -> {
            place.setGooglemapPlaceId(placeBean.getGooglemapPlaceId());
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
            place.setPriceLevel(placeBean.getPriceLevel());
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
        if (placeOpt.isEmpty()) {
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

	public PlaceBean findPlaceByAddress(String placeAddress) {
        return placeRepository.findByPlaceAddress(placeAddress); // 呼叫 Repository 方法
	}

	public PlaceBean findPlaceByGooglemapPlaceId(String googlemapPlaceId) {
        return placeRepository.findPlaceByGooglemapPlaceId(googlemapPlaceId); // 呼叫 Repository 方法
	}

	 // 保存 Place 詳細資訊的方法，接收名稱和圖片 URL 列表
    public void savePlaceDetails(String name, List<String> photosUrls) {
        PlaceBean place = new PlaceBean();
        place.setPlaceName(name);

        // 將照片 URL 列表轉換為 JSON 字串並儲存
        place.setPhotos(photosUrls);

        // 儲存到資料庫
        placeRepository.save(place);
    }
    
    
    
    
    public List<PlaceBean> getAllPlaces() {
        return placeRepository.findAll();
    }
    
 // 取得分頁數據
    public Page<PlaceBean> getPlacesWithPagination(int page, int size) {
        return placeRepository.findAll(PageRequest.of(page, size));
    }

    
}
