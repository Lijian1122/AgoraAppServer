package com.easemob.HtttpClient;

import com.easemob.Base.HttpResponseCallback;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import java.nio.charset.Charset;


/**
 * description:
 * author: lijian
 * date: 2021-01-25
 **/
public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {


    private HttpResponseCallback callback;

    public void setCallback(HttpResponseCallback callback) {
        this.callback = callback;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
        if (!response.headers().isEmpty()) {
            for (CharSequence name : response.headers().names()) {
                for (CharSequence value : response.headers().getAll(name)) {
                    System.err.println("HEADER: " + name + " = " + value);
                }
            }
            System.err.println();
        }

        String data = response.content().toString(Charset.forName("utf-8"));
        if(callback != null){
            callback.onResponseData(response.getStatus().code(),data);
        }
    }
}
