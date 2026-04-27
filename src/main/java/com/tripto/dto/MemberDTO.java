package com.tripto.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
    private int seqMember;        // 회원 번호 (PK)
    private String name;          // 이름
    private String id;            // 아이디
    private String pw;            // 비밀번호 (시큐리티로 암호화됨, 영문, 숫자, 특수문자 포함 8자리 이상)
    private String pic;           // 프로필 사진 경로(프로필 사진 미첨부시 기본 프로필 사진(pic.png) 적용)
    private String nickname;      // 닉네임
    private String email;         // 이메일
    private int status;           // 상태 (0:정상, 1: 비밀번호 5회 이상 틀려서 잠김, 2:탈퇴)
    private int type;             // 회원 유형 (0:일반회원, 1:관리자)
    private int failCount;        // 로그인 실패 횟수
    private String regDate;       // 가입일 (Date 타입 대신 String으로 받으면 화면에 출력하기가 용이하다)
    private int gender;           // 성별 (0:남성, 1:여성)
    private String birth;         // 생년월일 (YYYY-MM-DD 형태의 문자열)
    private String region;        // 거주 국가
    private String intro;         // 자기소개
    
    private int reportCount;    // 신고 당한 횟수
    
    private String grade;
}