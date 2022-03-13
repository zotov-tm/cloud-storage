package com.zotov.cloud.box.common;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;


public class FileReceiver {
    private enum State {
        IDLE, NAME_LENGTH, NAME, FILE_LENGTH, FILE;
    }


    private String rootDir;
    private State state = State.IDLE;
    private int filenameLength;
    private long fileLength;
    private long receivedFileLength;


    BufferedOutputStream out;

    public FileReceiver(String rootDir) {
        this.rootDir = rootDir;
    }

    public void startReceive() {
        state = FileReceiver.State.NAME_LENGTH;
        receivedFileLength = 0L;
        System.out.println("Состояние: Старт получения файла");
    }


    public void receive(ChannelHandlerContext ctx, ByteBuf buf, Runnable finishOperation) throws Exception {


        if (state == State.NAME_LENGTH) {
            if (buf.readableBytes() >= 4) {
                filenameLength = buf.readInt();
                System.out.println("Состояние: получена длинна имени файла");
                state = State.NAME;
            }

        }


        if (state == State.NAME) {
            if (buf.readableBytes() >= 4) {
                byte[] fileNameBytes = new byte[filenameLength];
                buf.readBytes(fileNameBytes);
                System.out.println("Состояние: получено имя файла");
                out = new BufferedOutputStream(new FileOutputStream(rootDir + "/" + new String(fileNameBytes)));
                state = State.FILE_LENGTH;
            }

        }
        if (state == State.FILE_LENGTH) {
            if (buf.readableBytes() >= 8) {
                fileLength = buf.readLong();
                System.out.println("Состояние: Получена длинна файла");
                state = State.FILE;
            }
        }

        if (state == State.FILE) {
            while (buf.readableBytes() > 0) {
                out.write(buf.readByte());
                receivedFileLength++;
                if (receivedFileLength == fileLength) {
                    state = State.IDLE;

                    System.out.println("Состояние: файл загружен");
                    out.close();
                    finishOperation.run();
                    return;
                }
            }
        }


    }
}
