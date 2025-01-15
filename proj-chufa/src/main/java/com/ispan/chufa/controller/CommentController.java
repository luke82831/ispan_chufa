package com.ispan.chufa.controller;

import java.util.List;

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

import com.ispan.chufa.domain.CommentBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.dto.CommentResponse;
import com.ispan.chufa.repository.MemberRepository;
import com.ispan.chufa.repository.PostRepository;
import com.ispan.chufa.service.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    String commentId = "commentId";
    String postId = "postId";
    String commentState = "commentState";
    String userId = "userId";
    String commentCreatedAt = "commentCreatedAt";
    String commentUpdatedAt = "commentUpdatedAt";
    String content = "content";
    String parentId = "parentId";

    // 新增留言
    // 測試 http://localhost:8080/comment/create
    // 測試 RequestBody => {"postId":"1","userId":"1","content":"內容"}
    @PostMapping("/create")
    public CommentResponse create(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();
        CommentBean commentBean = new CommentBean();
        PostBean postBean = new PostBean();
        MemberBean memberBean = new MemberBean();
        // 留言_留言ID 設定為自增
        // 留言_貼文id RequestBody獲取
        if (requestJson.isNull(postId)) {
            response.setSuccesss(false);
            response.setMessage("請輸入postId");
            return response;
        } else {
            try {
                postBean = postRepository.findById(requestJson.getLong(postId)).get();
                commentBean.setPostBean(postBean);
            } catch (Exception e) {
                response.setSuccesss(false);
                response.setMessage("沒有這則貼文");
                return response;
            }
        }
        // 留言_留言者id RequestBody獲取
        if (requestJson.isNull(userId)) {
            response.setSuccesss(false);
            response.setMessage("請輸入userId");
            return response;
        } else {
            try {
                memberBean = memberRepository.findById(requestJson.getLong(userId)).get();
                commentBean.setMemberBean(memberBean);
            } catch (Exception e) {
                response.setSuccesss(false);
                response.setMessage("沒有這個使用者");
                return response;
            }
        }
        // 留言_留言內文 RequestBody獲取
        if (requestJson.isNull(content)) {
            response.setSuccesss(false);
            response.setMessage("請輸入content");
            return response;
        } else {
            commentBean.setContent(requestJson.getString(content));
        }
        // 留言_上層留言id RequestBody獲取 可以NULL
        if (!requestJson.isNull(parentId)) {
            CommentBean parentBean = commentService.findById(requestJson.getInt(parentId));
            // 要驗證資料庫是否有這筆上層留言ID
            if (parentBean != null) {
                // 要驗證上層留言和這留言是否在同一個貼文中
                if (parentBean.getPostBean().getPostid() == requestJson.getLong(postId)) {
                    commentBean.setParentId(requestJson.getLong(parentId));
                } else {
                    response.setSuccesss(false);
                    response.setMessage("上層留言不在這篇文章");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("上層留言不存在");
                return response;
            }
        }
        // 條件成立，存入資料庫
        CommentBean bean = commentService.createComment(commentBean);
        response.setSuccesss(true);
        response.setMessage("創建成功");
        response.setBean(bean);
        return response;
    }

    // 刪除留言
    // 測試 http://localhost:8080/comment/delete
    // 測試 RequestBody => {"commentId":"1"}
    @DeleteMapping("/delete")
    public CommentResponse delete(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();
        if (!requestJson.isNull(commentId)) {
            Integer id = requestJson.getInt(commentId);
            if (commentService.existsById(id)) {
                commentService.deleteComment(id);
                response.setSuccesss(true);
                response.setMessage("已刪除這筆留言");
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆留言");
            }
        } else {
            response.setSuccesss(false);
            response.setMessage("沒有傳入commentId");
        }
        return response;
    }

    // 查詢留言
    // 測試 http://localhost:8080/comment/findById
    // 測試 RequestBody => {"commentId":"1"}
    @GetMapping("/findById")
    public CommentResponse findById(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();
        if (!requestJson.isNull(commentId)) {
            try {
                CommentBean commentBean = commentService.findById(requestJson.getInt(commentId));
                if (commentBean != null) {
                    response.setSuccesss(true);
                    response.setMessage("查尋成功");
                    response.setBean(commentBean);
                } else {
                    response.setSuccesss(false);
                    response.setMessage("查不到這筆留言");
                }
            } catch (JSONException e) {
                response.setSuccesss(false);
                response.setMessage("ID請輸入整數");
            }
        } else {
            response.setSuccesss(false);
            response.setMessage("沒有傳入ID");
        }
        return response;
    }

    // 用上層查詢留言
    // 測試 http://localhost:8080/comment/findByParentId
    // 測試 RequestBody => {"parentId":"1"}
    @GetMapping("/findByParentId")
    public CommentResponse findByParentId(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();
        List<CommentBean> beans = commentService.findByParentId(requestJson.getLong(parentId));
        response.setSuccesss(true);
        response.setMessage("查到資料");
        response.setList(beans);
        System.out.println(beans.size() == 0);
        return response;
    }

    // 更新留言
    // 測試 http://localhost:8080/comment/update
    // 測試 RequestBody => {"commentId":"1","content":"內容"}
    @PutMapping("/update")
    public CommentResponse update(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();
        if (!requestJson.isNull(commentId)) {
            if (!requestJson.isNull(content)) {
                try {
                    CommentBean commentBean = commentService.updateComment(requestJson.getInt(commentId),
                            requestJson.getString(content));
                    if (commentBean != null) {
                        response.setSuccesss(true);
                        response.setMessage("更新成功");
                        response.setBean(commentBean);
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
            response.setMessage("沒有傳入ID");
        }
        return response;
    }

    // 更新留言狀態
    // 測試 http://localhost:8080/comment/updateState
    // 測試 RequestBody => {"commentId":"1","commentState":"私人"}
    @PutMapping("/updateState")
    public CommentResponse updateState(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();
        if (!requestJson.isNull(commentId)) {
            if (!requestJson.isNull(commentState)) {
                try {
                    CommentBean commentBean = commentService.updateCommentState(requestJson.getInt(commentId),
                            requestJson.getString(commentState));
                    if (commentBean != null) {
                        response.setSuccesss(true);
                        response.setMessage("留言狀態更新成功");
                        response.setBean(commentBean);
                    } else {
                        response.setSuccesss(false);
                        response.setMessage("查不到這筆留言");
                    }
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("commentId請輸入整數");
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("沒有傳入狀態");
            }
        } else {
            response.setSuccesss(false);
            response.setMessage("沒有傳入commentId");
        }
        return response;
    }

}
