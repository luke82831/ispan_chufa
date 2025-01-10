package com.ispan.chufa.repository;

import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.Post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class PostDaoImpl implements PostDao {
@PersistenceContext
private EntityManager entityManager;

@Override
public List<Post> find(JSONObject param) {
	// TODO Auto-generated method stub
	return null;
}
}
