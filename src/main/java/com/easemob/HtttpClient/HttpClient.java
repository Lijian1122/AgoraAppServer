package com.easemob.HtttpClient;

import com.easemob.Base.HttpResponseCallback;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;


/**
 * description:
 * author: lijian
 * date: 2021-01-25
 **/

public class HttpClient {

    private String url;
    private String token;
    private HttpResponseCallback callback;
    private HttpClientInitializer initializer;
    public HttpClient(String url,String token){
        this.url = url;
        this.token = token;
    }

    public void setCallback(HttpResponseCallback callback){
       this.callback = callback;
    }

    public void sendRequest()throws Exception{
        URI uri = new URI(url);
        String host = uri.getHost();
        int port = 80;

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();

        initializer = new HttpClientInitializer();
        initializer.setCallBack(callback);
        b.group(group).channel(NioSocketChannel.class).handler(initializer);

        // Make the connection attempt.
        Channel ch = b.connect(host, port).sync().channel();

        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.AUTHORIZATION,"Bearer "+ token);
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");

        // Send the HTTP request.
        ch.writeAndFlush(request);

        // Wait for the server to close the connection.
        ch.closeFuture().sync();

        // Shut down executor threads to exit.
        group.shutdownGracefully();
    }
}