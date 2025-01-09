package com.ispan.chufa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.service.CityDataService;


@RestController
public class CityController {

    @Autowired
    private CityDataService cityDataService;

    @PostMapping("/import-city-data")
    public String importCityData() {
        try {
            // cityDataService.importCityData("/city_data.json");
            return "City data imported successfully!";
        } catch (Exception e) {
            return "Error importing city data: " + e.getMessage();
        }
    }
}