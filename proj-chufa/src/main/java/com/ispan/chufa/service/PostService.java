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
import com.ispan.chufa.dto.MemberDTO;
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
	
	 // 根據 userid 查詢會員資料並轉換成 MemberDTO
    public MemberDTO getMemberByUserid(Long userid) {
        MemberBean member = memberRepository.findByUserid(userid);
        
        // 檢查是否找到會員資料
        if (member != null) {
            // 轉換 Member 到 MemberDTO，只返回需要的字段
            return new MemberDTO(
                member.getUserid(),
                member.getUsername(),
                member.getPhoneNumber(),
                member.getEmail(),
                member.getName(),
                member.getGender(),
                member.getNickname(),
                member.getProfilePicture(),
                member.getBio(),
                member.getBirth()
            );
        } else {
            return null; // 或者返回適當的錯誤處理
        }
    }

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

//	public Optional<PostBean> getPostdetailById(Long id) {
//	        return postRepository.findById(id);
//	}

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

	public PostDTO forwardPost(String json) {
		JSONObject param = new JSONObject(json);
		Long userId = param.getLong("userid");
		Long postid = param.getLong("postid");
		PostBean originalPost = postRepository.findById(postid)
				.orElseThrow(() -> new RuntimeException("Post not found"));
		MemberBean member = memberRepository.findById(userId)
		         .orElseThrow(() -> new RuntimeException("Member not found"));
		// 判斷是否為最初的貼文
		if (originalPost.getForwardedFrom() != null) {
		    originalPost = getOriginalPost(originalPost); // 找到最初的原創貼文
		}
		// 創建新的轉發貼文
		PostBean forwardedPost = new PostBean();
		forwardedPost.setPostContent("repost: " + originalPost.getPostTitle()); // 轉發內容
		forwardedPost.setPostTime(LocalDateTime.now());
		forwardedPost.setForwardedFrom(originalPost); // 設置原始貼文為轉發來源
		forwardedPost.setMember(member);

		PostBean savedPost= postRepository.save(forwardedPost); // 保存轉發貼文
		
		PostDTO postDTO = new PostDTO();
		BeanUtils.copyProperties(savedPost, postDTO);
		//postDTO.setPostid(savedPost.getPostid());
		postDTO.setForwardedFrom(postid);
		
		// 將儲存的貼文轉換為 DTO
	    // 填充 MemberInfo
		PostDTO originalpostdto=new PostDTO();
		BeanUtils.copyProperties(originalPost, originalpostdto);
		MemberInfo originmember = new MemberInfo();
		BeanUtils.copyProperties(originalPost.getMember(), originmember);
		originalpostdto.setMember(originmember);
		postDTO.setRepostDTO(originalpostdto);

	    MemberInfo memberInfo = new MemberInfo();
	    BeanUtils.copyProperties(member, memberInfo);
	    postDTO.setMember(memberInfo);
	    
	    postDTO.setRepost(true);

	    return postDTO;
	}
	
	private PostBean getOriginalPost(PostBean post) {
	    while (post.getForwardedFrom() != null) {
	        post = post.getForwardedFrom();
	    }
	    return post;
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
//		InteractionBean likeAction = existActions.stream().filter(action -> "LIKE".equals(action.getInteractionType()))
//				.findFirst().orElse(null);
		InteractionBean likeAction = existActions.stream()
		        .filter(action -> interactionType.equals(action.getInteractionType()))
		        .findFirst()
		        .orElse(null);

		if ("LIKE".equals(interactionType) || "COLLECT".equals(interactionType)&& likeAction != null) {
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
