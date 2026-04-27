package com.tripto.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.LocationDTO;
import com.tripto.dto.TravelPostDTO;
import com.tripto.dto.TravelPostFileDTO;

@Repository
public class TravelPostDAO {

    @Autowired
    private SqlSessionTemplate template;

    public int getTotalCount(TravelPostDTO dto) {
        return template.selectOne("travelPost.getTotalCount", dto);
    }

    public List<TravelPostDTO> list(TravelPostDTO dto) {
        return template.selectList("travelPost.list", dto);
    }

    public int add(TravelPostDTO dto) {
        return template.insert("travelPost.add", dto);
    }

    public int getCurrentSeq() {
        return template.selectOne("travelPost.getCurrentSeq");
    }

    public int addFile(TravelPostFileDTO dto) {
        return template.insert("travelPost.addFile", dto);
    }

    public TravelPostDTO get(int seqTravelPost) {
        return template.selectOne("travelPost.get", seqTravelPost);
    }

    public List<TravelPostFileDTO> fileList(int seqTravelPost) {
        return template.selectList("travelPost.fileList", seqTravelPost);
    }

    public int increaseViewCount(int seqTravelPost) {
        return template.update("travelPost.increaseViewCount", seqTravelPost);
    }

    public int edit(TravelPostDTO dto) {
        return template.update("travelPost.edit", dto);
    }

    public int deleteFiles(int seqTravelPost) {
        return template.delete("travelPost.deleteFiles", seqTravelPost);
    }

    public int deletePost(int seqTravelPost) {
        return template.update("travelPost.deletePost", seqTravelPost);
    }

    public Integer getWriterSeq(int seqTravelPost) {
        return template.selectOne("travelPost.getWriterSeq", seqTravelPost);
    }
    
    public List<LocationDTO> locationListByTravelPost(int seqTravelPost) {
        return template.selectList("travelPost.locationListByTravelPost", seqTravelPost);
    }
}