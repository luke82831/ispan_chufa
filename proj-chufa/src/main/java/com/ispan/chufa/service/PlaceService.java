package com.ispan.chufa.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.repository.PlaceRepository;
import com.ispan.chufa.repository.PostRepository;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PostRepository postRepository;

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
}
