package com.example.dengweixiong.Bean;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by dengweixiong on 2017/8/27.
 */

public class Province extends DataSupport{

    private String name;
    private Country country;
    private List<City> cityList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}
