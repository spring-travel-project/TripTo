package com.tripto.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.LocationDTO;
import com.tripto.dto.TravelPostDTO;
import com.tripto.dto.TravelPostFileDTO;

// DAO 클래스임을 Spring에게 알려주는 어노테이션
// DB 접근을 담당하는 객체로 등록된다.
@Repository
public class TravelPostDAO {

    // MyBatis를 사용해서 SQL Mapper XML에 있는 쿼리를 실행하는 객체
    // travelPost.xml 같은 mapper 파일의 SQL id를 호출할 때 사용한다.
    @Autowired
    private SqlSessionTemplate template;

    // 여행 게시글 전체 개수를 조회하는 메서드
    // 검색어, 카테고리 같은 조건이 dto에 담겨 있으면 조건에 맞는 개수를 조회한다.
    public int getTotalCount(TravelPostDTO dto) {

        // travelPost.getTotalCount 쿼리를 실행하고 결과 한 개를 int로 반환한다.
        return template.selectOne("travelPost.getTotalCount", dto);
    }

    // 여행 게시글 목록을 조회하는 메서드
    // dto 안에는 카테고리, 검색어, 페이징 begin/end 값 등이 들어갈 수 있다.
    public List<TravelPostDTO> list(TravelPostDTO dto) {

        // travelPost.list 쿼리를 실행하고 게시글 목록을 List로 반환한다.
        return template.selectList("travelPost.list", dto);
    }

    // 여행 게시글을 등록하는 메서드
    // dto에는 제목, 내용, 작성자 번호, 카테고리, 썸네일 등의 게시글 정보가 들어간다.
    public int add(TravelPostDTO dto) {

        // travelPost.add 쿼리를 실행한다.
        // insert 성공 시 보통 1이 반환된다.
        return template.insert("travelPost.add", dto);
    }

    // 방금 등록된 게시글의 현재 시퀀스 번호를 가져오는 메서드
    // 게시글 등록 후 첨부파일을 연결할 때 게시글 번호가 필요해서 사용한다.
    public int getCurrentSeq() {

        // travelPost.getCurrentSeq 쿼리를 실행하고 현재 게시글 번호를 반환한다.
        return template.selectOne("travelPost.getCurrentSeq");
    }

    // 여행 게시글에 첨부파일 정보를 등록하는 메서드
    // dto에는 파일명, 저장 파일명, 게시글 번호 등의 파일 정보가 들어간다.
    public int addFile(TravelPostFileDTO dto) {

        // travelPost.addFile 쿼리를 실행한다.
        // insert 성공 시 보통 1이 반환된다.
        return template.insert("travelPost.addFile", dto);
    }

    // 특정 여행 게시글 한 개를 조회하는 메서드
    // seqTravelPost는 조회할 게시글 번호이다.
    public TravelPostDTO get(int seqTravelPost) {

        // travelPost.get 쿼리를 실행하고 게시글 DTO 한 개를 반환한다.
        return template.selectOne("travelPost.get", seqTravelPost);
    }

    // 특정 여행 게시글에 첨부된 파일 목록을 조회하는 메서드
    // seqTravelPost에 해당하는 게시글의 파일들만 가져온다.
    public List<TravelPostFileDTO> fileList(int seqTravelPost) {

        // travelPost.fileList 쿼리를 실행하고 파일 목록을 List로 반환한다.
        return template.selectList("travelPost.fileList", seqTravelPost);
    }

    // 특정 게시글의 조회수를 증가시키는 메서드
    // 상세보기 화면에 들어갈 때 호출될 수 있다.
    public int increaseViewCount(int seqTravelPost) {

        // travelPost.increaseViewCount 쿼리를 실행한다.
        // update 성공 시 보통 1이 반환된다.
        return template.update("travelPost.increaseViewCount", seqTravelPost);
    }

    // 여행 게시글 내용을 수정하는 메서드
    // dto에는 수정할 게시글 번호와 수정된 제목, 내용, 카테고리 등의 값이 들어간다.
    public int edit(TravelPostDTO dto) {

        // travelPost.edit 쿼리를 실행한다.
        // update 성공 시 보통 1이 반환된다.
        return template.update("travelPost.edit", dto);
    }

    // 특정 게시글에 연결된 첨부파일 정보를 삭제하는 메서드
    // 게시글 수정 시 기존 파일을 다시 정리하거나, 게시글 삭제 시 파일 정보를 지울 때 사용할 수 있다.
    public int deleteFiles(int seqTravelPost) {

        // travelPost.deleteFiles 쿼리를 실행한다.
        // delete 성공 시 삭제된 행 개수가 반환된다.
        return template.delete("travelPost.deleteFiles", seqTravelPost);
    }

    // 특정 여행 게시글을 삭제 처리하는 메서드
    // 실제 DELETE가 아니라 status 값을 변경하는 방식일 가능성이 높다.
    public int deletePost(int seqTravelPost) {

        // travelPost.deletePost 쿼리를 실행한다.
        // update 성공 시 보통 1이 반환된다.
        return template.update("travelPost.deletePost", seqTravelPost);
    }

    // 특정 게시글의 작성자 회원 번호를 조회하는 메서드
    // 수정/삭제 권한 확인에 사용할 수 있다.
    public Integer getWriterSeq(int seqTravelPost) {

        // travelPost.getWriterSeq 쿼리를 실행하고 작성자 회원 번호를 반환한다.
        return template.selectOne("travelPost.getWriterSeq", seqTravelPost);
    }
    
    // 특정 여행 게시글에 연결된 장소 목록을 조회하는 메서드
    // 지도에 표시할 장소 정보나 상세보기의 장소 목록에 사용할 수 있다.
    public List<LocationDTO> locationListByTravelPost(int seqTravelPost) {

        // travelPost.locationListByTravelPost 쿼리를 실행하고 장소 목록을 List로 반환한다.
        return template.selectList("travelPost.locationListByTravelPost", seqTravelPost);
    }
}