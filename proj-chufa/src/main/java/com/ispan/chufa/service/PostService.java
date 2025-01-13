package com.ispan.chufa.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.ispan.chufa.domain.Post;
import com.ispan.chufa.dto.FollowResponse;
import com.ispan.chufa.repository.PostRepository;

@Service
//@Transactional
public class PostService {
	@Autowired
	PostRepository postRepository;

	public List<Post> findPostsByCriteria(JSONObject param) {
		try {
			JSONObject blog = new JSONObject(param);
			return postRepository.findPostsByTitle(blog);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
