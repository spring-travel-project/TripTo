package com.tripto.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tripto.dao.MainRecommendDAO;
import com.tripto.dto.MainRecommendDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainRecommendService {

    private final MainRecommendDAO dao;

    public boolean isRecommended(String targetType, int seqTarget) {
        Map<String, Object> map = new HashMap<>();
        map.put("targetType", targetType);
        map.put("seqTarget", seqTarget);

        return dao.isRecommended(map) > 0;
    }

    public void addRecommend(String targetType, int seqTarget) {
        Map<String, Object> map = new HashMap<>();
        map.put("targetType", targetType);
        map.put("seqTarget", seqTarget);

        if (dao.existsRecommend(map) > 0) {
            dao.activeRecommend(map);
        } else {
            dao.addRecommend(map);
        }
    }

    public void removeRecommend(String targetType, int seqTarget) {
        Map<String, Object> map = new HashMap<>();
        map.put("targetType", targetType);
        map.put("seqTarget", seqTarget);

        dao.removeRecommend(map);
    }

    public List<MainRecommendDTO> mainList() {
        return dao.mainList();
    }
}