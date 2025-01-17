package com.ispan.chufa.dto;

import java.util.List;

import com.ispan.chufa.domain.PostBean;

public class PostResponse {
	 private List<PostBean> postlist;
	 private List<PostDTO> postdto;

	public List<PostBean> getPostlist() {
		return postlist;
	}

	public void setPostlist(List<PostBean> postlist) {
		this.postlist = postlist;
	}

	public List<PostDTO> getPostdto() {
		return postdto;
	}

	public void setPostdto(List<PostDTO> postdto) {
		this.postdto = postdto;
	}
	

}
