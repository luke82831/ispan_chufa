package com.ispan.chufa.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.dto.MemberInfo;
import com.ispan.chufa.dto.PostDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class PostDaoImpl implements PostDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private InteractionRepository interactionRepository;

	@Override
	public List<PostBean> find(JSONObject param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PostBean> listall(JSONObject param) {
		return null;
	}

	@Override
	public List<PostDTO> findPostsByTitle(JSONObject param) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PostBean> criteriaQuery = criteriaBuilder.createQuery(PostBean.class);
		// 建立根物件，表示查詢的實體（Post）
		Root<PostBean> postRoot = criteriaQuery.from(PostBean.class);
		// 準備條件列表
		List<Predicate> predicates = new ArrayList<>();
		
		// Join 到點贊表 (interaction 表)
	    Join<PostBean, InteractionBean> interactionJoin = postRoot.join("interactions", JoinType.LEFT);


		if (!param.isNull("postTitle")) {
			String titleKeyword = param.getString("postTitle");
			Predicate titleLike = criteriaBuilder.like(postRoot.get("postTitle"), "%" + titleKeyword + "%");
			predicates.add(titleLike);
		}
		
		// 根據 userId 查詢
	    if (!param.isNull("userid")) {
	        Long userId = param.getLong("userid");
	        Predicate userPredicate = criteriaBuilder.equal(postRoot.get("member").get("userid"), userId);
	        predicates.add(userPredicate);
	    }

		if (!predicates.isEmpty()) {
			criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
		}
		
		// 按 postTime 排序（按降序排列最新的貼文）
		if(!param.isNull("sortByTime")&&param.getBoolean("sortByTime")) {
	    criteriaQuery.orderBy(criteriaBuilder.desc(postRoot.get("postTime")));
		}
	    
		// 建立查詢並執行
		TypedQuery<PostBean> query = entityManager.createQuery(criteriaQuery);

		List<PostBean> post = query.getResultList();

		List<PostDTO> postDTOList = new ArrayList<>();
		for (PostBean postlist : post) {
			// 將 Post 的屬性複製到 PostDTO
			PostDTO postDTO = new PostDTO();
			BeanUtils.copyProperties(postlist, postDTO);

			// 計算點讚數
            long likeCount = interactionRepository.countByPost_PostidAndInteractionType(postlist.getPostid(), "LIKE");
            postDTO.setLikeCount(likeCount); 
            long repostCount=interactionRepository.countByPost_PostidAndInteractionType(postlist.getPostid(), "REPOST");
            postDTO.setRepostCount(repostCount);
            
			// 檢查 Post 是否有 Member（假設 Member 是一個巢狀物件）
			if (postlist.getMember() != null) {
				MemberInfo memberDTO = new MemberInfo();
				// 複製 Member 屬性到 MemberInfo
				BeanUtils.copyProperties(postlist.getMember(), memberDTO);
				postDTO.setMember(memberDTO);
			}
			// 把轉換後的 PostDTO 加入列表
			postDTOList.add(postDTO);
		}
		
		// 根據點讚數排序
	    if (!param.isNull("sortByLikes") && param.getBoolean("sortByLikes")) {
	    	postDTOList.sort(Comparator.comparingLong(PostDTO::getLikeCount).reversed());
	    	  //criteriaQuery.orderBy(criteriaBuilder.desc(postRoot.get("likeCount")));
	    }
	    
		return postDTOList;
	}

}
