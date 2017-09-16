package com.example.dengweixiong.Bean;

import java.sql.Date;

/**
 * Created by dengweixiong on 2017/9/15.
 */

public class Card {

    private long id;
    //1：余额卡，2：次卡，3：有效期卡
    private int type;
    private boolean del;
    private String name;
    private int balance;
    private Date start_time;
    private Date valid_time;
    private Date create_time;
    private Date last_modify_time;
    private ShopMember last_modify_user;
    private ShopMember creator;
    private Shop owner;

    private Card() {

    }

    //修改时使用的构造器
    public Card(long id,boolean del,String name,int balance,Date start_time,
                Date valid_time,Date last_modify_time,ShopMember last_modify_user) {
        this.id = id;
        this.del = del;
        this.name = name;
        this.balance = balance;
        this.start_time = start_time;
        this.valid_time = valid_time;
        this.last_modify_time = last_modify_time;
        this.last_modify_user = last_modify_user;
    }

    //创建时使用的构造器
    public Card(long id,int type,String name,int balance,Date start_time,
                Date valid_time,Date create_time,Date last_modify_time,ShopMember shopMember,Shop shop) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.balance = balance;
        this.start_time = start_time;
        this.valid_time = valid_time;
        this.create_time = create_time;
        this.last_modify_time = last_modify_time;
        this.creator = shopMember;
        this.owner = shop;
    }

    //显示会员卡时使用的构造器
    public Card(long id,String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean is_valid) {
        this.del = is_valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getValid_time() {
        return valid_time;
    }

    public void setValid_time(Date valid_time) {
        this.valid_time = valid_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
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

    public ShopMember getCreator() {
        return creator;
    }

    public void setCreator(ShopMember creator) {
        this.creator = creator;
    }

    public Shop getOwner() {
        return owner;
    }

    public void setOwner(Shop shop) {
        this.owner = shop;
    }
}
