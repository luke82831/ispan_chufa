package com.ispan.chufa.controller;

import java.util.List;

import org.json.JSONException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.FollowBean;
import com.ispan.chufa.dto.FollowRequest;
import com.ispan.chufa.dto.FollowResponse;
import com.ispan.chufa.dto.MemberInfo;
import com.ispan.chufa.service.FollowService;

@RestController
@RequestMapping("/follow")
public class FollowController {
	@Autowired
	private FollowService followService;

	@PostMapping("/verb")
	public FollowResponse follow(@RequestBody FollowRequest followRequest) {
		System.out.println("接收到的参数：" + followRequest);
		FollowResponse responseBean = new FollowResponse();
		try {
			// 請求服務層
			FollowBean follow = followService.follow(followRequest.getFollowerid(), followRequest.getFollowedid());
			if (followRequest.getFollowerid() == null || followRequest.getFollowedid() == null) {
				responseBean.setSuccess(false);
				responseBean.setMessage("請求體缺少必要欄位!!");
				return responseBean;
			}
			if (follow == null) {
				responseBean.setSuccess(false);
				responseBean.setMessage("取消關注");
			} else {
				responseBean.setSuccess(true);
				responseBean.setMessage("成功關注");
				responseBean.setFollowedId(follow.getFollowed().getUserid());
				responseBean.setFollowerId(follow.getFollower().getUserid());
				BeanUtils.copyProperties(follow, responseBean);
			}

		} catch (JSONException e) {
			responseBean.setSuccess(false);
			responseBean.setMessage("JSON解析錯誤: " + e.getMessage());
		} catch (Exception e) {
			responseBean.setSuccess(false);
			responseBean.setMessage("系統錯誤: " + e.getMessage());
		}
		return responseBean;
	}

	// 查詢用戶的關注列表（被關注者）
	@GetMapping("/followedList/{followerId}")
	public List<MemberInfo> getFollowedList(@PathVariable Long followerId) {
		return followService.getFollowedList(followerId);
	}

	// 查詢用戶的粉絲列表（關注者）
	@GetMapping("/followerList/{followedId}")
	public List<MemberInfo> getFollowerList(@PathVariable Long followedId) {
		return followService.getFollowerList(followedId);
	}

}
