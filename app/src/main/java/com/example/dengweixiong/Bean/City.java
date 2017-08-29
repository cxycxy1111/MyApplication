package com.example.dengweixiong.Bean;

import org.litepal.crud.DataSupport;

/**
 * Created by dengweixiong on 2017/8/27.
 */

public class City extends DataSupport{

    private String name;
    private Province province;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }
}
