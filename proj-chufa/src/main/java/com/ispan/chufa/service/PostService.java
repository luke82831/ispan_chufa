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

	public InteractionDTO insertaction(String json) {
		JSONObject param = new JSONObject(json);

		InteractionDTO interactDTO = new InteractionDTO();

		Long userId = param.getLong("userid");
		String interactionType = param.getString("interactiontype");
		Long postid = param.getLong("postid");

		Optional<MemberBean> optional = memberRepository.findById(userId);
		Optional<Post> post = postRepository.findById(postid);

		if (optional.isPresent() && post.isPresent()) {
			MemberBean perfomer = optional.get();
			Post postaction = post.get();

			InteractionBean bean = new InteractionBean();
			bean.setInteractionTime(LocalDateTime.now());
			bean.setInteractionType(interactionType);
			bean.setMember(perfomer);
			bean.setPost(postaction);
			interactionRepository.save(bean);
			Long postuserid = bean.getPost().getMember().getUserid();
			System.out.println(postuserid);

			// BeanUtils.copyProperties(bean, interactDTO);
			if (bean.getPost() != null) {
				PostDTO postDTO = new PostDTO();
				BeanUtils.copyProperties(postaction, postDTO);
				interactDTO.setPostdto(postDTO);
			}

			BeanUtils.copyProperties(bean, interactDTO);

			if (bean.getMember() != null) {
				MemberInfo memberDTO = new MemberInfo();
				// 複製 Member 屬性到 MemberInfo
				BeanUtils.copyProperties(bean.getMember(), memberDTO);
				interactDTO.setMember(memberDTO);
			}
		} else {
			return null;
		}
		return interactDTO;
	}

	public InteractionDTO performaction(String json) {
		JSONObject param = new JSONObject(json);
		InteractionDTO interactDTO = new InteractionDTO();

		Long userId = param.getLong("userid");
		String interactionType = param.getString("interactiontype");
		Long postid = param.getLong("postid");

		Optional<MemberBean> optionalMember = memberRepository.findById(userId);
		Optional<Post> optionalPost = postRepository.findById(postid);

		if (optionalMember.isPresent() && optionalPost.isPresent()) {
			// Member 和 Post 的資料
			MemberBean performer = optionalMember.get();
			Post postAction = optionalPost.get();

			// 處理 InteractionBean 並存入資料庫
			InteractionBean interactionBean = new InteractionBean();
			interactionBean.setInteractionTime(LocalDateTime.now());
			interactionBean.setInteractionType(interactionType);
			interactionBean.setMember(performer);
			interactionBean.setPost(postAction);
			interactionRepository.save(interactionBean);

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
			BeanUtils.copyProperties(interactionBean, interactDTO);

			// 顯示與存取的資料
			Long postUserId = interactionBean.getPost().getMember().getUserid();
			System.out.println(postUserId);
		} else {
			return null;
		}
		return interactDTO;
	}

}
