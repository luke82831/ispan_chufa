package com.ispan.chufa.repository;

import java.util.List;

import org.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long>,PostDao{


}
