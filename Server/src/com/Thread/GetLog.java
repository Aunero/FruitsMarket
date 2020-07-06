package com.Thread;

import com.FruitsMarket.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/*
    从客户端获取日志
 */
public class GetLog implements Runnable{
    //套接字对象
    private ServerSocket ss;
    private Socket s;

    public GetLog(ServerSocket ss) {
        this.ss = ss;
    }

    @Override
    public void run() {
        while (true){
            try {
                //监听并获取套接字对象
                Socket s = ss.accept();
                //获取字符输入流
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));  //规定编码
                //读取日志
                String log = br.readLine();
                //拼接并打印
                StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][消息] ").append(log).append(" 来自").append(s.getInetAddress());
                System.out.println(sb);
                //释放资源
                br.close();
                s.close();
                //写日志
                Server.writeLog(sb.toString());
            } catch (IOException e) {
                StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][错误] 获取日志时出错!");
                System.out.println(sb);
                //写日志
                Server.writeLog(sb.toString());
            }
        }
    }
}
