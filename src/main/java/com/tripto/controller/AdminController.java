package com.tripto.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tripto.dto.MemberDTO;
import com.tripto.service.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/main")
    public String main(Model model) {
        // DB���� �ǽð� ������ ����
        Map<String, Object> stats = adminService.getDashboardStats();
        
        // �����Ͱ� �� �Դ��� �ֿܼ��� Ȯ�� (���ڰ� 0�̸� DB�� �����Ͱ� ���� ��!)
        System.out.println("DEBUG: DB Stats Data -> " + stats);
        
        model.addAttribute("stats", stats);
        return "admin/main";
    }
    
    @GetMapping("/memberList")
    public String memberList(Model model, 
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(required = false) String searchType,
                             @RequestParam(required = false) String searchKeyword) {
        
        int pageSize = 10;
        int start = (page - 1) * pageSize + 1;
        int end = page * pageSize;

        Map<String, Object> map = new HashMap<>();
        map.put("start", start);
        map.put("end", end);
        map.put("searchType", searchType);
        map.put("searchKeyword", searchKeyword);

        List<MemberDTO> list = adminService.getMemberList(map);
        int totalCount = adminService.getMemberCount(map);
        int totalPage = (int)Math.ceil((double)totalCount / pageSize);

        // 검색 상태 유지를 위해 다시 모델에 전송
        model.addAttribute("list", list);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);

        return "admin/memberList";
    }
}