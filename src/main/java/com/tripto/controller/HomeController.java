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
        
        // ==========================================================
        // [��� 1] �÷��ֽ� DB�� ������ �����ϴ� 12�� �Խñ��� �������� ����
        // ==========================================================
        int pickSeq = 12; 
        BoardPostDTO featuredPost = boardPostService.get(pickSeq, false);
        

        //  ���� ó��: ������ �Խñ۰� ������ ȭ��(JSP)���� �Ѱ��ݴϴ�.
        if (featuredPost != null) {
            model.addAttribute("featuredPost", featuredPost);
            
            // ÷������ ����Ʈ�� �����ϸ�, ù ��° ������ ����� ���ϸ��� �ѱ� (DB�� SAVEDNAME)
            if (featuredPost.getFileList() != null && !featuredPost.getFileList().isEmpty()) {
                model.addAttribute("mainImage", featuredPost.getFileList().get(0).getSavedName());
            }
        }
        
        return "index/index"; 
    }
}