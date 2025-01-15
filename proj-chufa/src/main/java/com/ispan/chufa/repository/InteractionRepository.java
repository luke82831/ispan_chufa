package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.InteractionBean;
@Repository
public interface InteractionRepository extends JpaRepository<InteractionBean, Long> {

	//public long countByPostIdAndInteractionType(Long postId, String interactionType);
	public long countByPost_PostidAndInteractionType(Long postid, String interactionType);

}
