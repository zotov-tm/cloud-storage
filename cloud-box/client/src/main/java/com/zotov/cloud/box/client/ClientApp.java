package com.zotov.cloud.box.client;

import com.zotov.cloud.box.common.CommandsList;
import com.zotov.cloud.box.common.SendFile_proto_file;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ClientApp {
    public static void main(String[] args) throws InterruptedException, IOException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        new Thread(() -> Network_proto_file.getOurInstance().start(countDownLatch)).start();

        countDownLatch.await();


        Scanner sc = new Scanner(System.in);
        while (true) {
            String msg = sc.nextLine();
            if (msg.equals("/end")) {
                break;
            }
            if (msg.startsWith("/send ")) {
                String fileName = msg.split("\\s")[1];
                Path filePath = Paths.get("client-repository", fileName);
                if (!Files.exists(filePath)) {
                    System.out.println("Файл для отправки не найден в репозитории");
                    continue;
                }
                SendFile_proto_file.sendFile(filePath, Network_proto_file.getOurInstance().getCurrentChannel(), future -> {
                    if (!future.isSuccess()) {
                        System.out.println("Не удалось отправить файл на сервер");
                        future.cause().printStackTrace();
                    } else {
                        System.out.println("файл передан");
                    }
                });
                continue;
            }
            if (msg.startsWith("/download ")) {
                String fileName = msg.split("\\s")[1];
                sendFileRequest(fileName, Network_proto_file.getOurInstance().getCurrentChannel());
                continue;
            }
            System.out.println("Введена не верная команда, попробуйте еще раз");
        }
        sc.close();
        Network_proto_file.getOurInstance().stop();

    }

    public static void sendFileRequest(String fileName, Channel outChannel) {


        byte[] fileNameBytes = ("/request " + fileName).getBytes(StandardCharsets.UTF_8);

        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1 + 4 + fileNameBytes.length);
        buf.writeByte(CommandsList.CMD_SIGNAL_BYTE);
        buf.writeInt(fileNameBytes.length);
        buf.writeBytes(fileNameBytes);
        System.out.println(new String(fileNameBytes));
        outChannel.writeAndFlush(buf);

    }
}
