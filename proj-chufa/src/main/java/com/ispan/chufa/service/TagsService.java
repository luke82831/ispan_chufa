package com.ispan.chufa.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.TagsBean;
import com.ispan.chufa.repository.TagsRepository;

@Service
public class TagsService {
    @Autowired
    private TagsRepository tagsRepository;

    // 創建標籤
    public TagsBean createTags(TagsBean bean) {
        if (bean != null && bean.getTagName() != "") {
            // tagId 標籤系統_標籤id 自動生成
            // tagState 標籤系統_標籤狀態 預設1
            bean.setTagState(1);
            // tagName 標籤系統_標籤名稱 RequestBody獲取
            // tagCreatedAt 標籤系統_創建時間 現在時間
            bean.setTagCreatedAt(LocalDateTime.now());
            // tagUpdatedAt 標籤系統_更新時間 null
        }
        return tagsRepository.save(bean);
    }
    // 刪除標籤
    // 用ID查詢標籤
    // 更新標籤
    // 查詢ID是否存在
}
