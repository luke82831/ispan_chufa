package com.ispan.chufa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.PostBean;

@Repository
public interface InteractionRepository extends JpaRepository<InteractionBean, Long> {

	public long countByPost_PostidAndInteractionType(Long postid, String interactionType);

	public List<InteractionBean> findByMemberAndPost(MemberBean memberbean, PostBean postbean);

}
