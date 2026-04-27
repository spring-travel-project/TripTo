package com.tripto.controller;

import java.util.List;

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
        
        // ==========================================================
        // [방법 1] 올려주신 DB에 실제로 존재하는 12번 게시글을 고정으로 띄우기
        // ==========================================================
        int pickSeq = 12; 
        BoardPostDTO featuredPost = boardPostService.get(pickSeq, false);
        

        //  공통 처리: 가져온 게시글과 사진을 화면(JSP)으로 넘겨줍니다.
        if (featuredPost != null) {
            model.addAttribute("featuredPost", featuredPost);
            
            // 첨부파일 리스트가 존재하면, 첫 번째 사진의 저장된 파일명을 넘김 (DB의 SAVEDNAME)
            if (featuredPost.getFileList() != null && !featuredPost.getFileList().isEmpty()) {
                model.addAttribute("mainImage", featuredPost.getFileList().get(0).getSavedName());
            }
        }
        
        return "index/index"; 
    }
}