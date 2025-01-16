package com.ispan.chufa.dto;

import java.util.List;

import com.ispan.chufa.domain.TagsBean;

public class TagsResponse {
    private Boolean successs;
    private String message;
    private TagsBean bean;
    private List<TagsBean> list;
    private Long count;

    @Override
    public String toString() {
        return "TagsResponse [successs=" + successs + ", message=" + message + ", bean=" + bean + ", list=" + list
                + ", count=" + count + "]";
    }

    public Boolean getSuccesss() {
        return successs;
    }

    public void setSuccesss(Boolean successs) {
        this.successs = successs;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TagsBean getBean() {
        return bean;
    }

    public void setBean(TagsBean bean) {
        this.bean = bean;
    }

    public List<TagsBean> getList() {
        return list;
    }

    public void setList(List<TagsBean> list) {
        this.list = list;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}
