package com.zotov.cloud.box.common;

import com.zotov.cloud.box.common.CommandsList;
import com.zotov.cloud.box.common.SendFile_proto_file;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class CommandReceiver {


    private enum State {
        IDLE, COMMAND_LENGTH, COMMAND;
    }


    private State state = State.IDLE;

    private int commandLength;
    private int receivedCommandLength;
    private StringBuilder cmd;


    BufferedOutputStream out;

    public void startReceive() {
        state = State.COMMAND_LENGTH;
        cmd = new StringBuilder();
    }


    public void receive(ChannelHandlerContext ctx, ByteBuf buf,Runnable finishOperation) throws Exception {


        if (state == State.COMMAND_LENGTH) {

            if (buf.readableBytes() >= 4) {

                commandLength = buf.readInt();
                System.out.println("Состояние: получена длинна имени команды");
                state = State.COMMAND;
                receivedCommandLength = 0;
                cmd.setLength(0);
                System.out.println("333");
                System.out.println(commandLength);
            }

        }
        if (state == State.COMMAND) {

            while (buf.readableBytes() > 0) {


                cmd.append((char) buf.readByte());
                receivedCommandLength++;
                if (receivedCommandLength == commandLength) {
                    state = State.IDLE;
                    parsCommand(ctx, cmd.toString());
                    System.out.println("4444");
                    System.out.println(cmd.toString());
                    finishOperation.run();
                    return;
                }


            }

        }

    }

    public abstract void parsCommand(ChannelHandlerContext ctx, String cmd) throws Exception ;


}

