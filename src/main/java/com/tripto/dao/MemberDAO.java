package com.tripto.dao;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.MemberDTO;

@Repository
public class MemberDAO {

	@Autowired
	private SqlSessionTemplate sql; // root-context.xml에서 세팅한 DB 통신 객체

	// 아이디 중복 확인
	public int checkId(String id) {
		// member라는 namespace를 가진 매퍼에서 checkId라는 쿼리를 실행하고, 파라미터로 id를 넘김
		return sql.selectOne("member.checkId", id);
	}

	// 이메일 중복 확인
	public int checkEmail(String email) {
		return sql.selectOne("member.checkEmail", email);
	}

	// 회원가입 폼 제출
	public void joinMember(MemberDTO dto) {
		sql.insert("member.joinMember", dto);
	}

	// 아이디 찾기
	public String findIdByNameAndEmail(Map<String, String> map) {
		// mapper에게 member.findIdByNameAndEmail 쿼리 실행하라고 지시, 
		// 쓸 데이터는 map이라고 넘김
		return sql.selectOne("member.findIdByNameAndEmail", map);
	}
}