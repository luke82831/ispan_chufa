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
    @DeleteMapping("/delete")
    public String delete(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        JSONObject responseJson = new JSONObject();
        if (!requestJson.isNull(tagId)) {
            if (tagsService.deleteTags(requestJson.getInt(tagId))) {
                responseJson.put("成功", "已刪除這筆標籤");
            } else {
                responseJson.put("錯誤", "查不到這筆ID");
            }
        } else {
            responseJson.put("錯誤", "沒有傳入ID");
        }
        return responseJson.toString();
    }

    // 用ID查詢標籤
    @GetMapping("/findById")
    public String findById(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        JSONObject responseJson = new JSONObject();
        if (!requestJson.isNull(tagId)) {
            TagsBean tagsBean = tagsService.findById(requestJson.getInt(tagId));
            if (tagsBean != null) {
                responseJson.put(tagId, tagsBean.getTagId())
                        .put(tagState, tagsBean.getTagState())
                        .put(tagName, tagsBean.getTagName())
                        .put(tagCreatedAt, tagsBean.getTagCreatedAt())
                        .put(tagUpdatedAt, tagsBean.getTagUpdatedAt());
            } else {
                responseJson.put("錯誤", "查不到這筆ID");
            }
        } else {
            responseJson.put("錯誤", "沒有傳入ID");
        }
        return responseJson.toString();
    }

    // 更新標籤
    @PutMapping("/update")
    public String update(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        JSONObject responseJson = new JSONObject();
        if (!requestJson.isNull(tagId)) {
            if (!requestJson.isNull(tagName)) {
                try {
                    TagsBean tagsBean = tagsService.updateTags(requestJson.getInt(tagId),
                            requestJson.getString(tagName));
                    if (tagsBean != null) {
                        responseJson.put(tagId, tagsBean.getTagId())
                                .put(tagState, tagsBean.getTagState())
                                .put(tagName, tagsBean.getTagName())
                                .put(tagCreatedAt, tagsBean.getTagCreatedAt())
                                .put(tagUpdatedAt, tagsBean.getTagUpdatedAt());
                    } else {
                        responseJson.put("錯誤", "查不到這筆ID");
                    }
                } catch (Exception e) {
                    responseJson.put("錯誤", "已存在此標籤");
                }
            } else {
                responseJson.put("錯誤", "沒有傳入標籤名稱");
            }
        } else {
            responseJson.put("錯誤", "沒有傳入ID");
        }
        return responseJson.toString();
    }
}
