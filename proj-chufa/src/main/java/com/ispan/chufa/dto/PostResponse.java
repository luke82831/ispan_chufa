package com.ispan.chufa.dto;

import java.util.List;

import com.ispan.chufa.domain.PostBean;

public class PostResponse {
    private Boolean successs;
    private String message;
    private PostBean bean;
    private List<PostBean> list;
    private Long count;

    @Override
    public String toString() {
        return "PostResponse [successs=" + successs + ", message=" + message + ", bean=" + bean + ", list=" + list
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

    public PostBean getBean() {
        return bean;
    }

    public void setBean(PostBean bean) {
        this.bean = bean;
    }

    public List<PostBean> getList() {
        return list;
    }

    public void setList(List<PostBean> list) {
        this.list = list;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}
