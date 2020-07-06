package com.User;

import java.io.Serializable;

/*
    用户类
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1961769684812512885L; //实现序列化 以便保存
    //用户属性
    private String uid;
    private String username;    //用户名
    private String passwd;      //密码

    //构造方法

    public User() {
    }

    public User(String uid, String username, String passwd) {
        this.uid = uid;
        this.username = username;
        this.passwd = passwd;
    }

    //get set 方法

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    //重写toString方法, 用于打印用户信息

    @Override
    public String toString() {
        return uid + "\t" + username + "\t\t" + passwd;
    }
}
