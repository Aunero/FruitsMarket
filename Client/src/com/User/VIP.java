package com.User;


import com.Fruit.Fruit;

import java.util.ArrayList;

/*
    顾客类
 */
public class VIP extends User{
    //序列化ID
    private static final long serialVersionUID = -2131607199271836785L;

    //购物车
    public ArrayList<Fruit> shopCart = new ArrayList<>();

    public VIP() {
    }

    public VIP(String uid, String username, String passwd) {
        super(uid, username, passwd);
    }


    //重写toString方法, 用于打印用户信息

    @Override
    public String toString() {
        return super.getUid() + "\t\t" + super.getUsername() + "\t\t" + super.getPasswd();
    }


}
