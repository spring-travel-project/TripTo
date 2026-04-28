package com.tripto.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tripto.dao.MemberDAO;
import com.tripto.dto.MemberDTO;

@Service
public class MemberService {

	@Autowired
	private MemberDAO dao;

	// 회원가입 시 아이디 중복 확인
	public int checkId(String id) {
		return dao.checkId(id);
	}

	// 회원가입 시 이메일 중복 확인
	public int checkEmail(String email) {
		return dao.checkEmail(email);
	}

	// 회원가입 폼 제출
	public void joinMember(MemberDTO dto) {
		dao.joinMember(dto);
	}

	// 아이디 찾기
	public String findIdByNameAndEmail(Map<String, String> map) {
		// 컨트롤러에서 넘어온 map(이름, 이메일)을 그대로 DAO에게 넘겨주고,
		// DAO가 찾아온 아이디(String)를 다시 컨트롤러로 반환(return)
		return dao.findIdByNameAndEmail(map);
	}

	// 비밀번호 찾기
	public int checkIdAndEmail(Map<String, String> map) {
		return dao.checkIdAndEmail(map);
	}

	// 비밀번호 재설정
	public void updatePw(Map<String, String> map) {

		dao.updatePw(map);
	}

	// 마이페이지에 현재 로그인한 회원의 정보를 출력
	public MemberDTO getMemberById(String id) {

		return dao.getMemberById(id);
	}

	// 현재 비밀번호 가져오기
	public String getCurrentPw(String pw) {

		return dao.getCurrentPw(pw);
	}

	// 계정 탈퇴 (비식별화) - 리턴 값이 필요 없으므로 void로 처리
	public void deactivateMember(String name) {

		dao.deactivateMember(name);
	}
	
	// 마이페이지 -> 내 정보 설정 -> 닉네임 중복확인
	public int checkNickname(String nickname) {
		return dao.checkNickname(nickname);
	}
	
	// 마이페이지 -> 내 정보 설정 -> 회원 정보 업데이트
	public void updateMemberInfo(MemberDTO dto) {
		dao.updateMemberInfo(dto);
	}
}