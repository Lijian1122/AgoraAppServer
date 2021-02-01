package com.easemob.tokenserver;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import java.util.concurrent.ThreadFactory;

/**
 * description: 通道初始化，主要用于设置各种Handler
 * author: lijian
 * date: 2020-01-19
 **/
@Component
@ChannelHandler.Sharable //@Sharable 注解用来说明ChannelHandler是否可以在多个channel直接共享使用
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    ServerChannelHandler serverChannelHandler;

    /*线程数量不宜设置过高,线程切换非常耗时*/
    public static final EventExecutorGroup exec = new DefaultEventExecutorGroup( Runtime.getRuntime().availableProcessors()*2,
            (ThreadFactory) r -> {
                Thread thread = new Thread(r);
                thread.setName("custom-tcp-exec-"+r.hashCode());
                return thread;
            },
            100000,
            RejectedExecutionHandlers.reject()
    );

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //编码解码
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(2147483647));
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("deflater", new HttpContentCompressor());
        //pipeline.addLast("handler", new ServerChannelHandler());
        pipeline.addLast(exec, "handler", new ServerChannelHandler());
    }
}