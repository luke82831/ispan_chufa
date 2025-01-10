package com.ispan.chufa.repository;

import java.util.List;

import org.json.JSONObject;

import com.ispan.chufa.domain.Post;

public interface PostDao {
	public abstract List<Post> find(JSONObject param);
}
