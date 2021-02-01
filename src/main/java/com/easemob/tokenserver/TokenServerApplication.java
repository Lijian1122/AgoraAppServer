package com.easemob.tokenserver;

import org.springframework.boot.SpringApplication;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;


/**
 * ClassName: SpringBootApplication
 * description:
 * author: lijian
 * date: 2021-01-19 09:15
 **/
@org.springframework.boot.autoconfigure.SpringBootApplication//@EnableAutoConfiguration @ComponentScan

public class TokenServerApplication implements CommandLineRunner{


    public static void main(String[] args) {
          SpringApplication.run(TokenServerApplication.class, args);

    }


    @Autowired
    HttpServer nettyServer;

    @Override
    public void run(String... args) throws Exception {
        ChannelFuture start = nettyServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                nettyServer.destroy();
            }
        });
        start.channel().closeFuture().syncUninterruptibly();
    }
}
