package com.tripto.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tripto.dto.MainRecommendDTO;
import com.tripto.service.MainRecommendService;

@Controller
public class HomeController {

    private final MainRecommendService mainRecommendService;

    public HomeController(MainRecommendService mainRecommendService) {
        this.mainRecommendService = mainRecommendService;
    }

    @GetMapping(value = {"/", "/index.do"})
    public String index(Model model) {

        List<MainRecommendDTO> mainList = mainRecommendService.mainList();

        model.addAttribute("mainList", mainList);

        return "index/index";
    }
}