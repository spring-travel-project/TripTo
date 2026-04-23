package com.tripto.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.tripto.dao.MatchingDAO;
import com.tripto.dto.MatchDTO;

@Service
public class MatchingService {

    private final MatchingDAO matchingDAO;

    // 생성자 주입
    public MatchingService(MatchingDAO matchingDAO) {
        this.matchingDAO = matchingDAO;
    }

    // 1. 내 프로필 정보 가져오기 (MBTI 등 맞춤 큐레이션용)
    public MatchDTO getMyProfile(Integer loginSeq) {
        return matchingDAO.getMyProfile(loginSeq);
    }

    // 2. 매칭된 회원 목록 가져오기 (필터 조건 및 로그인 유저 정보 포함)
    public List<MatchDTO> getMatchingList(Map<String, Object> params) {
        return matchingDAO.getMatchingList(params);
    }
}