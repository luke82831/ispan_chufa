package com.ispan.chufa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.FollowBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.dto.MemberInfo;
import com.ispan.chufa.repository.FollowRepository;
import com.ispan.chufa.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class FollowService {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	FollowRepository followRepository;

	@Autowired
	MemberRepository memberRepository;
	
	 public boolean isUserFollowing(Long followerId, Long followedId) {
	        return followRepository.existsByFollower_UseridAndFollowed_UseridAndFollowStatus(followerId, followedId, "active");
	    }

	public FollowBean follow(Long followerId, Long followedId) {
		// TODO Auto-generated method stub
		// 確保 follower 和 followed 已存在
		MemberBean follower = memberRepository.findById(followerId)
				.orElseThrow(() -> new RuntimeException("Follower not found"));
		MemberBean followed = memberRepository.findById(followedId)
				.orElseThrow(() -> new RuntimeException("Followed member not found"));

		// 檢查是否已經存在該關注記錄
		Optional<FollowBean> existingFollow = followRepository.findByFollower_UseridAndFollowed_Userid(followerId,
				followedId);

		if (existingFollow.isPresent()) {
			FollowBean followList = existingFollow.get();
			followRepository.deleteById(followList.getFlid());
			return null;
		}
		// 建立新的 FollowList 物件
		else {
			FollowBean followList = new FollowBean();
			followList.setFollower(follower); // 設定關注者
			followList.setFollowed(followed); // 設定被關注者
			followList.setFollowTime(LocalDateTime.now()); // 設定關注時間
			followList.setFollowStatus("ACTIVE"); // 設定關注狀態為 "ACTIVE"
			return followRepository.save(followList); // 返回儲存的 FollowList
		}
	}

	// fans
	public List<MemberInfo> getFollowedList(Long followerid) {
		// 創建 CriteriaBuilder 和 CriteriaQuery
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MemberInfo> query = cb.createQuery(MemberInfo.class);

		// 根實體
		Root<FollowBean> followRoot = query.from(FollowBean.class);

		// 聯接 FollowBean 和 MemberBean
		Join<FollowBean, MemberBean> followerJoin = followRoot.join("followed", JoinType.INNER);

		// 選擇字段（id, name, profilePicture）
		query.select(cb.construct(MemberInfo.class, followerJoin.get("id"), followerJoin.get("name"),
				followerJoin.get("profilePicture"), followerJoin.get("nickname")));

		// 添加查詢條件：memberId
		query.where(cb.equal(followRoot.get("follower").get("id"), followerid));

		// 執行查詢
		return entityManager.createQuery(query).getResultList();
	}

	public List<MemberInfo> getFollowerList(Long followedId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MemberInfo> query = cb.createQuery(MemberInfo.class);

		// 根實體
		Root<FollowBean> followRoot = query.from(FollowBean.class);

		// 聯接 FollowBean 和 MemberBean
		Join<FollowBean, MemberBean> followerJoin = followRoot.join("follower", JoinType.INNER);

		// 選擇字段（id, name, profilePicture）
		query.select(cb.construct(MemberInfo.class, followerJoin.get("id"), followerJoin.get("name"),
				followerJoin.get("profilePicture"), followerJoin.get("nickname")));

		// 添加查詢條件：memberId
		query.where(cb.equal(followRoot.get("followed").get("id"), followedId));

		// 執行查詢
		return entityManager.createQuery(query).getResultList();
	}

	public Long followercount(Long userId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<FollowBean> root = query.from(FollowBean.class);

        // Select COUNT(*)
        query.select(cb.count(root));

        // WHERE followed_id = :userId AND followStatus = 'ACTIVE'
        Predicate condition = cb.and(
                cb.equal(root.get("followed").get("userid"), userId),
                cb.equal(root.get("followStatus"), "ACTIVE")
        );
        query.where(condition);

        return entityManager.createQuery(query).getSingleResult();

	}

	public Long followingcount(Long followingId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<FollowBean> root = query.from(FollowBean.class);

        // Select COUNT(*)
        query.select(cb.count(root));

        // WHERE followed_id = :userId AND followStatus = 'ACTIVE'
        Predicate condition = cb.and(
                cb.equal(root.get("follower").get("userid"), followingId),
                cb.equal(root.get("followStatus"), "ACTIVE")
        );
        query.where(condition);

        return entityManager.createQuery(query).getSingleResult();
	}


}
