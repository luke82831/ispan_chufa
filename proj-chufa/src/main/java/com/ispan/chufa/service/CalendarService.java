package com.ispan.chufa.service;

import java.time.LocalDate;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.CalendarBean;
import com.ispan.chufa.repository.CalendarRepository;

@Service
public class CalendarService {

    // // 輸入陣列日期(成功)
    // @Autowired
    // private CalendarRepository calendarRepository;
    //
    // public List<CalendarBean> saveAllCalendars(List<CalendarBean> calendars) {
    // return calendarRepository.saveAll(calendars);
    // }

    // 可輸入單一日期的程式碼
    @Autowired
    private CalendarRepository calendarRepository;

    public CalendarBean saveCalendar(CalendarBean calendar) {
        return calendarRepository.save(calendar);
    }

    public Optional<CalendarBean> findCalendarByDate(LocalDate date) {
        return calendarRepository.findById(date);
    }

    // @Autowired
    // private CalendarRepository calendarRepository;
    //
    // public void importCalendarData() throws Exception {
    // // JSON 檔案的絕對路徑
    // File file = new ClassPathResource("static/2025.json").getFile();
    //
    // // 讀取 JSON 檔案
    // ObjectMapper objectMapper = new ObjectMapper();
    // List<Map<String, Object>> calendarDataList = objectMapper.readValue(file,
    // List.class);
    //
    // DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    // DateTimeFormatter outputFormatter =
    // DateTimeFormatter.ofPattern("yyyy/MM/dd");
    //
    // // 解析並存入資料庫
    // for (Map<String, Object> calendarData : calendarDataList) {
    // try {
    // String dateStr = (String) calendarData.get("date");
    // LocalDate date = LocalDate.parse(dateStr, inputFormatter);
    //
    //
    // String week = (String) calendarData.get("week");
    // boolean isHoliday = (boolean) calendarData.get("isHoliday");
    // String description = (String) calendarData.get("description");
    //
    // System.out.println("Parsing data: " + date + ", " + week + ", " + isHoliday +
    // ", " + description);
    //
    // // 建立 CalendarBean 實體並設定屬性
    // CalendarBean calendarBean = new CalendarBean();
    // calendarBean.setDate(date); // 使用 LocalDate 賦值
    // calendarBean.setWeek(week);
    // calendarBean.setIsHoliday(isHoliday);
    // calendarBean.setDescription(description);
    //
    // // 儲存到資料庫
    // calendarRepository.save(calendarBean);
    // } catch (DateTimeParseException e) {
    // System.err.println("日期格式錯誤: " + calendarData.get("date"));
    // e.printStackTrace();
    // } catch (Exception e) {
    // System.err.println("資料處理錯誤: " + calendarData);
    // e.printStackTrace();
    // }
    // }
    // }
}