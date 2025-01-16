package com.ispan.chufa.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.TagsBean;
import com.ispan.chufa.dto.TagsResponse;
import com.ispan.chufa.service.TagsService;

@RestController
@RequestMapping("/Tags")
public class TagsController {
    @Autowired
    private TagsService tagsService;

    // 創建標籤
    // http://localhost:8080/Tags/create
    // RequestBody => {"tagName":"名稱"}
    @PostMapping("/create")
    public TagsResponse create(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        TagsResponse response = new TagsResponse();
        TagsBean tagsBean = new TagsBean();

        String tagName;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("tagName")) {
                tagName = requestJson.getString("tagName");
                if (tagName.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入tagName");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入tagName");
                return response;
            }
        }

        // 設定TagsBean
        tagsBean.setTagName(tagName);

        // 存入資料庫
        {
            try {
                TagsBean bean = tagsService.createTags(tagsBean);
                response.setSuccesss(true);
                response.setMessage("創建標籤成功");
                response.setBean(bean);
            } catch (DataIntegrityViolationException e) {
                response.setSuccesss(false);
                response.setMessage("已存在此標籤");
            }
        }
        return response;
    }

    // 刪除標籤
    // http://localhost:8080/Tags/delete
    // RequestBody => {"tagId":"1"}
    @DeleteMapping("/delete")
    public TagsResponse delete(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        TagsResponse response = new TagsResponse();

        Long tagId;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("tagId")) {
                try {
                    tagId = requestJson.getLong("tagId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("tagId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入tagId");
                return response;
            }
        }

        // 刪除標籤
        {
            if (tagsService.deleteTags(tagId)) {
                response.setSuccesss(true);
                response.setMessage("已刪除這筆標籤");
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆標籤");
            }
        }

        return response;
    }

    // 用tagId查詢標籤
    // 測試 http://localhost:8080/Tags/findById
    // RequestBody => {"tagId":"1"}
    @GetMapping("/findById")
    public TagsResponse findById(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        TagsResponse response = new TagsResponse();
        TagsBean bean = new TagsBean();

        Long tagId;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("tagId")) {
                try {
                    tagId = requestJson.getLong("tagId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("tagId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入tagId");
                return response;
            }
        }

        // 用tagId查詢標籤
        {
            bean = tagsService.findById(tagId);
            if (bean != null) {
                response.setSuccesss(true);
                response.setMessage("查詢標籤成功");
                response.setBean(bean);
            } else {
                response.setSuccesss(false);
                response.setMessage("查詢不到這筆標籤");
            }
        }

        return response;
    }

    // 更新標籤
    // 測試 http://localhost:8080/Tags/update
    // RequestBody => {"tagId":"1","tagName":"名稱"}
    @PutMapping("/update")
    public TagsResponse update(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        TagsResponse response = new TagsResponse();
        TagsBean bean = new TagsBean();

        Long tagId;
        String tagName;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("tagId")) {
                try {
                    tagId = requestJson.getLong("tagId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("tagId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入tagId");
                return response;
            }

            if (!requestJson.isNull("tagName")) {
                tagName = requestJson.getString("tagName");
                if (tagName.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入tagName");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入tagName");
                return response;
            }
        }

        // 更新標籤
        {
            try {
                bean = tagsService.updateTags(tagId, tagName);
            } catch (DataIntegrityViolationException e) {
                response.setSuccesss(false);
                response.setMessage("已有相同標籤");
                return response;
            }
        }

        // 設定response
        {
            if (bean != null) {
                response.setSuccesss(true);
                response.setMessage("更新標籤成功");
                response.setBean(bean);
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆ID");
            }
        }

        return response;
    }

    // 更新標籤狀態
    // 測試 http://localhost:8080/Tags/updateState
    // RequestBody => {"tagId":"1","tagState":"私人"}
    @PutMapping("/updateState")
    public TagsResponse updateState(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        TagsResponse response = new TagsResponse();
        TagsBean bean = new TagsBean();

        Long tagId;
        String tagState;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("tagId")) {
                try {
                    tagId = requestJson.getLong("tagId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("tagId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入tagId");
                return response;
            }

            if (!requestJson.isNull("tagState")) {
                tagState = requestJson.getString("tagState");
                if (tagState.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入tagState");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入tagState");
                return response;
            }
        }

        // 更新標籤狀態
        bean = tagsService.updateTagsState(tagId, tagState);

        // 設定response
        {
            if (bean != null) {
                response.setSuccesss(true);
                response.setMessage("更新標籤成功");
                response.setBean(bean);
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆ID");
            }
        }

        return response;
    }

}
