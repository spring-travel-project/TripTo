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

	// 특정 회원 상세 정보
	public MemberDTO getMemberDetail(String seqMember) {
	    return dao.memberDetail(seqMember);
	}
	
	// 🌟 회원 강제 탈퇴 (상태값 변경)
    public void deleteMember(String seqMember) {
        dao.memberDelete(seqMember);
    }
    
    public List<Map<String, Object>> getBoardList(Map<String, Object> map) {
        return dao.boardList(map);
    }

    public int getBoardCount(Map<String, Object> map) {
        return dao.boardCount(map);
    }

    // 몇 개의 행이 수정됐는지 숫자로 받고 싶을 때
    public int deleteBoard(String seqBoardPost) {
        return dao.boardDelete(seqBoardPost); // 이때는 return을 쓰는 게 맞아요!
    }
    
    public List<Map<String, Object>> getCompanionList(Map<String, Object> map) {
        return dao.companionList(map);
    }

    public int getCompanionCount(Map<String, Object> map) {
        return dao.companionCount(map);
    }

    public int deleteCompanion(String seqCompanionPost) {
        return dao.companionDelete(seqCompanionPost);
    }
}
	

