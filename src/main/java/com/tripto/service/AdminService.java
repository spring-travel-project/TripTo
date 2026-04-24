package com.tripto.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tripto.dao.AdminDAO;
import com.tripto.dto.MemberDTO;

@Service
public class AdminService {

    @Autowired
    private AdminDAO dao;

    // 대시보드 데이터 (기존)
    public Map<String, Object> getDashboardStats() {
        return dao.getStats();
    }

    // 👤 회원 관리 목록 가져오기
    public List<MemberDTO> getMemberList(Map<String, Object> map) {
        return dao.memberList(map);
    }

    // 👤 페이징 처리를 위한 총 회원 수
    public int getMemberCount(Map<String, Object> map) {
        return dao.memberCount(map);
    }
}
	

