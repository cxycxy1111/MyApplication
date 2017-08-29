package com.example.dengweixiong.Bean;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by dengweixiong on 2017/8/21.
 */

public class Country extends DataSupport{

    private String countryName;
    private List<Province> provinceList;

    public Country(){

    }

    public Country(String aCountryName) {
        this.countryName = aCountryName;
    }

    public String getCountryName() {
        return countryName;
    }

    public List<Province> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<Province> provinceList) {
        this.provinceList = provinceList;
    }

    public void setCountryName(String aCountryName) {
        this.countryName = aCountryName;
    }

}
