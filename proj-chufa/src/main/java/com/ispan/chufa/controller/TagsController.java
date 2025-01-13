package com.ispan.chufa.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.TagsBean;
import com.ispan.chufa.service.TagsService;

@RestController
@RequestMapping("/Tags")
public class TagsController {
    @Autowired
    private TagsService tagsService;

    String tagId = "tagId";
    String tagState = "tagState";
    String tagName = "tagName";
    String tagCreatedAt = "tagCreatedAt";
    String tagUpdatedAt = "tagUpdatedAt";

    // 創建標籤
    @PostMapping("/create")
    public String create(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        JSONObject responseJson = new JSONObject();
        TagsBean tagsBean = new TagsBean();
        if (!requestJson.isNull(tagName)) {
            tagsBean.setTagName(requestJson.getString(tagName));
            try {
                tagsService.createTags(tagsBean);
                responseJson.put(tagId, tagsBean.getTagId())
                        .put(tagState, tagsBean.getTagState())
                        .put(tagName, tagsBean.getTagName())
                        .put(tagCreatedAt, tagsBean.getTagCreatedAt())
                        .put(tagUpdatedAt, tagsBean.getTagUpdatedAt());
            } catch (Exception e) {
                responseJson.put("錯誤", "已存在此標籤");
            }
        }
        return responseJson.toString();
    }
    // 刪除標籤
    // 用ID查詢標籤
    // 更新標籤
    // 查詢ID是否存在
}
