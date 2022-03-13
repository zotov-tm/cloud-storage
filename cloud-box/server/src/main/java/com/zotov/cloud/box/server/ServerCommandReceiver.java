package com.zotov.cloud.box.server;

import com.zotov.cloud.box.common.CommandReceiver;
import com.zotov.cloud.box.common.SendFile_proto_file;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.nio.file.Paths;

public class ServerCommandReceiver extends CommandReceiver {
    @Override
    public void parsCommand(ChannelHandlerContext ctx, String cmd) throws IOException {
        if (cmd.startsWith("/request ")) {
            String fileToClientName = cmd.split("\\s")[1];
            SendFile_proto_file.sendFile(Paths.get("server-repository", fileToClientName), ctx.channel(), null);
        }
    }
}
