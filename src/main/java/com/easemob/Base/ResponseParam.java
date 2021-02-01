package com.easemob.Base;

/**
 * description:
 * author: lijian
 * date: 2021-01-25
 **/
public class ResponseParam {
    private String appkey = null;
    private String channel = null;
    private String userId = null;
    private String access_token = null;
    private String token = null;
    private ResCode code = ResCode.RES_0K;
    private String errorInfo = null;

    public ResponseParam(){
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ResCode getCode() {
        return code;
    }

    public void setCode(ResCode code) {
        this.code = code;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
