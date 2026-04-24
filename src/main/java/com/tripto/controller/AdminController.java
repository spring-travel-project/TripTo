package com.tripto.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tripto.service.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/main")
    public String main(Model model) {
        // DB에서 실시간 데이터 수신
        Map<String, Object> stats = adminService.getDashboardStats();
        
        // 데이터가 잘 왔는지 콘솔에서 확인 (숫자가 0이면 DB에 데이터가 없는 것!)
        System.out.println("DEBUG: DB Stats Data -> " + stats);
        
        model.addAttribute("stats", stats);
        return "admin/main";
    }
}