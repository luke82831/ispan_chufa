package com.ispan.chufa.dto;

import java.util.ArrayList;
import java.util.List;

public class Response {
    private Boolean successs;
    private String message;
    private List<Object> list = new ArrayList<>();
    private Long count;

    @Override
    public String toString() {
        return "Response [successs=" + successs + ", message=" + message + ", list=" + list + ", count=" + count + "]";
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

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}
