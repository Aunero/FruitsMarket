package com.Fruit;

import java.io.Serializable;

/*
    水果类
 */
public class Fruit implements Serializable {    //实现序列化, 以便保存信息

    private static final long serialVersionUID = 3412545881896336879L;    //序列化ID

    //水果的属性
    private String id;
    private String name;
    private int price;
    private int sum;        //库存

    //构造方法
    public Fruit() {
    }

    public Fruit(String id, String name, int price, int sum) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sum = sum;
    }

    //get set 方法

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    //重写toString方法, 用于打印水果信息

    @Override
    public String toString() {
        return id + "\t" + name + "\t\t" + price + "\t\t" + sum + "\t斤";
    }
}
