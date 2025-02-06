package com.ispan.chufa.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.FollowBean;
import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.domain.PlaceBean;
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
	
	@Autowired 
	@Lazy
	private PostRepository postRepository;

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
		
		//根據地區查詢
		if(!param.isNull("place")) {
			 String place = param.getString("place");
			 Join<PostBean, PlaceBean> placeJoin = postRoot.join("place",JoinType.INNER);
			 Predicate placePredicate = criteriaBuilder.equal(placeJoin.get("city"), place);
			 predicates.add(placePredicate);
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

		// 顯示用戶like的貼文
		if (!param.isNull("likedBy")) {
			Long currentid = param.getLong("likedBy"); // 取得當前用戶 ID
			// 過濾點贊用戶和互動類型爲 "LIKE"
			Predicate likedByMePredicate = criteriaBuilder.and(
					criteriaBuilder.equal(interactionJoin.get("member").get("userid"), currentid),
					criteriaBuilder.equal(interactionJoin.get("interactionType"), "LIKE"));
			predicates.add(likedByMePredicate);
		}
		
		// 顯示用戶collect的貼文
		if (!param.isNull("collectBy")) {
			Long currentid = param.getLong("collectBy"); // 取得當前用戶 ID
			// 過濾點贊用戶和互動類型爲 "LIKE"
			Predicate likedByMePredicate = criteriaBuilder.and(
					criteriaBuilder.equal(interactionJoin.get("member").get("userid"), currentid),
					criteriaBuilder.equal(interactionJoin.get("interactionType"), "collect"));
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
		}

		if (!predicates.isEmpty()) {
			criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
		}

		// 分頁設定
		int page = param.has("page") ? param.getInt("page") : 1; // 預設是第1頁
		int size = param.has("size") ? param.getInt("size") : 10; // 預設每頁10條
		int firstResult = (page - 1) * size;

		// 按 postTime 排序（按降序排列最新的貼文）
		if (!param.isNull("sortByTime") && param.getBoolean("sortByTime")) {
			criteriaQuery.orderBy(criteriaBuilder.desc(postRoot.get("postTime")));
		}
		
		
		if (!param.isNull("sortByLikes") && param.getBoolean("sortByLikes")) {
		    Subquery<Long> likeCountSubquery = criteriaQuery.subquery(Long.class);
		    Root<InteractionBean> interactionRoot = likeCountSubquery.from(InteractionBean.class);
		    likeCountSubquery.select(criteriaBuilder.count(interactionRoot))
		                     .where(criteriaBuilder.equal(interactionRoot.get("post").get("postid"), postRoot.get("postid")));		    
		    criteriaQuery.orderBy(
		        criteriaBuilder.desc(likeCountSubquery), // 按点赞数排序
		        criteriaBuilder.asc(postRoot.get("postid")) // 按 postid 排序，确保唯一性
		    );
		}

		String sql = entityManager.createQuery(criteriaQuery).unwrap(org.hibernate.query.Query.class).getQueryString();
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

			// 填充作者資訊
			MemberInfo memberInfo = new MemberInfo();
			BeanUtils.copyProperties(postlist.getMember(), memberInfo);
			postDTO.setMember(memberInfo);
			// 判斷是否為轉發並設置 originalPost 和 repost
			// 轉發的貼文應該有指向原始貼文的關聯
			if (!param.isNull("repost") && postlist.getForwardedFrom() != null) {
				// 如果有轉發來源，且深度限制未達上限，繼續處理
				if (postlist.getForwardedFrom() != null) {
					PostDTO repostDTO = new PostDTO();
					BeanUtils.copyProperties(postlist.getForwardedFrom(), repostDTO);
					MemberInfo repostmem = new MemberInfo();
					BeanUtils.copyProperties(postlist.getForwardedFrom().getMember(), repostmem);
					repostDTO.setMember(repostmem);
					postDTO.setRepostDTO(repostDTO);
					postDTO.setRepost(true); // 設定為轉發貼文
				}
			} else {
				postDTO.setRepost(false); // 如果不是轉發則設置為 false
			}

			// 計算點讚數
			long likeCount = interactionRepository.countByPost_PostidAndInteractionType(postlist.getPostid(), "LIKE");
			postDTO.setLikeCount(likeCount);
			long repostCount = postRepository.countByForwardedFrom(postlist);
			postDTO.setRepostCount(repostCount);

			// 把轉換後的 PostDTO 加入列表
			postDTOList.add(postDTO);
		}
//
//		// 根據點讚數排序
//		if (!param.isNull("sortByLikes") && param.getBoolean("sortByLikes")) {
//			postDTOList.sort(Comparator.comparingLong(PostDTO::getLikeCount).reversed());
//			//criteriaQuery.orderBy(criteriaBuilder.desc(postRoot.get("likeCount")));
//		}


		return postDTOList;
	}
}