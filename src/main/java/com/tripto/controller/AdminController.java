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
                             @RequestParam(required = false) String searchKeyword,
                             
                             // 🚨🚨 바로 이 줄이 빠져서 밑줄이 생기는 겁니다! 🚨🚨
                             @RequestParam(required = false) String showDeleted) { 
        
        int pageSize = 10;
        int start = (page - 1) * pageSize + 1;
        int end = page * pageSize;

        Map<String, Object> map = new HashMap<>();
        map.put("start", start);
        map.put("end", end);
        map.put("searchType", searchType);
        map.put("searchKeyword", searchKeyword);
        
        // 🌟 위에서 파라미터로 받았기 때문에 이제 여기서 빨간 밑줄이 사라집니다!
        map.put("showDeleted", showDeleted); 

        // 회원 목록 및 전체 수 가져오기
        List<MemberDTO> list = adminService.getMemberList(map);
        int totalCount = adminService.getMemberCount(map);
        int totalPage = (int)Math.ceil((double)totalCount / pageSize);

        // 모델에 담아서 JSP로 보내기
        model.addAttribute("list", list);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        
        // 🌟 체크박스 상태 유지를 위해 JSP로 다시 보내줌
        model.addAttribute("showDeleted", showDeleted); 

        return "admin/memberList";
    }
    
 // 1. 상세 페이지 (기존 메서드에 확인용 로그 추가)
    @GetMapping("/memberDetail")
    public String memberDetail(Model model, @RequestParam String seqMember) {
        MemberDTO member = adminService.getMemberDetail(seqMember);
        System.out.println("DEBUG 상세 데이터: " + member); // 콘솔에 데이터가 찍히는지 확인 필수!
        model.addAttribute("member", member);
        return "admin/memberDetail";
    }

    // 2. 회원 강제 탈퇴 (새로 추가)
    @GetMapping("/memberDelete")
    public String memberDelete(@RequestParam String seqMember) {
        // 삭제 실행
        adminService.deleteMember(seqMember);
        
        // 삭제 완료 후 다시 회원 목록 페이지로 이동
        return "redirect:/admin/memberList";
    }
    
    @GetMapping("/boardList")
    public String boardList(Model model, 
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(required = false) String searchType,
                            @RequestParam(required = false) String searchKeyword,
                            @RequestParam(required = false) String showDeleted) { // 🌟 파라미터 추가

        int pageSize = 10;
        int start = (page - 1) * pageSize + 1;
        int end = page * pageSize;

        Map<String, Object> map = new HashMap<>();
        map.put("start", start);
        map.put("end", end);
        map.put("searchType", searchType);
        map.put("searchKeyword", searchKeyword);
        map.put("showDeleted", showDeleted); // 🌟 맵에 담기

        List<Map<String, Object>> list = adminService.getBoardList(map);
        int totalCount = adminService.getBoardCount(map);
        int totalPage = (int)Math.ceil((double)totalCount / pageSize);

        model.addAttribute("list", list);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("showDeleted", showDeleted); // 🌟 상태 유지를 위해 JSP로 전송

        return "admin/boardList";
    }
    
    @GetMapping("/boardDelete")
    public String boardDelete(@RequestParam String seqBoardPost) {
        adminService.deleteBoard(seqBoardPost); // 서비스/DAO에 해당 메서드 추가 필요
        return "redirect:/admin/boardList";
    }
    
    @GetMapping("/companionList")
    public String companionList(Model model, 
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(required = false) String searchType,
                                @RequestParam(required = false) String searchKeyword,
                                @RequestParam(required = false) String showDeleted) {
        
        int pageSize = 10;
        int start = (page - 1) * pageSize + 1;
        int end = page * pageSize;

        Map<String, Object> map = new HashMap<>();
        map.put("start", start);
        map.put("end", end);
        map.put("searchType", searchType);
        map.put("searchKeyword", searchKeyword);
        map.put("showDeleted", showDeleted);

        List<Map<String, Object>> list = adminService.getCompanionList(map);
        int totalCount = adminService.getCompanionCount(map);
        int totalPage = (int)Math.ceil((double)totalCount / pageSize);

        model.addAttribute("list", list);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("showDeleted", showDeleted);

        return "admin/companionList";
    }

    @GetMapping("/companionDelete")
    public String companionDelete(@RequestParam String seqCompanionPost) {
        adminService.deleteCompanion(seqCompanionPost);
        return "redirect:/admin/companionList";
    }
}