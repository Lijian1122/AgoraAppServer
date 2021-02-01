package com.easemob.HtttpClient;

import com.easemob.Base.HttpResponseCallback;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;


/**
 * description:
 * author: lijian
 * date: 2021-01-25
 **/
class HttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private HttpResponseCallback callback;

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpClientCodec());

        // Remove the following line if you don't want automatic content
        // decompression.
        p.addLast(new HttpContentDecompressor());//这里要添加解压，不然打印时会乱码
        p.addLast(new HttpObjectAggregator(2147483647));//添加HttpObjectAggregator， HttpClientMsgHandler才会收到FullHttpResponse
        HttpClientHandler handler = new HttpClientHandler();
        handler.setCallback(callback);
        p.addLast(handler);
    }

    public void setCallBack(HttpResponseCallback callback){
        this.callback = callback;
    }
}
