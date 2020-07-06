package com.Config;

import com.Fruit.Fruit;
import com.User.Admin;
import com.User.VIP;

import java.io.Serializable;
import java.util.ArrayList;

/*
    配置文件类
 */
public class ConfigFile implements Serializable {
    //序列化
    private static final long serialVersionUID = 1990662610113283362L;
    //存放用户的集合
    public ArrayList<VIP> VIPList = new ArrayList<>();
    //存放管理员的集合
    public ArrayList<Admin> adminList = new ArrayList<>();
    //存放水果的集合
    public ArrayList<Fruit> fruits = new ArrayList<>();
}
