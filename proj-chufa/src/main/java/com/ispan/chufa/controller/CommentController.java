package com.ispan.chufa.controller;

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
import com.ispan.chufa.service.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    String commentId = "commentId";
    String postId = "postId";
    String commentState = "commentState";
    String userId = "userId";
    String commentCreatedAt = "commentCreatedAt";
    String commentUpdatedAt = "commentUpdatedAt";
    String content = "content";
    String parentId = "parentId";

    // 新增留言
    @PostMapping("/create")
    public String create(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        JSONObject responseJson = new JSONObject();
        CommentBean commentBean = new CommentBean();
        int i = 0;
        // 留言_留言ID 設定為自增
        // 留言_貼文id RequestBody獲取
        if (requestJson.isNull(postId)) {
            responseJson.put(postId, "請輸入");
            i++;
        } else {
            commentBean.setPostId(requestJson.getInt(postId));
        }
        // 留言_留言者id RequestBody獲取
        if (requestJson.isNull(userId)) {
            responseJson.put(userId, "請輸入");
            i++;
        } else {
            commentBean.setUserId(requestJson.getInt(userId));
        }
        // 留言_留言內文 RequestBody獲取
        if (requestJson.isNull(content)) {
            responseJson.put(content, "請輸入");
            i++;
        } else {
            commentBean.setContent(requestJson.getString(content));
        }
        // 留言_上層留言id RequestBody獲取 可以NULL
        if (!requestJson.isNull(parentId)) {
            CommentBean parentBean = commentService.findById(requestJson.getInt(parentId));
            // 要驗證資料庫是否有這筆上層留言ID
            if (parentBean != null) {
                // 要驗證上層留言和這留言是否在同一個貼文中
                if (parentBean.getPostId() == requestJson.getInt(postId)) {
                    commentBean.setParentId(requestJson.getInt(parentId));
                } else {
                    responseJson.put(parentId, "上層留言不在這篇文章");
                    i++;
                }
            } else {
                responseJson.put(parentId, "上層留言不存在");
                i++;
            }
        }
        // 條件成立，存入資料庫
        if (i == 0) {
            commentService.createComment(commentBean);
            responseJson.put(commentId, commentBean.getCommentId())
                    .put(postId, commentBean.getPostId())
                    .put(commentState, commentBean.getCommentState())
                    .put(userId, commentBean.getUserId())
                    .put(commentCreatedAt, commentBean.getCommentCreatedAt())
                    .put(commentUpdatedAt, commentBean.getCommentUpdatedAt())
                    .put(content, commentBean.getContent())
                    .put(parentId, commentBean.getParentId());
        }
        return responseJson.toString(0);
    }

    // 刪除留言
    @DeleteMapping("/delete")
    public String delete(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        JSONObject responseJson = new JSONObject();
        if (!requestJson.isNull(commentId)) {
            Integer id = requestJson.getInt(commentId);
            if (commentService.existsById(id)) {
                commentService.deleteComment(id);
                responseJson.put("成功", "已刪除這筆資料");
            } else {
                responseJson.put("錯誤", "查不到這筆ID");
            }
        } else {
            responseJson.put("錯誤", "沒有傳入ID");
        }
        return responseJson.toString();
    }

    // 查詢留言
    @GetMapping("/findById")
    public String findById(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        JSONObject responseJson = new JSONObject();
        if (!requestJson.isNull(commentId)) {
            CommentBean commentBean = commentService.findById(requestJson.getInt(commentId));
            if (commentBean != null) {
                responseJson.put(commentId, commentBean.getCommentId())
                        .put(postId, commentBean.getPostId())
                        .put(commentState, commentBean.getCommentState())
                        .put(userId, commentBean.getUserId())
                        .put(commentCreatedAt, commentBean.getCommentCreatedAt())
                        .put(commentUpdatedAt, commentBean.getCommentUpdatedAt())
                        .put(content, commentBean.getContent())
                        .put(parentId, commentBean.getParentId());
            } else {
                responseJson.put("錯誤", "查不到這筆ID");
            }
        } else {
            responseJson.put("錯誤", "沒有傳入ID");
        }
        return responseJson.toString();
    }

    // 更新留言
    @PutMapping("/update")
    public String update(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        JSONObject responseJson = new JSONObject();
        if (!requestJson.isNull(commentId)) {
            if (!requestJson.isNull(content)) {
                CommentBean commentBean = commentService.updateComment(requestJson.getInt(commentId),
                        requestJson.getString(content));
                if (commentBean != null) {
                    responseJson.put(commentId, commentBean.getCommentId())
                            .put(postId, commentBean.getPostId())
                            .put(commentState, commentBean.getCommentState())
                            .put(userId, commentBean.getUserId())
                            .put(commentCreatedAt, commentBean.getCommentCreatedAt())
                            .put(commentUpdatedAt, commentBean.getCommentUpdatedAt())
                            .put(content, commentBean.getContent())
                            .put(parentId, commentBean.getParentId());
                } else {
                    responseJson.put("錯誤", "查不到這筆ID");
                }
            } else {
                responseJson.put("錯誤", "沒有傳入內容");
            }
        } else {
            responseJson.put("錯誤", "沒有傳入ID");
        }
        return responseJson.toString();
    }
}
