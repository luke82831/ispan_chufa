package com.ispan.chufa.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/create")
    public String create(@RequestBody String json) {
        JSONObject responseJson = new JSONObject();
        CommentBean commentBean = new CommentBean();
        // 留言_留言ID 設定為自增
        // 留言_貼文id RequestBody獲取controller內設定
        // 留言_留言者id RequestBody獲取controller內設定_創建時間 set現在時間
        // 留言_更新時間 預設NULL
        // 留言_留言內文 RequestBody獲取controller內設定
        // 留言_上層留言id RequestBody獲取controller內設定
        commentBean = commentService.createComment(commentBean);
        responseJson = responseJson.put("CommentId", commentBean.getCommentId());
        return responseJson.toString(0);
    }
}
