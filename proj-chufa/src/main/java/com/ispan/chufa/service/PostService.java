package com.ispan.chufa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.dto.InteractionDTO;
import com.ispan.chufa.dto.PostDTO;
import com.ispan.chufa.repository.MemberRepository;
import com.ispan.chufa.repository.PostRepository;

@Service
//@Transactional
public class PostService {
	@Autowired
	PostRepository postRepository;

	@Autowired
	MemberRepository memberRepository;

	public List<PostDTO> findPostsByCriteria(JSONObject param) {
		try {
			JSONObject blog = new JSONObject(param);
			return postRepository.findPostsByTitle(blog);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<InteractionDTO> interact(JSONObject param) {
		JSONObject action = new JSONObject(param);

		return null;

	}

	// 通用方法處理各種互動行為
	public boolean performInteraction(JSONObject param) {
		boolean result = false;
		JSONObject action = new JSONObject(param);
		if (param != null && !param.isNull("userid") && !param.isNull("interactiontype")) {
			Long userid = param.getLong("userid");
			String interacttype = param.getString("interactiontype");
			Optional<MemberBean> optional = memberRepository.findById(userid);
		    if(optional.isPresent()) {
		    	MemberBean perfomer= optional.get();
		    	InteractionBean interaction = new InteractionBean();
		    	interaction.setActionId(userid);
		    	interaction.setInteractionTime(LocalDateTime.now()); // 設定互動時間
		    	interaction.setInteractionType(interacttype);
		    	
		    	return true;
			}
		} else {
			System.out.println("request parameter!");
		}
		return result;
	}

	

}
