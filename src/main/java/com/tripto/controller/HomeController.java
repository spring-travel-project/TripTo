package com.tripto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tripto.dto.BoardPostDTO;
import com.tripto.service.BoardPostService;

@Controller
public class HomeController {

    @Autowired
    private BoardPostService boardPostService;

    @GetMapping(value = {"/", "/index.do"})
    public String index(Model model) {
        
        // 1. 관리자가 선택한 게시글 번호 (지금은 임시로 50번 글이라고 가정)
        // 나중에는 DB(설정 테이블)에서 이 번호를 읽어오면 됩니다.
        int pickSeq = 50; 
        
        // 2. 짜두신 get() 메서드 활용 (조회수 증가는 false)
        BoardPostDTO featuredPost = boardPostService.get(pickSeq, false);
        
        if (featuredPost != null) {
            model.addAttribute("featuredPost", featuredPost);
            
            // 3. 사진만 쏙 빼오기! (첨부파일이 있다면 첫 번째 사진의 저장된 이름을 넘김)
            if (featuredPost.getFileList() != null && !featuredPost.getFileList().isEmpty()) {
                model.addAttribute("mainImage", featuredPost.getFileList().get(0).getSavedName());
            }
        }
        
        return "index/index"; 
    }
}



