package com.easemob.tokenserver;

import com.alibaba.fastjson.JSONObject;
import com.easemob.AgoraIO.RtcTokenGenerate;
import com.easemob.Base.*;
import com.easemob.HtttpClient.HttpClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * description:
 * author: lijian
 * date: 2020-01-19
 **/
@Component
@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final String TOKEN = "/token/rtcToken";
    private static final String FAVICON_ICO = "/favicon.ico";
    private static final String AGORA_APPID = "agoraAppId";
    private static final String APPCERT = "appCertificate";
    private static final String CHANNEL = "channelName";
    private static final String USERID = "userAccount";
    private static final String APPKEY = "easemobAppKey";
    private static final String ACCESSETOKEN = "accessToken";
    private static final String ERRORDESCRITION = "error_description";


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest  obj) throws Exception {

        ResponseParam responseParam = new ResponseParam();

        FullHttpRequest msg = (FullHttpRequest)obj;
        if(msg.method().equals(HttpMethod.GET)){
                HttpRequest request  = (HttpRequest)msg;
                String uri = request.getUri();
                System.out.println("-----------------------------------------------------------------");
                System.out.println("uri:"+uri + "Get uir");
                if(uri.equals(FAVICON_ICO)){
                    return;
                }
                String[] numberArray = uri.split("\\?");
                if(numberArray != null && numberArray[0].equals(TOKEN)) {
                    // 解析请求参数
                    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
                    Map<String, List<String>> params = queryStringDecoder.parameters();
                    if (!params.isEmpty()) {
                        for (Map.Entry<String, List<String>> p : params.entrySet()) {
                            String key = p.getKey();
                            List<String> vals = p.getValue();
                            String value = null;
                            for (String val : vals) {
                                System.out.println("PARAM: " + key + " = " + val + "\r\n");
                                value = val;
                            }
                            if (key.equals(APPKEY)) {
                                responseParam.setAppkey(value);
                            }else if (key.equals(CHANNEL)) {
                                responseParam.setChannel(value);
                            } else if (key.equals(USERID)) {
                                responseParam.setUserId(value);
                            }
                        }
                    }
                }else {
                    responseParam.setCode(ResCode.RES_METHOND_ERROR);
                    responseParam.setErrorInfo("request methond illegally");
                }
            }else if(msg.method().equals(HttpMethod.POST)){ //post请求
                   String uri = msg.getUri();
                   System.out.println("-----------------------------------------------------------------");
                   System.out.println("uri:"+uri + "Get uir");
                   if(uri.equals(FAVICON_ICO)){
                       return;
                   }
                   String authtoken = msg.headers().get(HttpHeaderNames.AUTHORIZATION);
                   String[] tokenArray = authtoken.split(" ");
                   if(tokenArray != null && tokenArray.length == 2){
                       String token = tokenArray[1];
                       responseParam.setAccess_token(token);
                   }

                   String[] numberArray = uri.split("\\?");
                   if(numberArray != null && numberArray[0].equals(TOKEN)) {
                         String strContentType = obj.headers().get("Content-Type").trim();
                         if(strContentType.contains("application/json")) {
                           try {
                               Map<String, Object>  params = getJSONParams(obj);
                               for (Object key : params.keySet()) {
                                   if(key.toString().equals(APPKEY)){
                                       responseParam.setAppkey(params.get(key).toString());
                                   }else if(key.toString().equals(CHANNEL)){
                                       responseParam.setChannel(params.get(key).toString());
                                   }else if(key.toString().equals(USERID)){
                                       responseParam.setUserId(params.get(key).toString());
                                   }
                               }
                           } catch (UnsupportedEncodingException e) {
                               e.getMessage();
                           }
                         }else{
                             responseParam.setCode(ResCode.RES_CONTENT_TYPE_ERROR);
                             responseParam.setErrorInfo("request  content type illegally");
                         }
                   }else{
                       responseParam.setCode(ResCode.RES_METHOND_ERROR);
                       responseParam.setErrorInfo("request methond illegally");
                   }
        }

        String[] numberArray = null;
        if(responseParam.getCode() == ResCode.RES_0K){
            if(responseParam.getAppkey()== null || responseParam.getAppkey().length() == 0){
                responseParam.setCode(ResCode.RES_PARME_ERROR);
                responseParam.setErrorInfo("easemob appId is null");
            }else if(responseParam.getChannel()== null || responseParam.getChannel().length() == 0){
                responseParam.setCode(ResCode.RES_PARME_ERROR);
                responseParam.setErrorInfo("channelName is null");
            }else if(responseParam.getUserId()== null ||responseParam.getUserId().length()== 0){
                responseParam.setCode(ResCode.RES_PARME_ERROR);
                responseParam.setErrorInfo("userAccount is null");
            }else if(responseParam.getAccess_token()== null || responseParam.getAccess_token().length() == 0){
                responseParam.setCode(ResCode.RES_PARME_ERROR);
                responseParam.setErrorInfo("accessToken is null");
            }else{
                numberArray = responseParam.getAppkey().split("#");
                if(numberArray.length != 2){
                    responseParam.setCode(ResCode.RES_PARME_ERROR);
                    responseParam.setErrorInfo("appkey is illegally");
                }
            }
        }

        if(responseParam.getCode() != ResCode.RES_0K){
            try {
                packageResponse(ctx,responseParam.getCode().code,responseParam.getErrorInfo(),responseParam.getToken());
            }catch (JsonProcessingException e){
                e.getMessage();
            }
        }else{
            String url = ApplicationConf.restServer;
            String org_name = numberArray[0];
            String app_name = numberArray[1];
            url += org_name;
            url += "/";
            url += app_name;
            url += "/users/";
            url += responseParam.getUserId();
            url += "/status";
            HttpClient client = new HttpClient(url,responseParam.getAccess_token());
            client.setCallback(new HttpResponseCallback() {
                @Override
                public void onResponseData(int resCode, String responseData) {
                    if(resCode == ResCode.RES_0K.code){
                        //校验用户信息成功
                        String token = RtcTokenGenerate.generateToken(ApplicationConf.agoraAppId,ApplicationConf.agoraCert,
                                responseParam.getChannel(),responseParam.getUserId());
                        responseParam.setToken(token);
                    }else {
                        try {
                            Map<String, Object> params = getUserInfo(responseData);
                            for (Object key : params.keySet()) {
                                if(key.toString().equals(ERRORDESCRITION)){
                                    responseParam.setErrorInfo(params.get(key).toString());
                                }
                            }
                        }catch(UnsupportedEncodingException e){
                                e.getMessage();
                        }
                    }
                    //发送Reponse
                    try {
                        packageResponse(ctx,resCode,responseParam.getErrorInfo(),responseParam.getToken());
                    }catch (JsonProcessingException e){
                        e.getMessage();
                    }
                }
            });
            try {
                client.sendRequest();
            }catch (Exception e){
                e.getMessage();
            }
        }
    }

    /**
     * 解析post参数信息
     * @param fullHttpRequest
     * @return
     * @throws UnsupportedEncodingException
     */
    private static Map<String, Object> getJSONParams(FullHttpRequest fullHttpRequest) throws UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<String, Object>();

        ByteBuf content = fullHttpRequest.content();
        byte[] reqContent = new byte[content.readableBytes()];
        content.readBytes(reqContent);
        String strContent = new String(reqContent, "UTF-8");

        JSONObject jsonParams = JSONObject.parseObject(strContent);
        for (Object key : jsonParams.keySet()) {
            params.put(key.toString(), jsonParams.get(key));
        }
        return params;
    }


    /**
     * 解析校验用户信息
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    private Map<String, Object> getUserInfo(String response) throws UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<String, Object>();
        JSONObject jsonParams = JSONObject.parseObject(response);
        for (Object key : jsonParams.keySet()) {
            params.put(key.toString(), jsonParams.get(key));
        }
        return params;
    }

    /**
     *返回用户Reponse 数据封装
     * @param ctx
     * @throws JsonProcessingException
     */
    private void packageResponse(ChannelHandlerContext ctx,int code,String errorInfo,String token)throws JsonProcessingException {
        Response responsebody = new Response();
        responsebody.setResCode(code);
        responsebody.setRtcToken(token);
        responsebody.setErrorInfo(errorInfo);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = mapper.writeValueAsString(responsebody);
        System.out.println(" response json: "+json);

        ByteBuf content = Unpooled.copiedBuffer(json, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json");
        System.out.println("conten byte NO. is "+response.content().readableBytes());
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        //将响应对象返回
        ctx.channel().writeAndFlush(response);
        ctx.close();
    }

}
