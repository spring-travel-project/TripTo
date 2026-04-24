package com.tripto.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.MemberDTO;

@Repository
public class AdminDAO {

    @Autowired
    private SqlSessionTemplate sql;

    // 대시보드 통계용 (기존)
    public Map<String, Object> getStats() {
        return sql.selectOne("admin.stats");
    }

    // 👤 회원 목록 조회 (새로 추가)
    public List<MemberDTO> memberList(Map<String, Object> map) {
        return sql.selectList("admin.memberList", map);
    }

    //	👤 전체 회원 수 (페이징용 - 새로 추가)
    public int memberCount(Map<String, Object> map) {
        return sql.selectOne("admin.memberCount", map);
    }

    // 특정 회원 상세 정보
    public MemberDTO memberDetail(String seqMember) {
        return sql.selectOne("admin.memberDetail", seqMember);
    }
    
    //  회원 강제 탈퇴 (DELETE -> UPDATE 로 변경 완료)
    public void memberDelete(String seqMember) {
        sql.update("admin.memberDelete", seqMember);
    }
}