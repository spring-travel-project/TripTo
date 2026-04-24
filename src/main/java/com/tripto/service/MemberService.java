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

	public String findIdByNameAndEmail(Map<String, String> map) {
		// 컨트롤러에서 넘어온 map(이름, 이메일)을 그대로 DAO에게 넘겨주고,
		// DAO가 찾아온 아이디(String)를 다시 컨트롤러로 반환(return)
		return dao.findIdByNameAndEmail(map);
	}

	public int checkIdAndEmail(Map<String, String> map) {
		return dao.checkIdAndEmail(map);
	}

	public void updatePw(Map<String, String> map) {
		
		dao.updatePw(map);
	}
}