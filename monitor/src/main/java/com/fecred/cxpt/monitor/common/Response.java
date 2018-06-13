package com.fecred.cxpt.monitor.common;

public class Response {
    protected int Code;
    protected Object Data;

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
