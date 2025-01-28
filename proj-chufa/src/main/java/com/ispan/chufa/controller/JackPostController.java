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
import com.ispan.chufa.dto.Response;
import com.ispan.chufa.repository.MemberRepository;
import com.ispan.chufa.service.JackPostService;

@RestController
@RequestMapping("/post")
public class JackPostController {
    @Autowired
    private JackPostService postService;
    @Autowired
    private MemberRepository memberRepository;

    // 創建貼文
    // 測試 http://localhost:8080/post/create
    // 測試 RequestBody =>
    // {"postTitle":"標題","postContent":"內容","postLink":"超連結","userid":"1"}
    @PostMapping("/create")
    public Response create(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        Response response = new Response();
        PostBean bean = new PostBean();
        MemberBean memberBean;

        String postTitle;
        String postContent;
        String postLink;
        Long userid;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("postTitle")) {
                postTitle = requestJson.getString("postTitle");
                if (postTitle.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入文章標題");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postTitle");
                return response;
            }

            if (!requestJson.isNull("postContent")) {
                postContent = requestJson.getString("postContent");
                if (postContent.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入postContent");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postContent");
                return response;
            }

            if (!requestJson.isNull("postLink")) {
                postLink = requestJson.getString("postLink");
                if (postLink.length() == 0) {
                    postLink = null;
                }
            } else {
                postLink = null;
            }

            if (!requestJson.isNull("userid")) {
                try {
                    userid = requestJson.getLong("userid");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("userid請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入userid");
                return response;
            }
        }

        // 設定PostBean
        {
            // postTitle
            bean.setPostTitle(postTitle);
            // postContent
            bean.setPostContent(postContent);
            // postLink
            if (postLink != null) {
                bean.setPostLink(postLink);
            }
            // memberBean
            try {
                memberBean = memberRepository.findById(userid).get();
            } catch (NoSuchElementException e) {
                response.setSuccesss(false);
                response.setMessage("沒有這個userid");
                return response;
            }
            bean.setMember(memberBean);
        }

        // 創建貼文
        postService.createPost(bean);

        // 設定response
        {
            response.setSuccesss(true);
            response.setMessage("創建貼文成功");
            response.getList().add(bean);
        }

        return response;
    }

    // 刪除貼文
    // 測試 http://localhost:8080/post/delete
    // 測試 RequestBody => {"postid":"1"}
    @DeleteMapping("/delete")
    public Response delete(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        Response response = new Response();

        Long postid;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("postid")) {
                try {
                    postid = requestJson.getLong("postid");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("postid請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postid");
                return response;
            }
        }

        // 刪除貼文
        {
            if (postService.deletePost(postid)) {
                response.setSuccesss(true);
                response.setMessage("已刪除這筆貼文");
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆貼文");
            }
        }

        return response;
    }

    // 查詢貼文
    // 測試 http://localhost:8080/post/findById
    // 測試 RequestBody => {"postid":"1"}
    @GetMapping("/findById")
    public Response findById(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        Response response = new Response();
        PostBean bean;

        Long postid;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("postid")) {
                try {
                    postid = requestJson.getLong("postid");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("postid請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postid");
                return response;
            }
        }

        // 查詢貼文
        bean = postService.findById(postid);

        // 設定response
        {
            if (bean != null) {
                response.setSuccesss(true);
                response.setMessage("查尋成功");
                response.getList().add(bean);
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆貼文");
            }
        }

        return response;
    }

    // 更新貼文
    // 測試 http://localhost:8080/post/update
    // 測試 RequestBody => {"postid":"1","postTitle":"標題","postContent":"內容"}
    @PutMapping("/update")
    public Response update(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        Response response = new Response();
        PostBean bean;

        Long postid;
        String postTitle;
        String postContent;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("postid")) {
                try {
                    postid = requestJson.getLong("postid");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("postid請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postid");
                return response;
            }

            if (!requestJson.isNull("postTitle")) {
                postTitle = requestJson.getString("postTitle");
                if (postTitle.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入postTitle");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postTitle");
                return response;
            }

            if (!requestJson.isNull("postContent")) {
                postContent = requestJson.getString("postContent");
                if (postContent.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入postContent");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postContent");
                return response;
            }

        }

        // 更新貼文
        bean = postService.updatePost(postid, postTitle, postContent);

        // 設定response
        {
            if (bean != null) {
                response.setSuccesss(true);
                response.setMessage("更新成功");
                response.getList().add(bean);
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆ID");
            }
        }

        return response;
    }

}
