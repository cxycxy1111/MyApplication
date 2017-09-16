package com.example.dengweixiong.Bean;

/**
 * Created by dengweixiong on 2017/9/15.
 */

public class Shop {

    private long id;
    private String name;

    public Shop() {}

    public Shop(long id,String name) {
        this.id = id;
        this.name = name;
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
}
