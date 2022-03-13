package com.zotov.cloud.box.client;

import com.zotov.cloud.box.common.CommandReceiver;
import io.netty.channel.ChannelHandlerContext;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;

public class ClientCommandReceiver extends CommandReceiver {
    @Override
    public void parsCommand(ChannelHandlerContext ctx, String cmd) throws Exception {
        throw new OperationNotSupportedException("Мы не должны сюда попадать на клиенте ");
    }
}
