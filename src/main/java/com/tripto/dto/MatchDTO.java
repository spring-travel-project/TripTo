package com.tripto.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MatchDTO {
    // 1. match 테이블 기본 필드
    private int seqMatch;			//
    private int seqMember;        	// 신청자
    private int seqMatchedUser;   	// 매칭 대상자
    private String matchMessage;
    private String createDate;

    // 2. 매칭 알고리즘 및 출력용 필드
    private int matchCount;       // 8개 항목 중 일치하는 개수 (0~8)
    
    // 3. 상대방 프로필 정보
    private String nickname;		// 닉네임
    private String pic;       		// 프로필 사진
    private int gender;       		// 남성(0)/여성(1)
    private int age;          		// 나이
    private String mbti;			// MBTI
    private int smoking;			// 흡연(0)/비흡연(1)
    private int drinking;			// 음주 자주(0)/음주 가끔(1)/음주 안함(2)
    private int travelType;			// 5천보 이하(0)/5천보 이상 1만보 이하(1), 1만보 이상(2)
    private int stepCount;			// 정적인(0)/무관함(1)/활동적인(2)
    private String intro;			// 자기소개

    // 4. 다중 선택 항목 (LISTAGG로 합쳐진 결과)
    private String stayNames;      	// 선호하는 숙소 
    private String languageNames;  	// 사용가능 언어
    private String ageGroupNames;  	// 선호 동행 나이
}