package com.zotov.cloud.box.client;

import com.zotov.cloud.box.common.Handler_proto_file;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class Network_proto_file {


    private static Network_proto_file ourInstance = new Network_proto_file();
    private Channel currentChannel;

    public static Network_proto_file getOurInstance() {
        return ourInstance;
    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }

    public Network_proto_file() {

    }

    public void start(CountDownLatch connectionOpened) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress("localhost", 8139)).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                currentChannel = socketChannel;
                socketChannel.pipeline().addLast(new Handler_proto_file("client-repository",new ClientCommandReceiver()));
            }
        });


        try {
            ChannelFuture f = b.connect().sync();
            connectionOpened.countDown();
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }


    }

    public void stop() {
        currentChannel.close();
    }


}
