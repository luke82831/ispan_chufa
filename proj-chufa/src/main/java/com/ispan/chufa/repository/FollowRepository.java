package com.ispan.chufa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.FollowBean;
import com.ispan.chufa.domain.MemberBean;
@Repository
public interface FollowRepository extends JpaRepository<FollowBean, Long>{	
    // 檢查是否已經關注過
    public boolean existsByFollower_UseridAndFollowed_Userid(Long follower, Long followed);

    // 查詢某用戶的粉絲列表（關注者），根據被關注者ID來查詢
    public List<MemberBean> findFollower_UseridByFollowed_Userid(Long followedId);

    // 查詢某用戶的被關注者列表（關注列表），根據關注者ID來查詢
    public List<MemberBean> findFollowed_UseridByFollower_Userid(Long followerId);

	public Optional<FollowBean> findByFollower_UseridAndFollowed_Userid(Long followerId, Long followedId);
	
	

	

}
