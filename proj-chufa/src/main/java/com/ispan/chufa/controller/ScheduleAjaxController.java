//package com.ispan.chufa.controller;
//
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ispan.chufa.domain.ScheduleBean;
//import com.ispan.chufa.service.ScheduleService;
//
//@RestController
//
//
//public class ScheduleAjaxController {
//
//
//	    @Autowired
//	    private ScheduleService scheduleService;
//
//	    @PostMapping("/ajax/schedule/create")
//	    public String createSchedule(@RequestBody String entity) {
//	        JSONObject responseJson = new JSONObject();
//
//	        // 解析傳入的 JSON 資料
//	        JSONObject obj = new JSONObject(entity);
//	        String coverPhoto = obj.optString("coverPhoto", null);
//	        String tripName = obj.optString("tripName", null);
//	        String startDateStr = obj.optString("startDate", null);
//	        String endDateStr = obj.optString("endDate", null);
//	        Long userId = obj.optLong("userId", 0);
//
//	        // 驗證輸入資料
//	        if (tripName == null || tripName.isEmpty() ||
//	            startDateStr == null || startDateStr.isEmpty() ||
//	            endDateStr == null || endDateStr.isEmpty() ||
//	            userId == 0) {
//	            responseJson.put("success", false);
//	            responseJson.put("message", "請填寫所有必填字段");
//	            return responseJson.toString();
//	        }
//
//	        // 轉換日期
//	        LocalDate startDate = LocalDate.parse(startDateStr);
//	        LocalDate endDate = LocalDate.parse(endDateStr);
//
//	        // 創建 ScheduleBean 實例
//	        ScheduleBean schedule = new ScheduleBean(coverPhoto, tripName, startDate, endDate, new MemberBean(userId));
//
//	        // 使用服務保存行程
//	        ScheduleBean savedSchedule = scheduleService.saveSchedule(schedule);
//
//	        // 根據結果準備回應
//	        if (savedSchedule != null) {
//	            responseJson.put("success", true);
//	            responseJson.put("message", "行程創建成功");
//	            responseJson.put("tripId", savedSchedule.getTripId());
//	        } else {
//	            responseJson.put("success", false);
//	            responseJson.put("message", "行程創建失敗");
//	        }
//	        return responseJson.toString();
//	    }
//	}
//
//}
