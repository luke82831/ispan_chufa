package com.ispan.chufa.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.FollowBean;
import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.domain.MemberBean;
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
import jakarta.persistence.criteria.Subquery;

@Repository
public class PostDaoImpl implements PostDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private InteractionRepository interactionRepository;

	@Override
	public List<PostBean> find(JSONObject param) {
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
		

		// 根據 postid 查詢
		if (!param.isNull("postid")) {
			Long postid = param.getLong("postid");
			Predicate userPredicate = criteriaBuilder.equal(postRoot.get("postid"), postid);
			predicates.add(userPredicate);
		}
		
		//like
		if (!param.isNull("likedBy")) {
		    Long currentid = param.getLong("likedBy"); // 取得當前用戶 ID
		    // 過濾點贊用戶和互動類型爲 "LIKE"
		    Predicate likedByMePredicate = criteriaBuilder.and(
		        criteriaBuilder.equal(interactionJoin.get("member").get("userid"), currentid),
		        criteriaBuilder.equal(interactionJoin.get("interactionType"), "LIKE")
		    );
		    predicates.add(likedByMePredicate);
		}

		// 根據關注的人查詢，followerId所關注的人查詢
		// 子查詢，用於查找被關注者 ID
		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<FollowBean> followRoot = subquery.from(FollowBean.class);
		if (!param.isNull("followerId")) {
			Long followerId = param.getLong("followerId");
			subquery.select(followRoot.get("followed").get("userid"))
					.where(criteriaBuilder.equal(followRoot.get("follower").get("userid"), followerId));

			// 主查詢條件：Post 的作者 ID 在子查詢結果中
			Predicate userPredicate = postRoot.get("member").get("userid").in(subquery);
			predicates.add(userPredicate); 
			
			// 根據關注的人的轉發查詢
		    if (!param.isNull("repost") && param.getBoolean("repost")) {
		        // 查詢用戶轉發的貼文（即 REPOST）
		       predicates.add(criteriaBuilder.equal(interactionJoin.get("interactionType"), "REPOST"));
		    }
		}

		if (!predicates.isEmpty()) {
			criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
		}
		
		// 分頁設定
	    int page = param.has("page") ? param.getInt("page") : 1;  // 預設是第1頁
	    int size = param.has("size") ? param.getInt("size") : 2; // 預設每頁10條
	    int firstResult = (page - 1) * size;
	    
		// 按 postTime 排序（按降序排列最新的貼文）
		if (!param.isNull("sortByTime") && param.getBoolean("sortByTime")) {
			criteriaQuery.orderBy(criteriaBuilder.desc(postRoot.get("postTime")));
		}
		
		String sql = entityManager.createQuery(criteriaQuery)
			    .unwrap(org.hibernate.query.Query.class)
			    .getQueryString();
			System.out.println("Generated SQL: " + sql);

		// 建立查詢並執行
		TypedQuery<PostBean> query = entityManager.createQuery(criteriaQuery);
		
		query.setFirstResult(firstResult);
	    query.setMaxResults(size);
		


		List<PostBean> post = query.getResultList();

		List<PostDTO> postDTOList = new ArrayList<>();
		for (PostBean postlist : post) {
			// 將 Post 的屬性複製到 PostDTO
			PostDTO postDTO = new PostDTO();
			BeanUtils.copyProperties(postlist, postDTO);
			
			// 判斷是否為轉發並設置 originalPost 和 repost
	        // 轉發的貼文應該有指向原始貼文的關聯
	        if (!param.isNull("repost")&&postlist.getInteractions() != null && postlist.getInteractions().stream().anyMatch(interaction -> "REPOST".equals(interaction.getInteractionType()))) {
	            // 檢查是否有轉發操作
	        	 for (InteractionBean interaction : postlist.getInteractions()) {
	                 if ("REPOST".equals(interaction.getInteractionType())) {
	                	 MemberInfo repostmem = new MemberInfo();
	     				 // 複製 Member 屬性到 MemberInfo
	     				 BeanUtils.copyProperties(interaction.getMember(), repostmem);
	                	 postDTO.setRepostMember(repostmem);
	                     postDTO.setRepost(true); // 設定為轉發貼文
	                     break;
	                 }
	        	 }
	        } else {
	            postDTO.setRepost(false); // 如果不是轉發則設置為 false
	        }
	        
	        
			// 計算點讚數
			long likeCount = interactionRepository.countByPost_PostidAndInteractionType(postlist.getPostid(), "LIKE");
			postDTO.setLikeCount(likeCount);
			long repostCount = interactionRepository.countByPost_PostidAndInteractionType(postlist.getPostid(),
					"REPOST");
			postDTO.setRepostCount(repostCount);

			// 檢查 Post 是否有 Member（假設 Member 是一個巢狀物件）
			if (postlist.getMember() != null||postlist.getCommentBeans()!=null) {
				MemberInfo memberDTO = new MemberInfo();
				// 複製 Member 屬性到 MemberInfo
				BeanUtils.copyProperties(postlist.getMember(), memberDTO);
				postDTO.setMember(memberDTO);
				
//				 // 如果 CommentBeans 不為空，則轉換每個 CommentBean 為 CommentDTO
//		        if (postlist.getCommentBeans() != null) {
//		            List<CommentDTO> commentDTOList = new ArrayList<>();
//		            for (CommentBean commentBean : postlist.getCommentBeans()) {
//		                CommentDTO commentDTO = new CommentDTO();
//		                BeanUtils.copyProperties(commentBean, commentDTO);
//		                commentDTOList.add(commentDTO);
//		            }
//		            postDTO.setCommentDTO(commentDTOList);  // 假設 PostDTO 中有 commentDTOList 來存儲多個 CommentDTO
//		        }
			}
			// 把轉換後的 PostDTO 加入列表
			postDTOList.add(postDTO);
		}

		// 根據點讚數排序
		if (!param.isNull("sortByLikes") && param.getBoolean("sortByLikes")) {
			postDTOList.sort(Comparator.comparingLong(PostDTO::getLikeCount).reversed());
			// criteriaQuery.orderBy(criteriaBuilder.desc(postRoot.get("likeCount")));
		}
		
		if (!param.isNull("sortByTime") && param.getBoolean("sortByTime")) {
			postDTOList.sort(Comparator.comparing(PostDTO::getPostTime).reversed());
		}
		return postDTOList;
	}
}
