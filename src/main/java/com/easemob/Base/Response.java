package com.easemob.Base;

/**
 * description:
 * author: lijian
 * date: 2021-01-19
 **/
public class Response {
    private  Integer  resCode;
    private  String rtcToken;
    private  String errorInfo;

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer  resCode) {
        this.resCode = resCode;
    }

    public String getRtcToken() {
        return rtcToken;
    }

    public void setRtcToken(String rtcToken) {
        this.rtcToken = rtcToken;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
