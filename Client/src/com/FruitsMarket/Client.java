package com.FruitsMarket;

import com.Config.ConfigFile;
import com.Config.ConfigFileOperater;
import com.Fruit.FruitUtil;
import com.User.UserUtil;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/*
    客户端类
 */
public class Client {
    //服务器配置信息
    public static int port;
    public static String SERVER_IP;
    public static int DOWN_PORT;
    public static int UP_PORT;
    public static int UUID_PORT ;    //接收uuid端口
    public static int SEND_LOG;    //日志发送端口

    //创建客户端套接字对象
    public static Socket down_s = null;
    public static Socket up_s = null;
    public static Socket uuid_s = null;
    public static Socket log_s = null;

    //创建配置文件对象
    public static ConfigFile configFile = new ConfigFile();

    //端口初始化 如果有文件就从文件读端口
    private static void portInit() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("ServerPort.txt"));
            //冒号分割 例如 127.0.0.1:10000  前面是ip 后面是端口
            String[] ServerPort = br.readLine().split(":");
            SERVER_IP = ServerPort[0];
            port = Integer.valueOf(ServerPort[1]);

        } catch (IOException e) {
            //SERVER_IP = "127.0.0.1";
            SERVER_IP = "192.168.22.33";
            port = 12455;
            System.out.println("未配置ServerPort.txt文件!将使用默认配置!");
        }
        //端口设置
        DOWN_PORT = port;    //接收数据端口
        UP_PORT = port + 1;    //发送数据端口
        UUID_PORT = port + 2;    //接收uuid端口
        SEND_LOG = port + 3;    //日志发送端口
    }

    //main方法
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        /**********初始化************/
        System.out.println("连接服务器中...");
        //端口初始化 可能文件读取
        portInit();
        //更新配置
        updateConfig();
        //键盘输入流
        Scanner sc = new Scanner(System.in);
        /****************************/

        System.out.println("连接服务器成功!");
        //功能路由
        while (true) {
            System.out.println("=============欢迎来到水果超市系统=============");
            System.out.println("请输入您的选择: (1.顾客    2.管理员    3.用户注册   4.退出)");
            switch (FruitUtil.isNum(sc)) {
                case 1:
                    customerIndex(sc);
                    break;
                case 2:
                    adminIndex(sc);
                    break;
                case 3:
                    createUser(sc);
                    break;
                case 4:
                    System.exit(0);
                default:
                    System.out.println("输入有误!");
                    continue;
            }
        }
    }

    private static void customerIndex(Scanner sc) throws IOException, ClassNotFoundException {
        //更新配置信息
        Client.updateConfig();
        //获取用户
        int vipIndex = UserUtil.vipLogin(configFile.VIPList, sc);
        //如果返回空用户, 说明登陆失败, 返回
        if (vipIndex == -1) return;
        //发日志
        sendLog("会员[" + configFile.VIPList.get(vipIndex).getUsername() + "]已登录");

        System.out.println("登陆成功!");
        System.out.println("=============欢迎来到水果超市系统=============");
        System.out.println("*************** 今日优惠活动 ***************");
        System.out.println("************* 满100元: 9折优惠 *************");
        System.out.println("************* 满300元: 8折优惠 *************");
        System.out.println("************* 满500元: 7折优惠 *************");
        while (true) {
            System.out.println("===============尊敬的VIP,您好================");
            System.out.println("请输入您的选择: (1.查看水果    2.选购水果    3.购物车    4.修改密码   5.返回)");
            switch (FruitUtil.isNum(sc)) {
                case 1:
                    //更新配置信息
                    Client.updateConfig();
                    FruitUtil.showfruit(configFile.fruits);
                    break;
                case 2:
                    FruitUtil.buyFruit(sc, vipIndex);
                    break;
                case 3:
                    FruitUtil.cart(sc, vipIndex);
                    break;
                case 4:
                    UserUtil.userChangePasswd(sc, vipIndex);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("输入有误!");
                    continue;
            }
        }
    }

    private static void adminIndex(Scanner sc) throws IOException, ClassNotFoundException {
        //更新配置信息
        Client.updateConfig();
        //获取用户
        int userIndex = UserUtil.adminLogin(configFile.adminList, sc);
        //如果返回空用户, 说明登陆失败, 返回
        if (userIndex == -1) return;
        //发日志
        sendLog("管理员[" + configFile.adminList.get(userIndex).getUsername() + "]已登录");

        System.out.println("登陆成功!");
        while (true) {
            System.out.println("=============欢迎来到水果超市系统=============");
            System.out.println("================管理员,您好=================");
            System.out.println("请输入您的选择: (1.查看水果    2.添加水果    3.删除水果    4.修改水果    5.用户管理   6.返回)");
            switch (FruitUtil.isNum(sc)) {
                case 1:
                    //更新配置信息
                    Client.updateConfig();
                    FruitUtil.showfruit(configFile.fruits);
                    break;
                case 2:
                    FruitUtil.addFruit(sc, userIndex);
                    break;
                case 3:
                    FruitUtil.delFruit(sc, userIndex);
                    break;
                case 4:
                    FruitUtil.changeFruit(sc, userIndex);
                    break;
                case 5: //用户管理
                    //更新配置信息
                    Client.updateConfig();
                    UserUtil.userManager(configFile.adminList, configFile.VIPList, sc, userIndex);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("输入有误!");
                    continue;
            }
        }
    }

    //用户注册
    private static void createUser(Scanner sc) throws IOException, ClassNotFoundException {
        while (true) {
            System.out.println("=================用 户 注 册================");
            System.out.println("请输入您的选择: (1.会员注册    2.管理员注册    3.返回)");
            switch (FruitUtil.isNum(sc)) {
                case 1:
                    UserUtil.createVIP(sc);
                    break;
                case 2:
                    UserUtil.createAdmin(sc);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("输入有误!");
                    continue;
            }
        }
    }

    //下载并更新配置
    public static void updateConfig() throws IOException {
        int tryCount = 3;   //三次尝试连接
        while (true) {
            try {
                if (tryCount < 3) System.out.println("尝试重新连接...");
                down_s = new Socket(SERVER_IP, DOWN_PORT);  //用于接收数据
                break;
            } catch (IOException e) {
                System.out.println("连接服务器失败! 请检查服务器是否开启!");
                if (tryCount == 1) {
                    System.out.println("尝试多次连接失败! 退出");
                    System.exit(0);
                }
            }
            tryCount--;
        }
        //读取配置
        ConfigFileOperater.downloadConfig();
        //释放套接字
        down_s.close();
    }

    //上传最新配置
    public static void uploadConfig() throws IOException {
        int tryCount = 3;   //三次尝试连接
        while (true) {
            try {
                if (tryCount < 3) System.out.println("尝试重新连接...");
                up_s = new Socket(SERVER_IP, UP_PORT);  //用于发送数据
                break;
            } catch (IOException e) {
                System.out.println("连接服务器失败! 请检查服务器是否开启!");
                if (tryCount == 1) {
                    System.out.println("尝试多次连接失败! 退出");
                    System.exit(0);
                }
            }
            tryCount--;
        }
        //上传配置
        ConfigFileOperater.uploadConfig();
        //释放套接字
        up_s.close();
    }

    //获得服务器uuid秘钥
    public static String getUUID() {
        int tryCount = 3;   //三次尝试连接
        while (true) {
            try {
                if (tryCount < 3) System.out.println("尝试重新连接...");
                uuid_s = new Socket(SERVER_IP, UUID_PORT);  //用于接收uuid
                break;
            } catch (IOException e) {
                System.out.println("连接服务器失败! 请检查服务器是否开启!");
                if (tryCount == 1) {
                    System.out.println("尝试多次连接失败! 退出");
                    System.exit(0);
                }
            }
            tryCount--;
        }

        String uuid = null;
        try {
            //创建网络输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(uuid_s.getInputStream()));
            //读取
            uuid = br.readLine();
            //释放
            br.close();
            uuid_s.close();
        } catch (IOException e) {
            System.out.println("管理员秘钥获取失败!");
        }

        //返回uuid
        return uuid;
    }

    //发送日志方法 形参为需要发的日志信息
    public static void sendLog(String str) throws IOException {
        int tryCount = 3;   //三次尝试连接
        while (true) {
            try {
                if (tryCount < 3) System.out.println("尝试重新连接...");
                log_s = new Socket(SERVER_IP, SEND_LOG);  //用于接收uuid
                break;
            } catch (IOException e) {
                System.out.println("连接服务器失败! 请检查服务器是否开启!");
                if (tryCount == 1) {
                    return;
                }
            }
            tryCount--;
        }
        //创建网络输出流
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(log_s.getOutputStream(),"UTF-8"));    //规定编码

        try {
            //写出日志
            bw.write(str);
            //释放资源
            bw.close();
            log_s.close();
        } catch (IOException e) {
            System.out.println("日志写出异常!");
        }

    }
}
