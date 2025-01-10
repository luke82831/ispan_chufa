package com.ispan.chufa.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.Post;
import com.ispan.chufa.dto.PostResponse;
import com.ispan.chufa.service.PostService;

@RestController
@RequestMapping("/search")
public class PostController {
 @Autowired 
 private PostService postService;
 
 @PostMapping("/post")
 public PostResponse find(@RequestBody JSONObject json) {
	 PostResponse responseBean = new PostResponse();


     List<Post> posts = postService.findPostsByCriteria(json);
     if (posts != null && !posts.isEmpty()) {
         responseBean.setPostlist(posts);
     } else {
         responseBean.setPostlist(new ArrayList<>());
     }

     return responseBean;
 }
}
