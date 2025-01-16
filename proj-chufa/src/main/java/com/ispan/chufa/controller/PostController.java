package com.ispan.chufa.controller;

import java.util.NoSuchElementException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.dto.PostResponse;
import com.ispan.chufa.repository.MemberRepository;
import com.ispan.chufa.service.PostService;

@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private MemberRepository memberRepository;

    // 創建貼文
    // 測試 http://localhost:8080/post/create
    // 測試 RequestBody =>
    // {"postTitle":"標題","postContent":"內容","postLink":"超連結","userid":"1"}
    @PostMapping("/create")
    public PostResponse create(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        PostResponse response = new PostResponse();
        PostBean bean = new PostBean();
        if (!requestJson.isNull("postTitle") && requestJson.getString("postTitle").length() != 0) {
            bean.setPostTitle(requestJson.getString("postTitle"));
        } else {
            response.setSuccesss(false);
            response.setMessage("請輸入標題");
            return response;
        }

        if (!requestJson.isNull("postContent") && requestJson.getString("postContent").length() != 0) {
            bean.setPostContent(requestJson.getString("postContent"));
        } else {
            response.setSuccesss(false);
            response.setMessage("請輸入內容");
            return response;
        }

        if (!requestJson.isNull("postLink")) {
            bean.setPostLink(requestJson.getString("postLink"));
        }

        if (!requestJson.isNull("userid")) {
            MemberBean memberBean;
            try {
                memberBean = memberRepository.findById(requestJson.getLong("userid")).get();
            } catch (JSONException e) {
                response.setSuccesss(false);
                response.setMessage("userid請輸入整數");
                return response;
            } catch (NoSuchElementException e) {
                response.setSuccesss(false);
                response.setMessage("沒有這個成員");
                return response;
            }
            System.err.println("=============================");
            System.err.println("=============================");
            System.err.println("=============================");
            System.err.println("=============================");
            System.err.println("=============================");
            bean.setMemberBean(memberBean);
            postService.createPost(bean);
            response.setSuccesss(true);
            response.setMessage("創建貼文成功");
            response.setBean(bean);
        } else {
            response.setSuccesss(false);
            response.setMessage("請輸入userid");
            return response;
        }

        return response;
    }

    // 刪除貼文
    // 測試 http://localhost:8080/post/delete
    // 測試 RequestBody => {"postid":"1"}
    @DeleteMapping("/delete")
    public PostResponse delete(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        PostResponse response = new PostResponse();
        if (!requestJson.isNull("postid")) {
            Long id;
            try {
                id = requestJson.getLong("postid");
            } catch (JSONException e) {
                response.setSuccesss(false);
                response.setMessage("postid請輸入整數");
                return response;
            }
            if (postService.existsById(id)) {
                postService.deletePost(id);
                response.setSuccesss(true);
                response.setMessage("已刪除這筆貼文");
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆貼文");
            }
        } else {
            response.setSuccesss(false);
            response.setMessage("沒有傳入postid");
        }
        return response;
    }

    // 查詢貼文
    // 測試 http://localhost:8080/post/findById
    // 測試 RequestBody => {"postid":"1"}
    @GetMapping("/findById")
    public PostResponse findById(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        PostResponse response = new PostResponse();
        if (!requestJson.isNull("postid")) {
            try {
                PostBean bean = postService.findById(requestJson.getLong("postid"));
                if (bean != null) {
                    response.setSuccesss(true);
                    response.setMessage("查尋成功");
                    response.setBean(bean);
                } else {
                    response.setSuccesss(false);
                    response.setMessage("查不到這筆貼文");
                }
            } catch (JSONException e) {
                response.setSuccesss(false);
                response.setMessage("postid請輸入整數");
            }
        } else {
            response.setSuccesss(false);
            response.setMessage("沒有傳入postid");
        }
        return response;
    }

    // 更新留言
    // 測試 http://localhost:8080/post/update
    // 測試 RequestBody => {"postid":"1","postTitle":"標題","postContent":"內容"}
    @PutMapping("/update")
    public PostResponse update(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        PostResponse response = new PostResponse();
        if (!requestJson.isNull("postid")) {
            if (!requestJson.isNull("postTitle") && requestJson.getString("postTitle").length() != 0) {
                if (!requestJson.isNull("postContent") && requestJson.getString("postContent").length() != 0) {
                    try {
                        PostBean bean = postService.updatePost(requestJson.getLong("postid"),
                                requestJson.getString("postTitle"), requestJson.getString("postContent"));
                        if (bean != null) {
                            response.setSuccesss(true);
                            response.setMessage("更新成功");
                            response.setBean(bean);
                        } else {
                            response.setSuccesss(false);
                            response.setMessage("查不到這筆ID");
                        }
                    } catch (JSONException e) {
                        response.setSuccesss(false);
                        response.setMessage("ID請輸入整數");
                    }
                } else {
                    response.setSuccesss(false);
                    response.setMessage("沒有傳入內容");
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("沒有傳入標題");
            }
        } else {
            response.setSuccesss(false);
            response.setMessage("沒有傳入postid");
        }
        return response;
    }

}
