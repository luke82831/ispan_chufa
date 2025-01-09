package com.ispan.chufa.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispan.chufa.domain.CityBean;
import com.ispan.chufa.repository.CityRepository;

@Service
public class CityDataService {

    @Autowired
    private CityRepository cityRepository;

    public void importCityData() throws Exception {
        // 讀取 JSON 檔案
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new ClassPathResource("static/taiwanRegion.json").getFile();
        Map<String, List<String>> cityDistrictMap = objectMapper.readValue(file, Map.class);

        // 解析並存入資料庫
        for (Map.Entry<String, List<String>> entry : cityDistrictMap.entrySet()) {
            String cityName = entry.getKey();
            List<String> districts = entry.getValue();

            for (String district : districts) {
                CityBean cityBean = new CityBean();
                cityBean.setCityName(cityName); // 設定縣市名稱
                cityBean.setDistrict(district); // 設定區域名稱
                cityRepository.save(cityBean); // 儲存到資料庫
            }
        }
    }
}