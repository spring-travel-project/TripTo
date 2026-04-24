package com.tripto.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import com.tripto.dto.MatchDTO;

@Mapper // 스프링 설정에 따라 @Repository를 쓰거나 생략할 수도 있습니다.
public interface MatchingDAO {

    // 1. matching.xml의 <select id="getMyProfile"> 과 연결
    MatchDTO getMyProfile(Integer loginSeq);

    // 2. matching.xml의 <select id="getMatchingList"> 와 연결
    List<MatchDTO> getMatchingList(Map<String, Object> params);

	MatchDTO getMatchDetail(Map<String, Integer> map);
    

}