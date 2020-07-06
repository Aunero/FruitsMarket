package com.Thread;

import com.FruitsMarket.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/*
    配置文件发送到客户端
 */
public class ConfigFileToClient implements Runnable {    //实现线程

    //服务端套接字
    private ServerSocket ss;
    private Socket s;

    public ConfigFileToClient(ServerSocket ss) {
        this.ss = ss;
    }

    static File configFile = new File("Server\\config.properties"); //配置文件

    @Override
    public void run() {
        while(true){
            try {
                synchronized (this) {
                    //侦听并返回客户端套接字
                    this.s = ss.accept();
                    //StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][消息] ").append("配置被拉取, ").append("来自").append(s.getInetAddress());
                    //System.out.println(sb);
                    //创建本地文件输入流
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(configFile));
                    //创建网络文件输出流
                    BufferedOutputStream sbos = new BufferedOutputStream(s.getOutputStream());

                    //把配置文件输出到套接字的输出流
                    byte[] bytes = new byte[8192];
                    int len;

                    //配置发送给客户端
                    while ((len = bis.read(bytes)) != -1) {
                        sbos.write(bytes, 0, len);
                    }

                    //结束输出
                    //s.shutdownOutput();

                    //释放资源
                    bis.close();
                    sbos.close();
                    s.close();

                    //写日志
                    //Server.writeLog(sb.toString());

                }

            } catch (Exception e) {
                StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][错误] 发送配置文件时出错!");
                System.out.println(sb);
                //写日志
                Server.writeLog(sb.toString());
            }
        }
    }
}
