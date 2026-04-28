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

	// 비밀번호 찾기
	public int checkIdAndEmail(Map<String, String> map) {

		return sql.selectOne("member.checkIdAndEmail", map);
	}

	public void updatePw(Map<String, String> map) {

		sql.update("member.updatePw", map);
	}

	// 현재 로그인한 회원의 정보를 마이페이지에 출력하기
	public MemberDTO getMemberById(String id) {

		return sql.selectOne("member.getMemberById", id);
	}

	// 현재 비밀번호 가져오기 (데이터 1개를 가져오므로 selectOne)
	public String getCurrentPw(String id) {
		return sql.selectOne("member.getCurrentPw", id);
	}

	// 계정 탈퇴 (데이터를 수정하므로 update)
	public void deactivateMember(String id) {
		sql.update("member.deactivateMember", id);
	}
	
	// 마이페이지 -> 내 정보 설정 -> 닉네임 중복확인
	public int checkNickname(String nickname) {
		return sql.selectOne("member.checkNickname", nickname);
	}
	
	// 마이페이지 -> 내 정보 설정 -> 회원 정보 업데이트
	public void updateMemberInfo(MemberDTO dto) {
		sql.update("member.updateMemberInfo", dto);
	}
}