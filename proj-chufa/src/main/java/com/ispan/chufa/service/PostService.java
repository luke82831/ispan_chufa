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
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PlaceRepository placeRepository;

    // 創建一個 Post 並關聯多個 Place
    public PostBean createPostWithPlaces(PostBean post, Set<Long> placeIds) {
    	Set<PlaceBean> places = new HashSet<>(placeRepository.findAllById(placeIds));
        post.setPlaces(places);
        return postRepository.save(post);
    }

    // 根據 ID 查詢 Post 和其相關聯的 Place
    public PostBean getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }
}
