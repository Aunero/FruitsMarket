package com.Thread;

import com.FruitsMarket.Server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

/*
    创建秘钥并发送 线程
 */
public class SendUUID implements Runnable{

    //服务端套接字
    private ServerSocket ss;
    private Socket s;

    public SendUUID(ServerSocket ss) {
        this.ss = ss;
    }

    @Override
    public void run() {
        while (true){
            synchronized (this){    //同步锁
                try {
                    //侦听并返回套接字
                    this.s = ss.accept();
                    //随机生成UUID
                    UUID uuid = UUID.randomUUID();
                    StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][消息] ").append("请求管理员秘钥,秘钥为: ").append(uuid).append(" 来自").append(s.getInetAddress());
                    System.out.println(sb);
                    //创建网络输出流 传送uuid 字符串        字节输出流 转 字符输出流 转 缓冲字符流
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    bw.write(String.valueOf(uuid)); //发送uuid
                    bw.flush(); //刷新
                    bw.newLine();   //新行

                    //释放资源
                    bw.close();

                    //写日志
                    Server.writeLog(sb.toString());

                } catch (IOException e) {
                    StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][错误] 发送管理员秘钥时出错!");
                    System.out.println(sb);
                    //写日志
                    Server.writeLog(sb.toString());
                }
            }
        }
    }
}
