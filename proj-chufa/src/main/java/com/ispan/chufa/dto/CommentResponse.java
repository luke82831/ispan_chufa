package com.ispan.chufa.dto;

import java.util.List;

import com.ispan.chufa.domain.CommentBean;

public class CommentResponse {
    private Boolean successs;
    private String message;
    private CommentBean bean;
    private List<CommentBean> list;
    private Long count;

    @Override
    public String toString() {
        return "CommentResponse [successs=" + successs + ", message=" + message + ", bean=" + bean + ", list=" + list
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

    public CommentBean getBean() {
        return bean;
    }

    public void setBean(CommentBean bean) {
        this.bean = bean;
    }

    public List<CommentBean> getList() {
        return list;
    }

    public void setList(List<CommentBean> list) {
        this.list = list;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}
