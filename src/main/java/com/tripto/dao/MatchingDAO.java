package com.tripto.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.tripto.dto.MatchDTO;

@Mapper // ������ ������ ���� @Repository�� ���ų� ������ ���� �ֽ��ϴ�.
public interface MatchingDAO {

    // 1. matching.xml�� <select id="getMyProfile"> �� ����
    MatchDTO getMyProfile(Integer loginSeq);

    // 2. matching.xml�� <select id="getMatchingList"> �� ����
    List<MatchDTO> getMatchingList(Map<String, Object> params);

	MatchDTO getMatchDetail(Map<String, Integer> map);
	
	// ==========================================
    // 여기서부터 프로필 편집(작성/수정)용 추가 메서드
    // ==========================================
    
    // 1. 프로필 작성/수정 (MERGE INTO)
    void upsertProfile(MatchDTO dto);
    
    // 2. 기존 다중 선택 데이터 삭제
    void deleteProfileStay(int seqMember);
    void deleteProfileLanguage(int seqMember);
    void deleteProfileAgeGroup(int seqMember);
    
    // 3. 새로운 다중 선택 데이터 삽입
    void insertProfileStay(Map<String, Integer> map);
    void insertProfileLanguage(Map<String, Integer> map);
    void insertProfileAgeGroup(Map<String, Integer> map);
    
}