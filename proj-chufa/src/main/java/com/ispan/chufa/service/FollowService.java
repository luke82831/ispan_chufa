package com.ispan.chufa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.FollowBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.repository.FollowRepository;
import com.ispan.chufa.repository.MemberRepository;
@Service
public class FollowService {

	@Autowired
	FollowRepository followRepository;
	
	@Autowired
	MemberRepository memberRepository;

	public FollowBean follow(Long followerId, Long followedId) {
		// TODO Auto-generated method stub		
		 // 確保 follower 和 followed 已存在
	    MemberBean follower = memberRepository.findById(followerId)
	        .orElseThrow(() -> new RuntimeException("Follower not found"));
	    MemberBean followed = memberRepository.findById(followedId)
	        .orElseThrow(() -> new RuntimeException("Followed member not found"));
	    
	    // 檢查是否已經存在該關注記錄
	    Optional<FollowBean> existingFollow = followRepository.findByFollower_UseridAndFollowed_Userid(followerId, followedId);
	              
		if (existingFollow.isPresent()){
			FollowBean followList = existingFollow.get();
			followRepository.deleteById(followList.getFlid());
			return null;
		}
		 // 建立新的 FollowList 物件
		else {
		FollowBean followList = new FollowBean();
	    followList.setFollower(follower);  // 設定關注者
	    followList.setFollowed(followed);  // 設定被關注者
		followList.setFollowTime(LocalDateTime.now()); // 設定關注時間
		followList.setFollowStatus("ACTIVE"); // 設定關注狀態為 "ACTIVE"
		return followRepository.save(followList); // 返回儲存的 FollowList
		}	
	}

	public List<MemberBean> getFollowedList(Long followerId) {
		// TODO Auto-generated method stub
		return followRepository.findFollowed_UseridByFollower_Userid(followerId);
	}

	public List<MemberBean> getFollowerList(Long followerId) {
		// TODO Auto-generated method stub
		return followRepository.findFollower_UseridByFollowed_Userid(followerId);
	}

	

}
