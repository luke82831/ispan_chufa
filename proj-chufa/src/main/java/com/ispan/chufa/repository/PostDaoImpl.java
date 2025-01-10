package com.ispan.chufa.repository;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.Post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class PostDaoImpl implements PostDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Post> find(JSONObject param) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Post>listall(JSONObject param){
		return null;	
	}

	@Override
	public List<Post> findPostsByTitle(JSONObject param) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
		// 建立根物件，表示查詢的實體（Post）
		Root<Post> postRoot = criteriaQuery.from(Post.class);
		// 準備條件列表
		List<Predicate> predicates = new ArrayList<>();
		
		if (!param.isNull("postTitle")) {
	        String titleKeyword = param.getString("postTitle");
	        Predicate titleLike = criteriaBuilder.like(postRoot.get("postTitle"), "%" + titleKeyword + "%");
	        predicates.add(titleLike);
	    }
		
		if (!predicates.isEmpty()) {
	        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
	    }
		// 建立查詢並執行
	    TypedQuery<Post> query = entityManager.createQuery(criteriaQuery);
		
		return query.getResultList();
	}

}
