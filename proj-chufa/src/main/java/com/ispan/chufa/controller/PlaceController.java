package com.ispan.chufa.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.service.PlaceService;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    // 創建 Place 並關聯多個 Post
    @PostMapping("/create")
    public ResponseEntity<PlaceBean> createPlaceWithPosts(@RequestBody PlaceBean place) {
        // 假設前端會提供一個 place 和一個包含 Post ID 的 list
        Set<Long> postIds = place.getPosts().stream()
                .map(PostBean::getPostid)
                .collect(Collectors.toSet());
        PlaceBean createdPlace = placeService.createPlaceWithPosts(place, postIds);
        return ResponseEntity.ok(createdPlace);
    }

    // 查詢指定 ID 的 Place 和其關聯的 Posts
    @GetMapping("/{id}")
    public ResponseEntity<PlaceBean> getPlaceById(@PathVariable Long id) {
        PlaceBean place = placeService.getPlaceById(id);
        if (place == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(place);
    }

    @PostMapping("/test")
    public ResponseEntity<String> testPost(@RequestBody String json) {
        return ResponseEntity.ok("Received: " + json);
    }

}
