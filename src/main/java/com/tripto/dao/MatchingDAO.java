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
    

}