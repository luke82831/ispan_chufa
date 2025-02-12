package com.ispan.chufa.service;


import java.sql.Timestamp;
import java.time.LocalDateTime;
//import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.dto.TimelinePostDto;
import com.ispan.chufa.repository.InteractionRepository;
import com.ispan.chufa.repository.TimelineRepository;

@Service
public class TimelineService {
	@Autowired
	private TimelineRepository timelineRepository;


	@Autowired
	private InteractionRepository interactionRepository;
	   public List<TimelinePostDto> getPostsByFollowerId(Long followerId) {
		   List<Object[]> results = timelineRepository.findTimelineWithReposts(followerId);
	        return results.stream().map(this::mapToPostDTO).collect(Collectors.toList());
	    }

	    private TimelinePostDto mapToPostDTO(Object[] result) {
	        return new TimelinePostDto(
	        		    (Long) result[0],  // postId
	        	        (String) result[1],                   // content
	        	        (String) result[2],                   // link
	        	        (String) result[3],                   // status
	        	        convertToLocalDateTime(result[4]),    // timestamp
	        	        (String) result[5],                   // title
	        	        (Long) result[6], // authorId
	        	        result[7] != null ? (Long) result[7] : null, // interactionId
	        	        convertToLocalDateTime(result[8]),    // interactionTime
	        	        (String) result[9],                   // postType
	        	        (String) result[10],                  // authorName
	        	        (String) result[11]                   // authorNickname
	        );
	    }
	    private LocalDateTime convertToLocalDateTime(Object timestamp) {
	        return timestamp != null ? ((Timestamp) timestamp).toLocalDateTime() : null;
	    }
}
