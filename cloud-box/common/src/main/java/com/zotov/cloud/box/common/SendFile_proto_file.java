package com.zotov.cloud.box.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SendFile_proto_file {

    public static void sendFile(Path path, Channel socketChannel, ChannelFutureListener finishListener) throws IOException {


        FileRegion fileRegion = new DefaultFileRegion(path.toFile(),0,Files.size(path));

        ByteBuf buf = null;
        buf = ByteBufAllocator.DEFAULT.directBuffer(1);
        buf.writeByte(CommandsList.FILE_SIGNAL_BYTE);
        socketChannel.write(buf);


        byte[] fileNameBytes = path.getFileName().toString().getBytes(StandardCharsets.UTF_8);
        buf = ByteBufAllocator.DEFAULT.directBuffer(4);
        buf.writeInt(fileNameBytes.length);
        socketChannel.write(buf);

        buf = ByteBufAllocator.DEFAULT.directBuffer(fileNameBytes.length);
        buf.writeBytes(fileNameBytes);
        socketChannel.write(buf);

        buf = ByteBufAllocator.DEFAULT.directBuffer(8);
        buf.writeLong(Files.size(path));
        socketChannel.write(buf);


        ChannelFuture channelFuture = socketChannel.writeAndFlush(fileRegion);


        if(finishListener!=null){
            channelFuture.addListener(finishListener);
        }



    }

}
