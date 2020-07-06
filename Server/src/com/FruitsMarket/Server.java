package com.FruitsMarket;

import com.Thread.ConfigFileToClient;
import com.Thread.GetConfigFile;
import com.Thread.GetLog;
import com.Thread.SendUUID;

import java.io.*;
import java.net.ServerSocket;
import java.util.Date;

/*
    水果超市 服务器端
    功能:
        多线程
        记录并打印日志
        保存水果库存, 用户, 管理员信息
        请求生成序列号, 用于验证管理员注册
 */
public class Server {

    //读取端口
    public static int port;

    //端口
    public static int UP_PORT;    //数据发送端口
    public static int DOWN_PORT;    //数据接收端口
    public static int SEND_UUID;    //发送uuid端口
    public static int GET_LOG;    //日志接收端口

    //打印日志输出文件流
    public static BufferedWriter log;
    static {
        try {
            log = new BufferedWriter(new FileWriter("Server\\log.txt",true));   //使用追加模式
            log.newLine();  //换新行
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][错误] 日志文件获取失败!");
            System.out.println(sb);
        }
    }

    public static void main(String[] args) throws IOException {
        /**************** 初始化 ********************/
        //端口初始化 如果文件存在就从文件读取端口
        portInit();
        StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][消息] 服务器初始化完毕!");
        System.out.println(sb);
        //写日志
        writeLog(sb.toString());
        /*******************************************/

        //创建服务器套接字对象
        ServerSocket ss1 = new ServerSocket(UP_PORT);   //配置发送对象
        ServerSocket ss2 = new ServerSocket(DOWN_PORT);   //配置接收对象
        ServerSocket ss3 = new ServerSocket(SEND_UUID);   //发送uuid对象
        ServerSocket ss4 = new ServerSocket(GET_LOG);   //接收日志对象
        //启动配置发送线程
        new Thread(new ConfigFileToClient(ss1)).start();
        //启动配置接收线程
        new Thread(new GetConfigFile(ss2)).start();
        //启动发送uuid线程
        new Thread(new SendUUID(ss3)).start();
        //启动获取日志线程
        new Thread(new GetLog(ss4)).start();
        //线程启动完毕
        StringBuilder sb1 = new StringBuilder().append("[").append(new Date()).append("][消息] 所有线程启动完毕!");
        System.out.println(sb1);
        //写日志
        writeLog(sb1.toString());

    }

    //端口初始化 如果有文件就从文件读端口
    private static void portInit() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("ServerPort.txt"));
            //冒号之后的就是端口
            port = Integer.valueOf(br.readLine().split(":")[1]);
            StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][消息] 从配置文件读取到端口号");
            System.out.println(sb);
            //写日志
            writeLog(sb.toString());
        } catch (IOException e) {
            port = 12455;
            StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][消息] 无法从配置文件读取到端口号,使用默认端口号");
            System.out.println(sb);
            writeLog(sb.toString());
        }
        //端口设置
        UP_PORT = port;    //数据发送端口
        DOWN_PORT = port + 1;    //数据接收端口
        SEND_UUID = port + 2;    //发送uuid端口
        GET_LOG = port + 3;    //日志接收端口

    }

    //写日志方法
    public static void writeLog(String s) {
        try {
            Server.log.write(s);
            Server.log.flush();
            Server.log.newLine();
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder().append("[").append(new Date()).append("][错误] 写出日志文件异常!");
            System.out.println(sb);
        }
    }
}
