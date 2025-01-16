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
import com.ispan.chufa.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // 創建 Post 並關聯多個 Place
    @PostMapping("/create")
    public ResponseEntity<PostBean> createPostWithPlaces(@RequestBody PostBean post) {
        // 假設前端會提供一個 post 和一個包含 Place ID 的 list
        Set<Long> placeIds = post.getPlaces().stream()
                                 .map(PlaceBean::getPlaceId)
                                 .collect(Collectors.toSet());
        PostBean createdPost = postService.createPostWithPlaces(post, placeIds);
        return ResponseEntity.ok(createdPost);
    }

    // 查詢指定 ID 的 Post 和其關聯的 Places
    @GetMapping("/{id}")
    public ResponseEntity<PostBean> getPostById(@PathVariable Long id) {
        PostBean post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }
}
