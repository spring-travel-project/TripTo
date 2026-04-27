package com.tripto.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.tripto.dto.MainRecommendDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MainRecommendDAO {

    private final SqlSessionTemplate template;

    public int isRecommended(Map<String, Object> map) {
        return template.selectOne("mainRecommend.isRecommended", map);
    }

    public int existsRecommend(Map<String, Object> map) {
        return template.selectOne("mainRecommend.existsRecommend", map);
    }

    public int addRecommend(Map<String, Object> map) {
        return template.insert("mainRecommend.addRecommend", map);
    }

    public int activeRecommend(Map<String, Object> map) {
        return template.update("mainRecommend.activeRecommend", map);
    }

    public int removeRecommend(Map<String, Object> map) {
        return template.update("mainRecommend.removeRecommend", map);
    }

    public List<MainRecommendDTO> mainList() {
        return template.selectList("mainRecommend.mainList");
    }
}