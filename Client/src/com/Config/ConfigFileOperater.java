package com.Config;
/*
    操作配置工具类
 */

import com.FruitsMarket.Client;

import java.io.*;

public class ConfigFileOperater {


    /*************** 网络操作 ******************/
    //从服务器读取配置
    public static void downloadConfig()  {
        try {
            //创建网络输入流
            ObjectInputStream ois = new ObjectInputStream(Client.down_s.getInputStream());
            //读取配置
            Client.configFile = (ConfigFile) ois.readObject();
            //释放资源
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("从服务器下载配置异常!");
        }

    }

    //把配置文件上传至服务器
    public static void uploadConfig(){
        try {
            //创建网络输出流
            ObjectOutputStream oos = new ObjectOutputStream(Client.up_s.getOutputStream());
            //发送更新配置
            oos.writeObject(Client.configFile);
            //释放资源
            oos.close();
        } catch (IOException e) {
            System.out.println("向服务器发送配置异常!");
        }


    }

}
