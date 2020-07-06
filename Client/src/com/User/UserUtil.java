package com.User;
/*
    用户工具类
 */

import com.FruitsMarket.Client;
import com.Fruit.FruitUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import static com.Fruit.FruitUtil.getMaxNum;

public class UserUtil {


    //账号密码正则表达式
    public static String regex = "^[0-9A-Za-z]{6,15}$";
    //打印用户信息
    public static void showUser(ArrayList<Admin> adminList, ArrayList<VIP> vipList) throws IOException, ClassNotFoundException {
        //更新配置信息
        Client.updateConfig();
        //开始操作
        if (adminList.isEmpty()) {
            //列表空
            System.out.println("********** 列表空 **********");
            return;
        }
        System.out.println("编号\t\t\t用户名\t\t密码");
        for (Admin admin : adminList) {
            System.out.println(admin);
        }
        for (VIP vip : vipList) {
            System.out.println(vip);
        }
    }

    //创建会员
    public static void createVIP(Scanner sc) throws IOException{
        String uid = FruitUtil.newSid(getMaxNum(Client.configFile.VIPList,"VIP"), "user");  //生成编号
        System.out.println("请输入用户名:");
        String name = islegal(sc);
        //判断重复用户名
        if(getUserIndex(Client.configFile.VIPList,name) != -1){
            System.out.println("会员用户名已存在!");
            return;
        }
        System.out.println("请输入密码:");
        String passwd = islegal(sc);
        System.out.println("请输入确认密码:");
        String passwd2 = sc.next();
        if (!passwd.equals(passwd2)) {
            System.out.println("两次输入密码不一致!");
            return;
        }
        //创建VIP
        VIP v = new VIP(uid, name, passwd);
        //保存到集合
        Client.configFile.VIPList.add(v);
        System.out.println("创建会员成功!");

        //保存并上传信息
        Client.uploadConfig();
        //更新配置信息
        Client.updateConfig();
        //发送日志
        Client.sendLog("用户[" + name + "]被注册");

    }

    //创建管理员账号
    public static void createAdmin(Scanner sc) throws IOException, ClassNotFoundException {
        //判断秘钥来创建管理员账号
        System.out.println("请输入服务器秘钥:");
        String uuid = Client.getUUID();
        if (!sc.next().equals(uuid)) {
            System.out.println("秘钥错误!创建失败!");
            return;
        }
        System.out.println("秘钥验证成功!");
        //开始引导创建
        String uid = FruitUtil.newSid(getMaxNum(Client.configFile.adminList,"Admin"), "admin");  //生成编号
        System.out.println("请输入用户名:");
        String name = islegal(sc);
        //判断重复用户名
        if(getAdminIndex(Client.configFile.adminList,name) != -1){
            System.out.println("管理员用户名已存在!");
            return;
        }
        System.out.println("请输入密码:");
        String passwd = islegal(sc);
        System.out.println("请输入确认密码:");
        String passwd2 = sc.next();
        if (!passwd.equals(passwd2)) {
            System.out.println("两次输入密码不一致!");
            return;
        }
        //创建管理员
        Admin a = new Admin(uid, name, passwd);
        //保存到集合
        Client.configFile.adminList.add(a);
        System.out.println("创建管理员成功!");
        //保存并上传信息
        Client.uploadConfig();
        //更新配置信息
        Client.updateConfig();
        //发送日志
        Client.sendLog("管理员[" + name + "]被注册");
    }


    /************* VIP登陆 ********************/

    //通过用户名获取用户索引 不存在返回-1
    static int getUserIndex(ArrayList<VIP> vipList, String username) {
        if(vipList.isEmpty()) return -1;
        for (int i = 0; i < vipList.size(); i++) {
            //如果用户名能找到
            if (vipList.get(i).getUsername().equals(username)) {
                return i;   //返回索引
            }
        }
        return -1;
    }


    //登陆账号, 如果账号密码验证无误, 直接返回索引, 否则返回-1
    public static int vipLogin(ArrayList<VIP> vipList, Scanner sc) {
        //登陆顾客账号
        System.out.println("请输入用户名:");
        String username = sc.next();
        int userIndex = UserUtil.getUserIndex(vipList, username);
        //寻找用户名
        if (userIndex == -1) {
            System.out.println("用户不存在!");
            return -1;
        }
        System.out.println("请输入密码:");
        String passwd = sc.next();

        //判断密码是否正确
        if (!passwd.equals(vipList.get(userIndex).getPasswd())) {
            System.out.println("密码不正确!");
            return -1;
        }
        return userIndex;
    }

    /************* 管理员登陆 ********************/

    //通过用户名获取用户索引 不存在返回-1
    static int getAdminIndex(ArrayList<Admin> adminList, String username) {
        if(adminList.isEmpty()) return -1;
        for (int i = 0; i < adminList.size(); i++) {
            //如果用户名能找到
            if (adminList.get(i).getUsername().equals(username)) {
                return i;   //返回索引
            }
        }
        return -1;
    }


    //登陆账号, 如果账号密码验证无误, 直接返回索引, 否则返回-1
    public static int adminLogin(ArrayList<Admin> adminList, Scanner sc) {
        //登陆顾客账号
        System.out.println("请输入用户名:");
        String username = sc.next();
        int userIndex = UserUtil.getAdminIndex(adminList, username);
        //寻找用户名
        if (userIndex == -1) {
            System.out.println("用户不存在!");
            return -1;
        }
        System.out.println("请输入密码:");
        String passwd = sc.next();

        //判断密码是否正确
        if (!passwd.equals(adminList.get(userIndex).getPasswd())) {
            System.out.println("密码不正确!");
            return -1;
        }
        return userIndex;
    }

    //用户管理页面
    public static void userManager(ArrayList<Admin> adminList, ArrayList<VIP> vipList, Scanner sc, int userIndex) throws IOException, ClassNotFoundException {
        showUser(adminList, vipList);
        while (true) {
            System.out.println("请输入您的选择: (1.删除用户    2.修改密码    3.返回)");
            switch (FruitUtil.isNum(sc)) {
                case 1:
                    delUser(adminList, vipList, sc, userIndex);      //删除用户
                    break;
                case 2:
                    changePasswd(adminList, vipList, sc, userIndex);     //修改密码
                    break;
                case 3:
                    return;
                default:
                    System.out.println("输入有误!");
                    continue;
            }
        }

    }

    //修改密码方法
    private static void changePasswd(ArrayList<Admin> adminList, ArrayList<VIP> vipList, Scanner sc, int userIndex) throws IOException, ClassNotFoundException {
        boolean isVip = false;    //是否为会员标记位 默认是管理员
        System.out.println("请输入要修改密码的账号:");
        String username = sc.next();
        //在管理员集合中搜索, 返回索引
        int index = getAdminIndex(adminList, username);
        if (index == -1) {    //在管理员中找不到
            //搜不到就在用户中搜索
            index = getUserIndex(vipList, username);
            //用户不存在
            if (index == -1) {
                System.out.println("用户不存在!");
                return;
            }
            //找到了, 放标记, 是会员
            isVip = true;
        }

        System.out.println("请输入修改的密码:");
        String tempPasswd = sc.next();
        System.out.println("请确认密码:");
        String passwd = sc.next();
        //如果不相同 返回
        if (!tempPasswd.equals(passwd)) {
            System.out.println("两次输入不一致!");
            return;
        }

        //设置密码
        if (isVip) {  //是会员
            Client.configFile.VIPList.get(index).setPasswd(passwd);
        } else {      //是管理员
            Client.configFile.adminList.get(index).setPasswd(passwd);
        }
        System.out.println("密码修改成功!");
        //保存并上传信息
        Client.uploadConfig();
        //更新配置信息
        Client.updateConfig();
        //发日志
        Client.sendLog("管理员[" + Client.configFile.adminList.get(userIndex).getUsername() + "]将用户[" + Client.configFile.VIPList.get(index).getUsername() + "]的密码修改为[" + passwd + "]");

    }

    //删除用户方法
    private static void delUser(ArrayList<Admin> adminList, ArrayList<VIP> vipList, Scanner sc, int userIndex) throws IOException {
        boolean isVip = false;    //是否为会员标记位 默认是管理员
        System.out.println("请输入要删除用户的账号:");
        String username = sc.next();
        //在管理员集合中搜索, 返回索引
        int index = getAdminIndex(adminList, username);
        if (index == -1) {    //在管理员中找不到
            //搜不到就在用户中搜索
            index = getUserIndex(vipList, username);
            //用户不存在
            if (index == -1) {
                System.out.println("用户不存在!");
                return;
            }
            //找到了, 放标记, 是会员
            isVip = true;
        }


        System.out.println("确认删除用户? (1.是   2.否)");
        while (true) {
            String name;
            switch (FruitUtil.isNum(sc)) {
                case 1:
                    if (isVip) {  //是会员
                        name = Client.configFile.VIPList.get(index).getUsername();
                        Client.configFile.VIPList.remove(index);
                    } else {      //是管理员
                        name = Client.configFile.VIPList.get(index).getUsername();
                        Client.configFile.adminList.remove(index);
                    }
                    System.out.println("删除成功!");
                    //保存并上传信息
                    Client.uploadConfig();
                    //更新配置信息
                    Client.updateConfig();
                    //发日志
                    Client.sendLog("管理员[" + Client.configFile.adminList.get(userIndex).getUsername() + "]删除了用户[" + name + "]");
                    break;
                case 2:
                    System.out.println("取消删除");
                    return;
                default:
                    System.out.println("输入有误,请重新输入: ");
                    continue;
            }
            break;
        }
    }

    public static void userChangePasswd(Scanner sc, int vipIndex) throws IOException {
        System.out.println("请输入修改的密码:");
        String tempPasswd = sc.next();
        System.out.println("请确认密码:");
        String passwd = sc.next();
        //如果不相同 返回
        if (!tempPasswd.equals(passwd)) {
            System.out.println("两次输入不一致!");
            return;
        }
        //设置密码
        Client.configFile.VIPList.get(vipIndex).setPasswd(passwd);

        System.out.println("密码修改成功!");
        //保存并上传信息
        Client.uploadConfig();
        //更新配置信息
        Client.updateConfig();
        //发日志
        Client.sendLog("用户[" + Client.configFile.VIPList.get(vipIndex).getUsername() + "]将自己的密码修改为[" + passwd + "]");

    }


    //判断用户名或者密码是否合法, 不合法则循环提示重新输入, 合法直接返回
    public static String islegal (Scanner sc){
        while (true){
            String str = sc.next();
            if(!str.matches(regex)){    //输入不合法
                System.out.println("不合法!请输入6-15位的英文或数字: ");
            }else {
                return str;
            }
        }
    }
}
