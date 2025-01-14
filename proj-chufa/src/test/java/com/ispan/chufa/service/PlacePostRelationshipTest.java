//package com.ispan.chufa.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import com.ispan.chufa.domain.PlaceBean;
//import com.ispan.chufa.domain.PlacePostBean;
//import com.ispan.chufa.domain.PostBean;
//import com.ispan.chufa.repository.PlacePostRepository;
//import com.ispan.chufa.repository.PlaceRepository;
//import com.ispan.chufa.repository.PostRepository;
//
//@SpringBootTest
//public class PlacePostRelationshipTest {
//
//    @Autowired
//    private PlaceRepository placeRepository;
//    @Autowired
//    private PostRepository postRepository;
//    @Autowired
//    private PlacePostRepository placePostRepository; // 假設你有PlacePost的Repository
//
//    @Test
//    public void testSavePlacePost() {
//        // 1. 儲存資料到 Post 資料表
//        PostBean post = new PostBean();
//        post.setPostTitle("New Post");
//        post.setPostStatus("Active");
//        post.setPostTime(LocalDateTime.now());
//        postRepository.save(post);  // 儲存到資料庫
//
//        // 2. 儲存資料到 Place 資料表
//        PlaceBean place = new PlaceBean();
//        place.setPlaceName("New Place");
//        place.setPlaceAddress("123 Street");
//        place.setLongitude(120.00);
//        place.setLatitude(23.00);
//        place.setPlacePhone("1234567890");
//        place.setPlaceImage("image_url");
//        place.setPlaceInfo("Some info about the place.");
//        place.setPrice(new BigDecimal("100"));
//        placeRepository.save(place);  // 儲存到資料庫
//
//        // 確保 Place 儲存後已經有 placeId
//        assertNotNull(place.getPlaceId(), "Place should be saved and have an ID");
//
//        // 3. 創建並儲存一個 PlacePostBean 並建立與 Post 和 Place 的關聯
//        PlacePostBean placePost = new PlacePostBean();
//        placePost.setPlace(place);   // 設置關聯的 Place
//        placePost.setPost(post);     // 設置關聯的 Post
//        placePostRepository.save(placePost);  // 儲存到資料庫
//
//        // 4. 驗證 PlacePost 是否成功儲存
//        Optional<PlacePostBean> savedPlacePost = placePostRepository.findById(placePost.getPlacePostId());
//        assertTrue(savedPlacePost.isPresent());  // 確保 PlacePost 被正確儲存
//        // 查詢 postTitle 為 "New Post" 的 placeInfo 並打印
//        List<String> priceList = placePostRepository.findPriceByPostTitle("New Post");
//
//        if (priceList != null && !priceList.isEmpty()) {
//        	priceList.forEach(price -> {
//                System.out.println("Price for 'New Post': " + price);
//            });
//        } else {
//            System.out.println("No price found for 'New Post'");
//        }
// 
//        // 5. 驗證資料是否正確儲存
//        PlacePostBean savedPost = savedPlacePost.get();
//        assertNotNull(savedPost.getPlace(), "Place should be associated");
//        assertNotNull(savedPost.getPost(), "Post should be associated");
//        assertEquals(savedPost.getPlace().getPlaceName(), place.getPlaceName());  // 驗證 Place 的名稱
//        assertEquals(savedPost.getPost().getPostTitle(), post.getPostTitle());  // 驗證 Post 的標題
//    }
//}
