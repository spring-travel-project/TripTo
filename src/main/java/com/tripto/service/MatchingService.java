package com.tripto.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripto.dao.MatchingDAO;
import com.tripto.dto.MatchDTO;

@Service
public class MatchingService {

	private final MatchingDAO matchingDAO;

	// ������ ����
	public MatchingService(MatchingDAO matchingDAO) {
		this.matchingDAO = matchingDAO;
	}

	// 1. �� ������ ���� �������� (MBTI �� ���� ť���̼ǿ�)
	public MatchDTO getMyProfile(Integer loginSeq) {
		return matchingDAO.getMyProfile(loginSeq);
	}

	// 2. ��Ī�� ȸ�� ��� �������� (���� ���� �� �α��� ���� ���� ����)
	public List<MatchDTO> getMatchingList(Map<String, Object> params) {
		return matchingDAO.getMatchingList(params);
	}

	// 3. �� ������ �������� (���� Ŭ���� �޼���)
	public MatchDTO getMatchDetail(Map<String, Integer> map) {
		return matchingDAO.getMatchDetail(map);
	}

	// ==========================================
	// 여기서부터 프로필 편집(작성/수정)용 추가 메서드
	// ==========================================

	@Transactional // 여러 테이블을 건드리므로 트랜잭션 처리
	public void saveMyProfile(MatchDTO profile, List<Integer> stays, List<Integer> langs, List<Integer> ages) {

		// 1. 메인 프로필 저장 (MERGE)
		matchingDAO.upsertProfile(profile);

		// 2. 다중 선택 시 기존 데이터 지우기 (초기화)
		int seqMember = profile.getSeqMember();
		matchingDAO.deleteProfileStay(seqMember);
		matchingDAO.deleteProfileLanguage(seqMember);
		matchingDAO.deleteProfileAgeGroup(seqMember);

		// 3. 체크박스 선택한 항목들 새로 넣기 (반복문)
		if (stays != null && !stays.isEmpty()) {
			for (int seq : stays) {
				Map<String, Integer> map = new HashMap<>();
				map.put("seqMember", seqMember);
				map.put("seqTarget", seq);
				matchingDAO.insertProfileStay(map);
			}
		}
		if (langs != null && !langs.isEmpty()) {
			for (int seq : langs) {
				Map<String, Integer> map = new HashMap<>();
				map.put("seqMember", seqMember);
				map.put("seqTarget", seq);
				matchingDAO.insertProfileLanguage(map);
			}
		}
		if (ages != null && !ages.isEmpty()) {
			for (int seq : ages) {
				Map<String, Integer> map = new HashMap<>();
				map.put("seqMember", seqMember);
				map.put("seqTarget", seq);
				matchingDAO.insertProfileAgeGroup(map);
			}
		}
	}

}