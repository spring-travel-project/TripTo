package com.tripto.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tripto.dao.MatchingDAO;
import com.tripto.dto.MatchDTO;

@Service
public class MatchingService {

    private final MatchingDAO matchingDAO;

    // ������ ����
    public MatchingService(MatchingDAO matchingDAO) {
        this.matchingDAO = matchingDAO;
    }

    // 1. �� ������ ���� �������� (MBTI �� ���� ť���̼ǿ�)
    public MatchDTO getMyProfile(Integer loginSeq) {
        return matchingDAO.getMyProfile(loginSeq);
    }

    // 2. ��Ī�� ȸ�� ��� �������� (���� ���� �� �α��� ���� ���� ����)
    public List<MatchDTO> getMatchingList(Map<String, Object> params) {
        return matchingDAO.getMatchingList(params);
    }
    
 // 3. �� ������ �������� (���� Ŭ���� �޼���)
    public MatchDTO getMatchDetail(Map<String, Integer> map) {
    return matchingDAO.getMatchDetail(map);
    }
    
}