//package com.ispan.chufa.service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.ispan.chufa.domain.FollowBean;
//import com.ispan.chufa.domain.InteractionBean;
//import com.ispan.chufa.domain.MemberBean;
//import com.ispan.chufa.domain.PostBean;
//import com.ispan.chufa.dto.PostDto2;
//import com.ispan.chufa.repository.InteractionRepository;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.TypedQuery;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.persistence.criteria.Predicate;
//import jakarta.persistence.criteria.Root;
//import jakarta.persistence.criteria.Subquery;
//
//@Service
//public class PostShowService {
//	@Autowired
//	private EntityManager entityManager;
//	
//	@Autowired
//	private InteractionRepository interactionRepository;
//
//	public List<PostDto2> getPostsForFollower(Long followerId,int page, int pageSize) {
//		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//		int firstResult = (page - 1) * pageSize;
//
//        // 原始帖子查詢
//        CriteriaQuery<PostDto2> originalPostQuery = cb.createQuery(PostDto2.class);
//        Root<PostBean> postRoot = originalPostQuery.from(PostBean.class);
//        Subquery<Long> followSubquery = originalPostQuery.subquery(Long.class);
//        Root<FollowBean> followRoot = followSubquery.from(FollowBean.class);
//        followSubquery.select(followRoot.get("followed").get("userid"))
//                .where(cb.equal(followRoot.get("follower").get("userid"), followerId));
//
//        // 選擇原始帖子
//        Predicate followedCondition = postRoot.get("member").get("userid").in(followSubquery);
//
//        originalPostQuery.multiselect(
//                postRoot.get("postid"), // postId
//                postRoot.get("postTitle"),  // postTitle
//                postRoot.get("postContent"),  // postContent
//                postRoot.get("postLink"),  // postLink
//                postRoot.get("postStatus"),  // postStatus
//                postRoot.get("postTime"),  // timestamp
//                cb.literal((LocalDateTime) null),  // interactionTime
//                cb.literal(null),  // interactionId
//                cb.literal(null),  // interactionName
//                cb.literal(null),  // interactionNickname
//                cb.literal(null),  // interactionProfilePicture
//                cb.literal("ORIGINAL"),  // postType
//                postRoot.get("member").get("userid"),  // authorId
//                postRoot.get("member").get("name"),  // authorName
//                postRoot.get("member").get("nickname"),  // authorNickname
//                postRoot.get("member").get("profilePicture"))  // authorProfilePicture
//                .where(followedCondition);
//
//       originalPostQuery.orderBy(cb.desc(postRoot.get("postTime")));
//        List<PostDto2> originalPosts = entityManager.createQuery(originalPostQuery).getResultList();
//        
////        TypedQuery<PostDto2> typedQuery = entityManager.createQuery(originalPostQuery)
////        		.setFirstResult(1)
////        		.setMaxResults(2);
//        
//       
//        // 互動帖子查詢
//        CriteriaQuery<PostDto2> interactionQuery = cb.createQuery(PostDto2.class);
//        Root<InteractionBean> interactionRoot = interactionQuery.from(InteractionBean.class);
//        Root<PostBean> interactionPostRoot = interactionQuery.from(PostBean.class);
//        Root<MemberBean> interactionMemberRoot = interactionQuery.from(MemberBean.class);
//
//        // 子查詢：獲取粉絲所關注的作者
//        Subquery<Long> interactionFollowSubquery = interactionQuery.subquery(Long.class);
//        Root<FollowBean> interactionFollowRoot = interactionFollowSubquery.from(FollowBean.class);
//        interactionFollowSubquery.select(interactionFollowRoot.get("followed").get("userid"))
//                .where(cb.equal(interactionFollowRoot.get("follower").get("userid"), followerId));
//        // 轉發條件
//        Predicate interactionJoinCondition = cb.equal(interactionRoot.get("post").get("postid"),
//                interactionPostRoot.get("postid"));
//        Predicate interactionFollowedCondition = interactionRoot.get("member").get("userid")
//                .in(interactionFollowSubquery);  // 這裡過濾必須是粉絲所關注的成員
//        Predicate interactionTypeCondition = cb.equal(interactionRoot.get("interactionType"), "REPOST");
//
//
//        interactionQuery
//                .multiselect(
//                        interactionPostRoot.get("postid"),
//                        interactionPostRoot.get("postTitle"),
//                        interactionPostRoot.get("postContent"),
//                        interactionPostRoot.get("postLink"),
//                        interactionPostRoot.get("postStatus"),
//                        interactionPostRoot.get("postTime"),
//                        interactionRoot.get("interactionTime"),
//                        interactionRoot.get("member").get("userid"),
//                        interactionRoot.get("member").get("name"),
//                        interactionRoot.get("member").get("nickname"),
//                        interactionRoot.get("member").get("profilePicture"),
//                        cb.literal("REPOST"), // postType
//                        interactionPostRoot.get("member").get("userid"),
//                        interactionPostRoot.get("member").get("name"),
//                        interactionPostRoot.get("member").get("nickname"),
//                        interactionPostRoot.get("member").get("profilePicture"))
//                .where(cb.and(interactionJoinCondition, interactionFollowedCondition, interactionTypeCondition));
//        
//        interactionQuery.distinct(true);
//        interactionQuery.orderBy(cb.desc(interactionRoot.get("interactionTime")));
//        
////        TypedQuery<PostDto2> typedQuery1 = entityManager.createQuery(interactionQuery)
////        		.setFirstResult(1)
////        		.setMaxResults(2);
////
////        
//        
//        List<PostDto2> interactionPosts = entityManager.createQuery(interactionQuery).getResultList();
//
//        // 合併兩個查詢的結果
//        List<PostDto2> allPosts = new ArrayList<>();
//        allPosts.addAll(originalPosts);
//        allPosts.addAll(interactionPosts);
//        
//        for (PostDto2 postDTO : allPosts) {
//            long likeCount = interactionRepository.countByPost_PostidAndInteractionType(postDTO.getPostid(), "LIKE");
//            postDTO.setLike(likeCount);
//            long repostCount = interactionRepository.countByPost_PostidAndInteractionType(postDTO.getPostid(),
//					"REPOST");
//			postDTO.setRepost(repostCount);
//
//        }
//
//        // 按時間排序：直接使用 postTime 或 interactionTime 排序
//        allPosts.sort((post1, post2) -> {
//            // 取得 post1 和 post2 的時間值（優先 interactionTime，其次 postTime）
//            LocalDateTime time1 = post1.getInteractionTime() != null ? post1.getInteractionTime() : post1.getpostTime();
//            LocalDateTime time2 = post2.getInteractionTime() != null ? post2.getInteractionTime() : post2.getpostTime();
//
//            // 比較時間，降序排序
//            return time2.compareTo(time1);
//        });
//        
//
//        
//        return allPosts;
//    }
//}
