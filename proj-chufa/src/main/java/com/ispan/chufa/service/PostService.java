package com.ispan.chufa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.Post;
import com.ispan.chufa.dto.InteractionDTO;
import com.ispan.chufa.dto.MemberInfo;
import com.ispan.chufa.dto.PostDTO;
import com.ispan.chufa.repository.InteractionRepository;
import com.ispan.chufa.repository.MemberRepository;
import com.ispan.chufa.repository.PostRepository;

@Service
//@Transactional
public class PostService {
	@Autowired
	PostRepository postRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	InteractionRepository interactionRepository;

	public List<PostDTO> findPostsByCriteria(String param) {
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

	public InteractionDTO performaction(String json) {
		JSONObject param = new JSONObject(json);
		InteractionDTO interactDTO = new InteractionDTO();
		InteractionBean interactionBean = new InteractionBean();

		Long userId = param.getLong("userid");
		String interactionType = param.getString("interactiontype");
		Long postid = param.getLong("postid");

		Optional<MemberBean> optionalMember = memberRepository.findById(userId);
		Optional<Post> optionalPost = postRepository.findById(postid);

		if (!optionalMember.isPresent() || !optionalPost.isPresent()) {
			return null;
		}
		// Member 和 Post 的資料
		MemberBean performer = optionalMember.get();
		Post postAction = optionalPost.get();
		// 查詢所有相關互動記錄
		List<InteractionBean> existActions = interactionRepository.findByMemberAndPost(performer, postAction);

		// 過濾出 LIKE 的互動
		InteractionBean likeAction = existActions.stream().filter(action -> "LIKE".equals(action.getInteractionType()))
				.findFirst().orElse(null);

		if ("LIKE".equals(interactionType)&& likeAction != null) {
			// 如果是點讚操作，檢查是否已有 LIKE 記錄
			// 如果已存在 LIKE，則刪除該互動並返回取消狀態
			if (likeAction != null) {
				interactionRepository.delete(likeAction);
				BeanUtils.copyProperties(likeAction, interactDTO);
				interactDTO.setStatus("取消點讚");
				return interactDTO;
			}
		}

		interactionBean.setInteractionTime(LocalDateTime.now());
		interactionBean.setInteractionType(interactionType);
		interactionBean.setMember(performer);
		interactionBean.setPost(postAction);
		interactionRepository.save(interactionBean);
		interactDTO.setStatus("成功 " + interactionType);
		BeanUtils.copyProperties(interactionBean, interactDTO);

		// 複製 Member 屬性到 MemberInfo
		MemberInfo performermemberDTO = new MemberInfo();
		if (interactionBean.getMember() != null) {
			BeanUtils.copyProperties(interactionBean.getMember(), performermemberDTO);
			interactDTO.setMember(performermemberDTO);
		}

		// 準備 PostDTO 並檢查 Post 是否有 Member
		PostDTO postDTO = new PostDTO();
		BeanUtils.copyProperties(postAction, postDTO);
		if (postAction.getMember() != null) {
			MemberInfo memberDTO = new MemberInfo();
			BeanUtils.copyProperties(postAction.getMember(), memberDTO);
			postDTO.setMember(memberDTO);
		}
		// postDTOList.add(postDTO);

		// 準備 InteractionDTO
		if (interactionBean.getPost() != null) {
			interactDTO.setPostdto(postDTO);
		}

		return interactDTO;
	}

}
