package com.ispan.chufa.dto;

import java.util.ArrayList;
import java.util.List;

public class CommentResponse {
    private Boolean successs;
    private String message;
    private CommentDTO bean;
    private List<CommentDTO> list = new ArrayList<>();
    private Long count;

    @Override
    public String toString() {
        return "Response [successs=" + successs + ", message=" + message + ", bean=" + bean + ", list=" + list
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

    public CommentDTO getBean() {
        return bean;
    }

    public void setBean(CommentDTO bean) {
        this.bean = bean;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<CommentDTO> getList() {
        return list;
    }

    public void setList(List<CommentDTO> list) {
        this.list = list;
    }

}
