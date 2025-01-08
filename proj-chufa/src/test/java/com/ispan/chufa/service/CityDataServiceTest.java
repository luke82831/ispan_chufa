package com.ispan.chufa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ispan.chufa.domain.CityBean;
import com.ispan.chufa.repository.CityRepository;

@SpringBootTest
public class CityDataServiceTest {

    @Autowired
    private CityDataService cityDataService;

    @Autowired
    private CityRepository cityRepository;

    @Test
    public void testImportCityData() throws Exception {
        // 執行匯入
        cityDataService.importCityData();

        // 驗證資料總數
        assertEquals(368, cityRepository.count()); // 預期匯入的資料筆數

        // 驗證部分資料
        List<CityBean> taipeiDistricts = cityRepository.findByCityName("臺北市");
        assertEquals(12, taipeiDistricts.size());
    }
}