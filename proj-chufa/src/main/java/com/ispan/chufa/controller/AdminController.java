package com.ispan.chufa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.service.MemberService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MemberService memberService;

    // 顯示所有會員列表
    @GetMapping("/members")
    public String listMembers(Model model) {
        List<MemberBean> members = memberService.findAllMembers();
        model.addAttribute("members", members);
        return "/admin/members"; // 對應 admin/members.html 的管理頁面
    }

    // 顯示修改會員角色的表單，根據會員ID
    @GetMapping("/members/{userid}/edit")
    public String editMember(@PathVariable Long userid, Model model) {
        MemberBean member = memberService.findById(userid);
        model.addAttribute("member", member);
        model.addAttribute("roles", MemberBean.Role.values()); // 取得所有角色選項
        return "/admin/editMember"; // 對應 admin/editMember.html
    }

    // 處理表單送出，修改會員角色
    @PostMapping("/members/{userid}/updateRole")
    public String updateMemberRole(@PathVariable Long userid, @RequestParam("role") MemberBean.Role role) {
        MemberBean member = memberService.findById(userid);
        if (member != null) {
            member.setRole(role);
            memberService.saveMember(member);
        }
        return "redirect:/admin/members";
    }
}
