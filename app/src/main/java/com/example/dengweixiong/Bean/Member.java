package com.example.dengweixiong.Bean;

import java.sql.Date;

/**
 * Created by dengweixiong on 2017/9/15.
 */

public class Member {

    private long id;
    private String name;
    private boolean del;
    private String login_name;
    private String passord;
    private Date birthday;
    private String phone;
    private String im;
    private Date create_time;
    private ShopMember creator;
    private Date last_modify_time;
    private ShopMember last_modify_user;
    private Shop owner;

    public Member() {
    }

   //新建时使用的构造器
    public Member(long id,String name,boolean del,String login_name,
                  String passord,Date birthdate,String phone,String im,Date create_time,
                  ShopMember creator,Date last_modify_time,ShopMember last_modify_user,Shop shop) {
        this.id = id;this.name = name;this.del = del;this.login_name = login_name;
        this.passord = passord;this.birthday = birthdate;this.phone = phone;this.im = im;this.create_time = create_time;
        this.creator = creator;this.last_modify_time = last_modify_time;this.last_modify_user = last_modify_user;this.owner = shop;
    }

    //修改时使用的构造器
    public Member(String name,Date birthdate,String phone,String im,Date last_modify_time,ShopMember last_modify_user) {
        this.name = name;this.birthday = birthdate;this.phone = phone;this.im = im;
        this.last_modify_time = last_modify_time;this.last_modify_user = last_modify_user;
    }

    //构建会员列表时使用的构造器
    public Member(long id,String name) {
        this.id = id;
        this.name = name;
    }

    //构建会员详情时使用的构造器
    public Member(long id,String name,Date birthday,String phone,String im) {
        this.id = id;this.name = name;this.birthday = birthday;this.phone = phone;this.im = im;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public String getLogin_name() {
        return login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public String getPassord() {
        return passord;
    }

    public void setPassord(String passord) {
        this.passord = passord;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public ShopMember getCreator() {
        return creator;
    }

    public void setCreator(ShopMember creator) {
        this.creator = creator;
    }

    public Date getLast_modify_time() {
        return last_modify_time;
    }

    public void setLast_modify_time(Date last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public ShopMember getLast_modify_user() {
        return last_modify_user;
    }

    public void setLast_modify_user(ShopMember last_modify_user) {
        this.last_modify_user = last_modify_user;
    }

    public Shop getOwner() {
        return owner;
    }

    public void setOwner(Shop owner) {
        this.owner = owner;
    }
}
