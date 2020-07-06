package com.User;
/*
    管理员类
 */
public class Admin extends User{
    //序列化ID
    private static final long serialVersionUID = -7283243913746721609L;

    public Admin() {
    }

    public Admin(String uid, String username, String passwd) {
        super(uid, username, passwd);
    }
}
