package com.ispan.chufa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.chufa.domain.PostBean;

public interface TimelineRepository extends JpaRepository<PostBean, Long> {
	 @Query(value = """
		        SELECT 
		            p.postid AS postId, 
		            p.postContent AS content, 
		            p.postLink AS link, 
		            p.postStatus AS status, 
		            p.postTime AS timestamp, 
		            p.postTitle AS title, 
		            p.userid AS authorId,
		            NULL AS interactionId, 
		            NULL AS interactionTime, 
		            'ORIGINAL' AS postType,
		            m.name AS authorName, 
		            m.nickname AS authorNickname
		        FROM chufa.dbo.post p
		        JOIN chufa.dbo.members m ON p.userid = m.userid
		        WHERE p.userid IN (
		            SELECT fl.followed_id 
		            FROM chufa.dbo.followlist fl
		            WHERE fl.follower_id = :followerId
		        )
		        
		        UNION ALL

		        SELECT 
		            p.postid AS postId, 
		            p.postContent AS content, 
		            p.postLink AS link, 
		            p.postStatus AS status, 
		            i.interaction_time AS timestamp, 
		            p.postTitle AS title, 
		            i.member_id AS authorId,
		            i.actionId AS interactionId, 
		            i.interaction_time AS interactionTime, 
		            'REPOST' AS postType,
		            m.name AS authorName, 
		            m.nickname AS authorNickname
		        FROM chufa.dbo.interaction i
		        JOIN chufa.dbo.post p ON i.postid = p.postid
		        JOIN chufa.dbo.members m ON i.member_id = m.userid
		        WHERE i.interction_type = 'REPOST'
		        AND i.member_id IN (
		            SELECT fl.followed_id 
		            FROM chufa.dbo.followlist fl
		            WHERE fl.follower_id = :followerId
		        )
		        
		        ORDER BY timestamp DESC
		    """, nativeQuery = true)
		    List<Object[]> findTimelineWithReposts(@Param("followerId") Long followerId);

}
