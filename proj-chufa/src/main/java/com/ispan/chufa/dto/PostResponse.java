package com.ispan.chufa.dto;

import java.util.ArrayList;
import java.util.List;

import com.ispan.chufa.domain.Post;

public class PostResponse {
	 private List<Post> postlist;

	public List<Post> getPostlist() {
		return postlist;
	}

	public void setPostlist(List<Post> postlist) {
		this.postlist = postlist;
	}
	

}
