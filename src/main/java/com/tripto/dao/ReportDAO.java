package com.tripto.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.ReportDTO;

@Repository
public class ReportDAO {

    @Autowired
    private SqlSessionTemplate template;

    public int add(ReportDTO dto) {
        return template.insert("report.add", dto);
    }

    public int checkDuplicate(ReportDTO dto) {
        return template.selectOne("report.checkDuplicate", dto);
    }
}