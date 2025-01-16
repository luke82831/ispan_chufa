package com.ispan.chufa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.Post;
@Repository
public interface InteractionRepository extends JpaRepository<InteractionBean, Long> {

	//public long countByPostIdAndInteractionType(Long postId, String interactionType);
	public long countByPost_PostidAndInteractionType(Long postid, String interactionType);

	public List<InteractionBean> findByMemberAndPost(MemberBean performer, Post postAction);

}
