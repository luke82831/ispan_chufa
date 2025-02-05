package com.ispan.chufa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {
    @GetMapping("/favicon.ico")
    public void favicon() {
        // 返回空響應，避免 404 錯誤
    }
}
