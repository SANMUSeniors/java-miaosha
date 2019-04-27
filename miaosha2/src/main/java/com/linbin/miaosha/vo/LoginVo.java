package com.linbin.miaosha.vo;


public class LoginVo {

    private String mobile;
    private String password;

    public String getMobile() {
        return mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo{" + "mobile='" + mobile + '\'' + ", password='" + password + '\'' + '}';
    }
}
