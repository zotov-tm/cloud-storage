package com.zotov.cloud.box.common;


import com.zotov.cloud.box.common.CommandReceiver;
import com.zotov.cloud.box.common.CommandsList;

import com.zotov.cloud.box.common.FileReceiver;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;





public class Handler_proto_file extends ChannelInboundHandlerAdapter {

    private enum State {
        IDLE, FILE, COMMAND;
    }

    private FileReceiver fileReceiver;
    private CommandReceiver commandReceiver;
    private State state;
    private Runnable finishOperation = ()->{
        state=State.IDLE;
        System.out.println("Операция завершена");
    };

    public Handler_proto_file(String rootDir, CommandReceiver commandReceiver) {
        this.fileReceiver = new FileReceiver(rootDir);
        this.commandReceiver = commandReceiver;
        this.state = State.IDLE;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент отключился");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        ByteBuf buf = (ByteBuf) msg;
        while (buf.readableBytes() >0) {
            if (state == State.IDLE) {
                byte reader = buf.readByte();
                if (reader == CommandsList.FILE_SIGNAL_BYTE) {
                    state = State.FILE;
                    fileReceiver.startReceive();
                } else if (reader == CommandsList.CMD_SIGNAL_BYTE) {
                    System.out.println("111");
                    state = State.COMMAND;
                    commandReceiver.startReceive();


                }
            }
            if (state == State.FILE) {
                fileReceiver.receive(ctx, buf,finishOperation);
            }
            if (state == State.COMMAND) {
                commandReceiver.receive(ctx, buf,finishOperation);
                System.out.println("222");
            }


        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
