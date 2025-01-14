package com.ispan.chufa.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.dto.InteractionDTO;
import com.ispan.chufa.dto.PostDTO;
import com.ispan.chufa.dto.PostResponse;
import com.ispan.chufa.service.PostService;

@RestController
@RequestMapping("/search")
public class PostController {
	@Autowired
	private PostService postService;

	@PostMapping("/post")
//	@JsonView(Views.Public.class)
	public PostResponse find(@RequestBody JSONObject json) {
		PostResponse responseBean = new PostResponse();

		List<PostDTO> posts = postService.findPostsByCriteria(json);
		if (posts != null && !posts.isEmpty()) {
			responseBean.setPostdto(posts);
		} else {
			responseBean.setPostdto(new ArrayList<>());
		}

		return responseBean;
	}

	@PostMapping("/listall")
	public String postMethodName(@RequestBody String entity) {
		// TODO: process POST request

		return entity;
	}
	
	@PostMapping("/postinteraction")
	public InteractionDTO interact(@RequestBody JSONObject json) {
		InteractionBean interactionBean = new InteractionBean();
		InteractionDTO interactionDTO=new InteractionDTO();
		
		boolean interaction=postService.performInteraction(json);

        if (interaction) {
             interactionDTO.setSuccess(true);
             interactionDTO.setInteractionbean(interactionBean);
        } else {
        	interactionDTO.setSuccess(false);
        	}
		
		return interactionDTO;
		
	}

}
