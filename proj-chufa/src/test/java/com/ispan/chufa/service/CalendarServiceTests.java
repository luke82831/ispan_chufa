package com.ispan.chufa.service;
	import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ispan.chufa.domain.CalendarBean;
import com.ispan.chufa.repository.CalendarRepository;


	@SpringBootTest
	public class CalendarServiceTests {

	    @Autowired
	    private CalendarService calendarService;

	    @Autowired
	    private CalendarRepository calendarRepository;

	    @Test
	    public void testImportCalendarData() throws Exception {
	        // 執行匯入
	        calendarService.importCalendarData();

	        // 驗證資料總數
	        assertEquals(365, calendarRepository.count()); // 預期匯入的資料筆數，假設是 10 筆

	        // 驗證部分資料
	        List<CalendarBean> holidayData = calendarRepository.findByIsHoliday(true);
	        assertEquals(115, holidayData.size()); // 假設有 4 筆假日資料

	        // 驗證特定日期資料
//	        CalendarBean firstEntry = calendarRepository.findById("2025-01-01").orElse(null);
//	        assertEquals("2025-01-01", firstEntry.getDate().toString());
//	        assertEquals("三", firstEntry.getWeek());
//	        assertEquals(true, firstEntry.isIsHoliday());
//	        assertEquals("開國紀念日", firstEntry.getDescription());
//	    }
	}

}
