package com.Fruit;

import com.FruitsMarket.Client;
import com.User.Admin;
import com.User.VIP;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
    水果操作工具类
 */
public class FruitUtil {

    /************** 用户功能 ***************/
    //打印水果信息
    public static void showfruit(ArrayList<Fruit> fruList) throws IOException, ClassNotFoundException {
        //开始操作
        if (fruList.isEmpty()) {
            //列表空
            System.out.println("********** 列表空 **********");
            return;
        }
        System.out.println("编号\t\t\t名称\t\t价格(元)\t数量\t单位");
        for (Fruit fruit : fruList) {
            System.out.println(fruit);
        }
    }

    //顾客买水果
    public static void buyFruit(Scanner sc, int vipIndex) throws IOException, ClassNotFoundException {
        while (true) {
            //先打印水果列表
            //更新配置信息
            Client.updateConfig();
            showfruit(Client.configFile.fruits);
            System.out.println("=================请添加购物车================");
            System.out.println("请输入您的选择: (1.选择水果    2.返回)");
            switch (isNum(sc)) {
                case 1:
                    buy(sc, vipIndex);
                    break;
                case 2:
                    return;
                default:
                    System.out.println("输入有误!");
                    continue;

            }
        }

    }

    //查看购物车
    public static void cart(Scanner sc, int vipIndex) throws IOException, ClassNotFoundException {
        //打印购物车水果
        System.out.println("您的购物车有以下水果: ");
        showfruit(Client.configFile.VIPList.get(vipIndex).shopCart);
        //计算总价
        int sumPrice = 0;
        for (Fruit fruit : Client.configFile.VIPList.get(vipIndex).shopCart) {
            sumPrice += fruit.getPrice() * fruit.getSum();
        }
        while (true) {
            //打印总价
            sumPrice = discountPrice(sumPrice);
            System.out.println("您的购物车总价为: " + sumPrice + "元");
            System.out.println("================== 结 算 ==================");
            System.out.println("请输入您的选择: (1.结账    2.返回)");
            switch (isNum(sc)) {
                case 1:
                    if (sumPrice == 0) {
                        System.out.println("购物车啥都没有哦~");
                        return;
                    }
                    System.out.println("请选择支付方式: 1.支付宝   2.微信   3.银行卡");
                    sc.next();
                    System.out.println("支付成功! 请等待发货");
                    //清空购物车
                    Client.configFile.VIPList.get(vipIndex).shopCart.clear();
                    break;
                case 2:
                    return;
                default:
                    System.out.println("输入有误!");
                    continue;
            }
            break;
        }

        //保存并上传信息
        Client.uploadConfig();
        //更新配置信息
        Client.updateConfig();
        //发日志
        Client.sendLog("会员[" + Client.configFile.VIPList.get(vipIndex).getUsername() + "]结算了购物车,共计消费: " + sumPrice + "元");
    }

    //将水果添加到购物车
    private static void buy(Scanner sc, int vipIndex) throws IOException, ClassNotFoundException {
        System.out.println("请输入水果编号: ");
        String id = sc.next();
        //获得水果的集合索引
        int index = getIndex(Client.configFile.fruits, id);
        //如果水果不存在 提示没有 直接返回
        if (index == -1) {
            System.out.println("编号不存在!");
            return;
        }
        System.out.println("请输入购买数量(斤): ");
        int num = isNum(sc);
        //得到水果
        Fruit f = getFruit(index, num);
        if (f != null) { //判断库存够不够
            //如果购物车存在这个水果
            int cartIndex = getIndex(Client.configFile.VIPList.get(vipIndex).shopCart, f.getName());
            if (cartIndex != -1) {
                //直接添加数量并返回即可
                Fruit fru = Client.configFile.VIPList.get(vipIndex).shopCart.get(cartIndex);
                fru.setSum(fru.getSum() + num);
            } else {
                //添加购物车
                Client.configFile.VIPList.get(vipIndex).shopCart.add(f);
                System.out.println("添加购物车成功!");
            }

        } else {
            System.out.println("库存不足!");
        }

        //保存并上传信息
        Client.uploadConfig();
        //更新配置信息
        Client.updateConfig();
        //发日志
        Client.sendLog("会员[" + Client.configFile.VIPList.get(vipIndex).getUsername() + "]将" + num + "斤" + f.getName() + "添加至购物车");
    }

    //添加水果
    public static void addFruit(Scanner sc, int userIndex) throws IOException {
        String id = newSid(getMaxNum(Client.configFile.fruits,"Fruit"), "fruit");
        System.out.println("请输入水果名称:");
        String name = sc.next();
        System.out.println("请输入水果价格(元/斤):");
        int price = isNum(sc);
        System.out.println("请输入水果库存(斤):");
        int sum = isNum(sc);

        //尝试获得水果的在集合的索引
        int index = getIndexByName(Client.configFile.fruits, name);
        //如果水果不存在 新建水果
        if (index == -1) {
            //创建水果对象并添加到集合
            Client.configFile.fruits.add(new Fruit(id, name, price, sum));
        } else {//如果库存存在这个水果
            //直接添加数量即可
            Fruit fruit = Client.configFile.fruits.get(index);
            fruit.setSum(fruit.getSum() + sum);
            fruit.setPrice(price);
        }
        System.out.println("添加成功!");

        //保存并上传信息
        Client.uploadConfig();
        //更新配置信息
        Client.updateConfig();
        //发日志
        Client.sendLog("管理员[" + Client.configFile.adminList.get(userIndex).getUsername() + "]向库存中添加了" + sum + "斤" + name + ",售价为: " + price + "元");
    }

    //删除水果
    public static void delFruit(Scanner sc, int userIndex) throws IOException, ClassNotFoundException {
        //更新配置信息
        Client.updateConfig();
        System.out.println("库存中有以下水果:");
        showfruit(Client.configFile.fruits);
        System.out.println("请输入要删除的水果编号:");
        String id = sc.next();
        String name;
        //通过id找到索引
        int index = getIndex(Client.configFile.fruits, id);
        if (index != -1) {     //水果存在
            while (true) {
                System.out.println("确认删除水果: " + Client.configFile.fruits.get(index).getName() + "(1.确认   2.取消)");
                switch (isNum(sc)) {
                    case 1:
                        name = Client.configFile.fruits.get(index).getName();
                        Client.configFile.fruits.remove(index);
                        System.out.println("删除水果成功!");
                        break;
                    case 2:
                        return;
                    default:
                        System.out.println("输入有误!");
                        continue;
                }
                break;
            }
        } else {
            System.out.println("要找的水果不存在");
            return;
        }
        //保存并上传信息
        Client.uploadConfig();
        //更新配置信息
        Client.updateConfig();
        //发日志
        Client.sendLog("管理员[" + Client.configFile.adminList.get(userIndex).getUsername() + "]删除了库存中的" + name);
    }

    //修改水果属性 库存 价格
    public static void changeFruit(Scanner sc, int userIndex) throws IOException, ClassNotFoundException {
        //更新配置信息
        Client.updateConfig();
        System.out.println("库存中有以下水果:");
        showfruit(Client.configFile.fruits);
        System.out.println("请输入要修改的水果编号:");
        String id = sc.next();
        //通过id找到索引
        int index = getIndex(Client.configFile.fruits, id);
        if (index != -1) {     //水果存在
            Fruit fruit = Client.configFile.fruits.get(index);
            while (true) {
                System.out.println("将要修改的水果为: " + fruit.getName());
                System.out.println("请输入要修改的属性: " + "(1.价格   2.库存   3.取消)");
                switch (isNum(sc)) {
                    case 1:
                        System.out.println("请输入修改价格(元/斤): ");
                        int price = isNum(sc);
                        fruit.setPrice(price);
                        //发日志
                        Client.sendLog("管理员[" + Client.configFile.adminList.get(userIndex).getUsername() + "]把" + Client.configFile.fruits.get(index).getName() + "的价格修改为: " + price + "元");
                        break;
                    case 2:
                        System.out.println("请输入修改库存(斤): ");
                        int num = isNum(sc);
                        fruit.setSum(num);
                        //发日志
                        Client.sendLog("管理员[" + Client.configFile.adminList.get(userIndex).getUsername() + "]把" + Client.configFile.fruits.get(index).getName() + "的库存修改为: " + num + "斤");
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("输入有误!");
                        continue;
                }
                break;
            }
        } else {
            System.out.println("要找的水果不存在");
            return;
        }
        System.out.println("属性修改成功!");

        //保存并上传信息
        Client.uploadConfig();
        //更新配置信息
        Client.updateConfig();
    }

    /*************** 工具方法 *******************/

    //通过sid查找所在对象的索引，返回索引，返回值为-1时不存在
    public static int getIndex(ArrayList<Fruit> fruits, String id) {
        if (fruits.isEmpty()) return -1;
        for (int i = 0; i < fruits.size(); i++) {  //遍历集合
            Fruit fru = fruits.get(i);
            if (id.equals(fru.getId())) return i;  //找到符合的sid并返回索引
        }
        return -1;
    }

    //通过name查找所在对象的索引，返回索引，返回值为-1时不存在
    public static int getIndexByName(ArrayList<Fruit> fruits, String name) {
        if (fruits.isEmpty()) return -1;
        for (int i = 0; i < fruits.size(); i++) {  //遍历集合
            Fruit fru = fruits.get(i);
            if (name.equals(fru.getName())) return i;  //找到符合的sid并返回索引
        }
        return -1;
    }

    //自动生成集合中最新的编号  参数为开头字符串
    public static String newSid(int sid, String header) {
        //获取到最大的id后再加1
        sid++;
        //返回格式化后的学号, 前面补0, 最高三位数
        int zeroLen = 3 - String.valueOf(sid).length();   //获取需要补的0的数量
        StringBuilder sb = new StringBuilder();
        sb.append(header);
        //补0
        for (int i = 0; i < zeroLen; i++) {
            sb.append("0");
        }
        //补数字
        sb.append(sid);
        //返回结果
        return sb.toString();
    }

    //判断是否是一个整数, 正确则直接返回, 错误则反复提示重新输入
    public static int isNum(Scanner sc) {
        int num;
        //判断整型
        while (true) {
            try {
                num = sc.nextInt();
                if (num <= 0) {
                    System.out.print("输入不合法，请重新输入：");
                    sc.nextLine();
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.print("输入不合法，请重新输入：");
                sc.nextLine();
            }
        }
        return num;
    }

    //通过索引获得水果, 对水果数量进行减少, 并返回减少数量的水果, 如果库存不够, 返回null
    private static Fruit getFruit(int index, int buyNum) {
        Fruit rst = null;   //默认返回null
        //获得库存中的水果
        Fruit fruit = Client.configFile.fruits.get(index);
        //判断库存够不够
        if (fruit.getSum() - buyNum >= 0) {
            //得到将要返回水果
            rst = new Fruit(fruit.getId(), fruit.getName(), fruit.getPrice(), fruit.getSum());
            //设置购买水果数量
            rst.setSum(buyNum);
            //减库存
            fruit.setSum(fruit.getSum() - buyNum);
        }
        return rst;
    }

    //判断折扣 返回折扣价
    public static int discountPrice(int price) {
        if (price < 100) {
            return price;
        } else if (price >= 100 && price < 300) {
            System.out.println("享受折扣: 满100元9折优惠");
            return (int) (price * 0.9);
        } else if (price >= 300 && price < 500) {
            System.out.println("享受折扣: 满300元8折优惠");
            return (int) (price * 0.8);
        } else {
            System.out.println("享受折扣: 满500元7折优惠");
            return (int) (price * 0.7);
        }
    }

    //获取集合中后三位编号最大的编号并返回, 第二个参数传类名
    public static int getMaxNum(ArrayList<?> list,String type) {
        int sid = 0;
        for (Object o : list) {
            int id;
            String fullId = null;
            //判断水果类
            if(type == "Fruit"){
                Fruit f = (Fruit)o;
                fullId = f.getId();
            }else if(type == "VIP"){    //如果是VIP
                VIP f = (VIP)o;
                fullId = f.getUid();
            }else{
                Admin f = (Admin)o;
                fullId = f.getUid();
            }

            id = Integer.parseInt(fullId.substring(fullId.length() - 3));      //获得后三位
            //如果sid小于当前id, 赋值
            if (sid < id) {
                sid = id;
            }
        }
        //获取到最大的id后再加1
        sid++;
        //返回结果
        return sid;
    }


}
