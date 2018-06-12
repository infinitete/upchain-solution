package com.fecred.cxpt.consumer.common;

public class Response {
    private int Code;
    private Object Data;

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public Object getData() {
        return Data;
    }

    public void setData(Object data) {
        Data = data;
    }
}
