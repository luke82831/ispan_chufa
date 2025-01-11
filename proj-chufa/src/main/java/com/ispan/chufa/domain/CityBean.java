package com.ispan.chufa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "city")
public class CityBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 設定為自增
    @Column(name = "id")
    private Long id; // 主鍵

    @Column(name = "cityname", nullable = false)
    private String cityName;

    @Column(name = "district", nullable = false)
    private String district;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
