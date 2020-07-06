package com.Thread;

import com.FruitsMarket.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class GetConfigFile implements Runnable{

    //服务端套接字
    private ServerSocket ss;
    private Socket s;

    public GetConfigFile(ServerSocket ss){
        this.ss = ss;
    }

    @Override
    public void run() {
        while (true){
            try {
                synchronized (this) {
                    //侦听并返回客户端套接字
                    this.s = ss.accept();
                    //StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][消息] ").append("配置已更新, ").append("来自").append(s.getInetAddress());
                    //System.out.println(sb);
                    //创建网络文件输入流
                    BufferedInputStream sbis = new BufferedInputStream(s.getInputStream());
                    //创建本地文件输出
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ConfigFileToClient.configFile));

                    //创建缓冲
                    byte[] buffer = new byte[8192];
                    int len;

                    //从客户端接收数据
                    while ((len = sbis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }

                    //结束输出
                    //s.shutdownOutput();


                    //释放资源
                    bos.close();
                    sbis.close();
                    s.close();

                    //写日志
                    //Server.writeLog(sb.toString());
                }


            } catch (Exception e) {
                StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][错误] 获取配置文件时出错!");
                System.out.println(sb);
                //写日志
                Server.writeLog(sb.toString());
            }
        }
    }
}
