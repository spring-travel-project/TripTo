package com.tripto.service;

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
}