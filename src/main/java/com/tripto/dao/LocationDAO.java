package com.tripto.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.LocationDTO;

@Repository
public class LocationDAO {

    @Autowired
    private SqlSessionTemplate template;

    public int add(LocationDTO dto) {
        return template.insert("location.add", dto);
    }

    public LocationDTO get(int seqLocation) {
        return template.selectOne("location.get", seqLocation);
    }
}