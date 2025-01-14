package com.ispan.chufa.repository;

import java.util.List;

import org.json.JSONObject;

import com.ispan.chufa.domain.Post;
import com.ispan.chufa.dto.PostDTO;

public interface PostDao {
	public abstract List<Post> find(JSONObject param);
	public abstract List<PostDTO>findPostsByTitle(JSONObject param);
	public abstract List<Post> listall(JSONObject param);
}
