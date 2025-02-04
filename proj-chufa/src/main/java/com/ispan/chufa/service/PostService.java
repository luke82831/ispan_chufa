package com.ispan.chufa.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.dto.InteractionDTO;
import com.ispan.chufa.dto.MemberInfo;
import com.ispan.chufa.dto.PostDTO;
import com.ispan.chufa.repository.InteractionRepository;
import com.ispan.chufa.repository.MemberRepository;
import com.ispan.chufa.repository.PlaceRepository;
import com.ispan.chufa.repository.PostRepository;

@Service
// @Transactional
public class PostService {
	@Autowired
	PostRepository postRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	InteractionRepository interactionRepository;

	@Autowired
	private PlaceRepository placeRepository;

	// 創建一個 Post 並關聯多個 Place
	public PostBean createPostWithPlaces(PostBean post, Set<Long> placeIds) {
		Set<PlaceBean> places = new HashSet<>(placeRepository.findAllById(placeIds));
		post.setPlaces(places);
		return postRepository.save(post);
	}

	// 根據 ID 查詢 Post 和其相關聯的 Place
	public PostBean getPostById(Long id) {
		return postRepository.findById(id).orElse(null);
	}
	
	public List<PostBean> getAllPosts() {
        return postRepository.findAll();
    }

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
		String interactionType = param.getString("interactionType");
		Long postid = param.getLong("postid");

		Optional<MemberBean> optionalMember = memberRepository.findById(userId);
		Optional<PostBean> optionalPost = postRepository.findById(postid);

		if (optionalMember.isEmpty() || optionalPost.isEmpty()) {
			return null;
		}
		// Member 和 Post 的資料
		MemberBean performer = optionalMember.get();
		PostBean postAction = optionalPost.get();
		// 查詢所有相關互動記錄
		List<InteractionBean> existActions = interactionRepository.findByMemberAndPost(performer, postAction);

		// 過濾出 LIKE 的互動
		InteractionBean likeAction = existActions.stream().filter(action -> "LIKE".equals(action.getInteractionType()))
				.findFirst().orElse(null);

		if ("LIKE".equals(interactionType) && likeAction != null) {
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
